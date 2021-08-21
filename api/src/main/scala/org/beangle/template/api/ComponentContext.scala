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

import org.beangle.commons.text.i18n.TextProvider

class ComponentContext(val engine: TagTemplateEngine, val idGenerator: UIIdGenerator, val textProvider: TextProvider, val services: Map[String, AnyRef]) {

  private val themeStack = new ThemeStack()

  private val components = new collection.mutable.Stack[Component]

  /**
   * Finds the nearest ancestor of this component stack.
   */
  def find[T <: Component](clazz: Class[T]): T = {
    components.find { component => clazz == component.getClass } match {
      case Some(c) => c.asInstanceOf[T]
      case None => null.asInstanceOf[T]
    }
  }

  def pop(): Component = {
    val elem = components.pop()
    if (null != elem.theme) themeStack.pop()
    elem
  }

  def theme: Theme = {
    if (themeStack.isEmpty) Themes.Default
    else themeStack.peek()
  }

  def push(component: Component): Unit = {
    components.push(component)
    if (null != component.theme) themeStack.push(Themes(component.theme))
  }
}
