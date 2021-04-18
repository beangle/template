/*
 * Beangle, Agile Development Scaffold and Toolkits.
 *
 * Copyright © 2005, The Beangle Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.beangle.template.freemarker.web

import freemarker.cache.{MultiTemplateLoader, TemplateLoader}
import org.beangle.commons.lang.Strings.{split, substringAfter}
import org.beangle.commons.web.context.ServletContextHolder
import org.beangle.template.freemarker.Configurer

/**
 * @author chaostone
 */
class WebConfigurer extends Configurer {
  override def createTemplateLoader(props: Map[String, String]): TemplateLoader = {
    if (null == templatePath) {
      templatePath = ServletContextHolder.context.getInitParameter("templatePath")
    }
    if (null == templatePath) {
      templatePath = props.getOrElse("template_path", "class://")
    }
    if (null == templatePath) templatePath = "class://"
    val paths = split(templatePath, ",")
    val loaders = new collection.mutable.ListBuffer[TemplateLoader]
    for (path <- paths) {
      if (path.startsWith("webapp://")) {
        loaders += new WebappTemplateLoader(ServletContextHolder.context, substringAfter(path, "webapp://"))
      } else {
        loaders += super.buildLoader(path)
      }
    }
    new MultiTemplateLoader(loaders.toArray[TemplateLoader])
  }

}
