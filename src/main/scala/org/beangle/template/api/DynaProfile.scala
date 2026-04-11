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

import org.beangle.commons.lang.{ScopedContext, Strings}

object DynaProfile {

  private val key = ScopedContext.Key[String]("beangle.template.profile")

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
      case null => ScopedContext.remove(key)
      case None => ScopedContext.remove(key)
      case name: String => if name.isBlank then ScopedContext.remove(key) else ScopedContext.put(key, process(name))
      case _ => ScopedContext.put(key, process(p.toString))
  }

  def remove(): Unit = {
    ScopedContext.remove(key)
  }

  def get: Option[String] = {
    ScopedContext.get(key)
  }

  def concat(prefix: String, p: String): String = {
    if (prefix == null) {
      p
    } else {
      if p.charAt(0) == '/' then prefix + p.substring(1) else prefix + p
    }
  }

  def path(p: String): String = {
    get match {
      case None => p
      case Some(pf) => concat(pf, p)
    }
  }
}
