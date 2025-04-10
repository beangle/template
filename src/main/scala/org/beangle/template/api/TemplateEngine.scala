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

import java.io.{StringWriter, Writer}

trait TemplateEngine {
  final def render(template: String, model: Any): String = {
    val sw = new StringWriter()
    renderTo(template, model, sw)
    sw.toString
  }

  def renderTo(template: String, model: Any, writer: Writer): Unit

  def forTemplate(template: String): TemplateRender

  def forString(templateStr:String):TemplateRender

  def suffix: String

}

trait TagTemplateEngine extends TemplateEngine{

  def newTag(context: ComponentContext,clazz: Class[_ <: Component]): Tag
}
