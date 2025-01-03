/*
 * Copyright (C) 2005, The Beangle Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.beangle.template.freemarker

import freemarker.cache.{FileTemplateLoader, MultiTemplateLoader, TemplateLoader}
import freemarker.template.{Configuration, ObjectWrapper, SimpleHash}
import org.beangle.commons.bean.Initializing
import org.beangle.commons.io.IOs
import org.beangle.commons.lang.ClassLoaders
import org.beangle.commons.lang.Strings.{split, substringAfter}
import org.beangle.commons.lang.annotation.description
import org.beangle.template.api.DynaProfile

import java.io.{File, IOException, StringWriter}

object Configurator {

  //must before configuration init
  //disable freemarker logging
  System.setProperty(freemarker.log.Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY, freemarker.log.Logger.LIBRARY_NAME_NONE)

  def newConfig: Configuration = {
    val cfg = new Configurator
    cfg.init()
    cfg.config
  }

  def newConfig(templatePath: String): Configuration = {
    val cfg = new Configurator
    cfg.templatePath = templatePath
    cfg.init()
    cfg.config
  }
}

@description("Freemarker配置提供者")
class Configurator extends Initializing {

  val config = new Configuration(Configuration.VERSION_2_3_33)

  var contentType: String = _

  var devMode = false

  var templatePath: String = _

  override def init(): Unit = {
    config.setDefaultEncoding("UTF-8")
    config.setLocalizedLookup(false)
    config.setWhitespaceStripping(true)
    config.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX)
    // Disable auto imports and includes
    config.setAutoImports(new java.util.HashMap(0))
    config.setAutoIncludes(new java.util.ArrayList(0))

    val props = properties
    for ((key, value) <- props) {
      if (null != key && null != value) config.setSetting(key, value)
    }

    config.setObjectWrapper(createObjectWrapper(props))
    config.setTemplateLoader(createTemplateLoader(props))

    config.setSharedVariable("include_optional", new IncludeOptionalModel)
    var content_type = config.getCustomAttribute("content_type").asInstanceOf[String]
    if (null == content_type) content_type = "text/html"
    if (!content_type.contains("charset"))
      content_type += "; charset=" + config.getDefaultEncoding
    contentType = content_type
  }

  protected def detectTemplatePath(props: Map[String, String]): String = {
    if (null == templatePath) templatePath = props.getOrElse("template_path", "class://")
    templatePath
  }

  /** The default template loader is a MultiTemplateLoader which includes
   * BeangleClassTemplateLoader(classpath:) and a WebappTemplateLoader
   * (webapp:) and FileTemplateLoader(file:) . All template path described
   * in init parameter templatePath or TemplatePlath
   * <p/>
   * The ClassTemplateLoader will resolve fully qualified template includes that begin with a slash.
   * for example /com/company/template/common.ftl
   * <p/>
   */
  def createTemplateLoader(props: Map[String, String]): TemplateLoader = {
    val paths = split(detectTemplatePath(props), ",")
    val loaders = new collection.mutable.ListBuffer[TemplateLoader]
    for (path <- paths) {
      loaders += buildLoader(path)
    }
    val loader = if (loaders.size == 1) loaders.head else new MultiTemplateLoader(loaders.toArray[TemplateLoader])
    ProfileTemplateLoader(loader)
  }

  def cleanProfile(): Unit = {
    DynaProfile.remove()
  }

  def buildLoader(path: String): TemplateLoader = {
    if (path.startsWith("class://")) {
      new ClassTemplateLoader(substringAfter(path, "class://"))
    } else if (path.startsWith("file://")) {
      try {
        new FileTemplateLoader(new File(substringAfter(path, "file://")))
      } catch {
        case e: IOException => throw new RuntimeException("templatePath: " + path + " cannot be accessed", e)
      }
    } else if (path.startsWith("http://") || path.startsWith("https://")) {
      new HttpTemplateLoader(path, !devMode)
    } else {
      throw new RuntimeException("templatePath: " + path
        + " is not well-formed. Use [class://|file://] seperated with ,")
    }
  }

  def createObjectWrapper(props: Map[String, String]): ObjectWrapper = {
    val wrapper = new BeangleObjectWrapper()
    wrapper.setUseCache(false)
    wrapper
  }

  /**
   * Load the multi settings from the /META-INF/freemarker.properties and
   * /freemarker.properties file on the classpath
   *
   * @see freemarker.template.Configuration#setSettings for the definition of valid settings
   */
  def properties: Map[String, String] = {
    val properties = new collection.mutable.HashMap[String, String]
    // 1. first META-INF/freemarker.properties
    for (url <- ClassLoaders.getResources("META-INF/freemarker.properties"))
      properties ++= IOs.readJavaProperties(url)

    // 2. second global freemarker.properties
    for (url <- ClassLoaders.getResources("freemarker.properties"))
      properties ++= IOs.readJavaProperties(url)

    // 3. system properties
    val sysProps = System.getProperties
    val sysKeys = sysProps.propertyNames
    while (sysKeys.hasMoreElements) {
      val key = sysKeys.nextElement.asInstanceOf[String]
      val value: String = sysProps.getProperty(key)
      if (key.startsWith("freemarker.")) properties.put(substringAfter(key, "freemarker."), value)
    }
    if (devMode) {
      properties.put(Configuration.TEMPLATE_UPDATE_DELAY_KEY, "0")
      properties.put("template_exception_handler", "html_debug")
    } else {
      properties.put("template_exception_handler", "rethrow")
    }
    properties.toMap
  }

  def render(url: String, datas: collection.Map[String, Any]): String = {
    val model = new SimpleHash(this.config.getObjectWrapper)
    datas foreach { case (k, v) => model.put(k, v) }
    val buf = new StringWriter(512)
    val template = this.config.getTemplate(url)
    template.process(model, buf)
    buf.toString
  }
}
