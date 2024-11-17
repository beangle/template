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

import freemarker.cache.URLTemplateLoader
import org.beangle.commons.lang.ClassLoaders
import org.beangle.template.api.DynaProfile

import java.net.URL

class ClassTemplateLoader(prefixStr: String = null) extends URLTemplateLoader {

  private val prefix = DynaProfile.process(prefixStr)

  protected def getURL(name: String): URL = {
    var url: Option[URL] = None
    if (prefix.nonEmpty) url = ClassLoaders.getResource(prefix + name)
    url match
      case None => ClassLoaders.getResource(name).orNull
      case Some(u) => u
  }

}
