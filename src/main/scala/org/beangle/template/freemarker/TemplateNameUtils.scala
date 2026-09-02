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

import freemarker.template.MalformedTemplateNameException

/** Guards template names against backing out from the template root directory, like FreeMarker 2.3.35+ loaders do. */
private[freemarker] object TemplateNameUtils {

  def checkInsideBaseDir(name: String): Unit = {
    if !isInsideBaseDir(name) then
      throw new MalformedTemplateNameException(name, "Backing out from the template root directory is not allowed")
  }

  private def isInsideBaseDir(name: String): Boolean = {
    var len = name.length
    while (len > 0 && Character.isWhitespace(name.charAt(len - 1))) len -= 1

    var level = 0
    var lastNameStart = 0
    var pos = 0
    while (pos < len + 1) {
      if (pos == len || isNameEnd(name.charAt(pos))) {
        val nameLength = pos - lastNameStart
        if (nameLength == 2 && name.charAt(lastNameStart) == '.' && name.charAt(lastNameStart + 1) == '.') {
          level -= 1
          if level < 0 then return false
        } else if !(nameLength == 0 || (nameLength == 1 && name.charAt(lastNameStart) == '.')) then
          level += 1
        lastNameStart = pos + 1
      }
      pos += 1
    }
    true
  }

  private def isNameEnd(c: Char): Boolean = c == '\\' || c == '/'
}
