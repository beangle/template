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
package org.beangle.template.freemarker

import freemarker.cache.TemplateLoader
import org.beangle.commons.lang.Strings
import org.beangle.commons.net.http.HttpUtils

import java.io.{IOException, InputStreamReader}
import java.net.URL

class HttpTemplateLoader(val pattern: String) extends TemplateLoader {

  @throws[IOException]
  override def findTemplateSource(name: String): Any = {
    val url = getURL(name)
    val status = HttpUtils.access(url)
    if (status.isOk) new URLTemplateSource(url) else null
  }

  override def getLastModified(templateSource: Any): Long = {
    templateSource.asInstanceOf[URLTemplateSource].lastModified
  }

  @throws[IOException]
  override def getReader(templateSource: Any, encoding: String) = {
    new InputStreamReader(templateSource.asInstanceOf[URLTemplateSource].getInputStream, encoding)
  }

  @throws[IOException]
  override def closeTemplateSource(templateSource: Any): Unit = {
    templateSource.asInstanceOf[URLTemplateSource].close()
  }

  protected def getURL(name: String): URL = {
    new URL(Strings.replace(pattern, "{path}", name))
  }
}
