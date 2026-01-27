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

import freemarker.cache.TemplateLoader
import org.beangle.commons.lang.Strings
import org.beangle.commons.net.Networks
import org.beangle.commons.net.http.HttpUtils

import java.io.{IOException, InputStreamReader, Reader}

class HttpTemplateLoader(val pattern: String, preload: Boolean) extends TemplateLoader {
  private var files: Set[String] = Set.empty

  if (preload) {
    loadList()
  }

  private def loadList(): Unit = {
    val url = getURL("ls")
    val res = HttpUtils.get(url)
    if (res.isOk) {
      files = Strings.split(res.getText).toSet
    }
  }

  @throws[IOException]
  override def findTemplateSource(name: String): Any = {
    if (preload) {
      if (files.contains(name)) new URLTemplateSource(Networks.url(getURL(name))) else null
    } else {
      val url = getURL(name)
      if (HttpUtils.isAlive(url)) new URLTemplateSource(Networks.url(url)) else null
    }
  }

  override def getLastModified(templateSource: Any): Long = {
    templateSource.asInstanceOf[URLTemplateSource].lastModified
  }

  @throws[IOException]
  override def getReader(templateSource: Any, encoding: String): Reader = {
    new InputStreamReader(templateSource.asInstanceOf[URLTemplateSource].getInputStream, encoding)
  }

  @throws[IOException]
  override def closeTemplateSource(templateSource: Any): Unit = {
    templateSource.asInstanceOf[URLTemplateSource].close()
  }

  /**
   * get url corresponding to name
   *
   * @param name shoudnot starts with /
   * @return
   */
  protected def getURL(name: String): String = {
    Strings.replace(pattern, "{path}", name)
  }
}
