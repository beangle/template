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
import org.beangle.template.api.DynaProfile

import java.io.Reader

class ProfileTemplateLoader(loader: TemplateLoader) extends TemplateLoader {

  override def findTemplateSource(name: String): AnyRef = {
    DynaProfile.get match
      case None => loader.findTemplateSource(name)
      case Some(p) =>
        val profiled = loader.findTemplateSource(p + name)
        if null == profiled then loader.findTemplateSource(name) else profiled
  }

  override def getLastModified(s: Any): Long = {
    loader.getLastModified(s)
  }

  override def closeTemplateSource(s: Any): Unit = loader.closeTemplateSource(s)

  override def getReader(s: Any, e: String): Reader = loader.getReader(s, e)
}
