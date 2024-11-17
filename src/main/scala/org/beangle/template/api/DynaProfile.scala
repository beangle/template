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

package org.beangle.template.api

import org.beangle.commons.lang.Strings

object DynaProfile {

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

  def set(p: Any): Unit = {
    p match
      case null => profile.remove()
      case None => profile.remove()
      case name: String => if name.isBlank then profile.remove() else profile.set(process(name))
      case _ => profile.set(process(p.toString))
  }

  def remove(): Unit = {
    profile.remove()
  }

  def get: Option[String] = {
    Option(profile.get())
  }
}
