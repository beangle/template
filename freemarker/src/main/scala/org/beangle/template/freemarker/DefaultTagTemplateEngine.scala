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

import freemarker.cache.StrongCacheStorage
import freemarker.template.{Configuration, SimpleHash}
import org.beangle.commons.bean.Initializing
import org.beangle.commons.io.IOs
import org.beangle.commons.lang.ClassLoaders
import org.beangle.commons.lang.annotation.description
import org.beangle.template.api.*
import org.beangle.template.freemarker.{AbstractTemplateEngine, IncludeIfExistsModel, PrefixClassTemplateLoader}

import java.io.Writer
import java.util as ju

/** Freemarker Template Engine
 *
 * <ul>
 * <li>User hashmodel store in request</li>
 * <li>Load hierarchical templates</li>
 * <li>Disabled freemarker localized lookup in template loading</li>
 * </ul>
 *
 * @author chaostone
 */
@description("Freemarker Tag 模板引擎")
class DefaultTagTemplateEngine(modelBuilder: ModelBuilder) extends AbstractTemplateEngine with TagTemplateEngine with Initializing {

  val config = new Configuration(Configuration.VERSION_2_3_30)

  var devMode: Boolean = false

  @throws(classOf[Exception])
  override def renderTo(template: String, component: Any, writer: Writer): Unit = {
    val model = modelBuilder.createModel(config).asInstanceOf[SimpleHash]
    val prevTag = model.get("tag")
    model.put("tag", component)
    getTemplate(config, template).process(model, writer)
    if (null != prevTag) model.put("tag", prevTag)
  }

  /**
   * Clone configuration from FreemarkerManager,but custmize in
   * <ul>
   * <li>Disable freemarker localized lookup
   * <li>Using tag.properties
   * <li>Disable auto imports and includes
   * </ul>
   */
  override def init(): Unit = {
    config.setEncoding(config.getLocale, "UTF-8")
    ClassLoaders.getResource("org/beangle/webmvc/freemarker/tag.properties") foreach { r =>
      IOs.readJavaProperties(r) foreach {
        case (k, v) => config.setSetting(k, v)
      }
    }
    //FIXME ContextObjectWrapper?
    val wrapper = new BeangleObjectWrapper()
    wrapper.setUseCache(false)
    config.setObjectWrapper(wrapper)
    config.setTemplateLoader(new ThemeTemplateLoader(new PrefixClassTemplateLoader()))

    if (devMode) config.setTemplateUpdateDelayMilliseconds(0)

    config.setCacheStorage(new StrongCacheStorage())
    config.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX)
    config.setSharedVariable("include_if_exists", new IncludeIfExistsModel)
    // Disable auto imports and includes
    config.setAutoImports(new ju.HashMap(0))
    config.setAutoIncludes(new ju.ArrayList(0))
  }

  override def newTag(context: ComponentContext, clazz: Class[_ <: Component]): Tag = {
    new TagModel(context, clazz)
  }
}
