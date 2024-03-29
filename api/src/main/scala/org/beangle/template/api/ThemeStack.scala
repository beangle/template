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

import java.util as ju

/**
 * ui主体栈
 * @author chaostone
 */
class ThemeStack {
  private val themes = new ju.Stack[Theme]()

  def push(item: Theme): Theme = themes.push(item)

  def pop(): Theme = themes.pop()

  def peek(): Theme = themes.peek()

  def isEmpty: Boolean = themes.isEmpty

}
