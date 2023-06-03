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

import freemarker.cache.{MultiTemplateLoader, TemplateLoader, URLTemplateLoader}
import org.beangle.commons.lang.{ClassLoaders, Strings}
import org.beangle.template.freemarker.ProfileTemplateLoader.process

import java.io.Reader
import java.net.URL

object ProfileTemplateLoader {

  private val profile = new ThreadLocal[String]

  /** Not starts with /,but end with / */
  def process(pre: String): String = {
    if (Strings.isBlank(pre)) return ""
    var prefix = pre.trim()

    if prefix.equals("/") then ""
    else
      if (!prefix.endsWith("/")) prefix += "/"
      if (prefix.startsWith("/")) prefix = prefix.substring(1)
      prefix
  }

  def setProfile(p: Any): Unit = {
    p match
      case null => profile.remove()
      case None => profile.remove()
      case name: String => if name.isBlank then profile.remove() else profile.set(process(name))
      case _ => profile.set(process(p.toString))
  }

  def removeProfile(): Unit = {
    profile.remove()
  }
}

class ProfileTemplateLoader(loader: TemplateLoader) extends TemplateLoader {

  override def findTemplateSource(name: String): AnyRef = {
    val p = ProfileTemplateLoader.profile.get()
    if null == p then loader.findTemplateSource(name)
    else
      val profiled = loader.findTemplateSource(p + name)
      if null == profiled then loader.findTemplateSource(name) else profiled
  }

  override def getLastModified(s: Any): Long = {
    loader.getLastModified(s)
  }

  override def closeTemplateSource(s: Any): Unit = loader.closeTemplateSource(s)

  override def getReader(s: Any, e: String): Reader = loader.getReader(s, e)
}
