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

import org.beangle.commons.logging.Logging
import org.beangle.template.api.TemplateInterpreter

object DefaultTemplateInterpreter extends TemplateInterpreter, Logging {

  private val engine = DefaultTemplateEngine()

  override def process(contents: String, model: Any): String = {
    try {
      val render = engine.forString(safeRewrite(contents))
      render.render(model)
    } catch {
      case ex: Exception =>
        logger.error(s"process ${contents} error", ex)
        contents
    }
  }

  def safeRewrite(text: String): String = {
    var template = text.trim()
    val start = "${"
    val end = "}"
    var processIdx = 0
    while (template.indexOf(start, processIdx) >= processIdx && template.indexOf(end, processIdx) >= processIdx) {
      val startIdx = template.indexOf(start, processIdx)
      val endIdx = template.indexOf(end, processIdx)
      if (startIdx >= 0 && endIdx > startIdx) {
        var exp = template.substring(startIdx + start.length, endIdx).trim()
        if (!exp.startsWith("(") && !exp.endsWith(")!")) {
          exp = s"(${exp})!"
          val sb = new StringBuilder(template)
          sb.replace(startIdx + start.length, endIdx, exp)
          template = sb.toString()
        }
      }
      if (endIdx > processIdx) {
        processIdx = endIdx + 1
      } else {
        processIdx = template.length
      }
    }
    template
  }
}
