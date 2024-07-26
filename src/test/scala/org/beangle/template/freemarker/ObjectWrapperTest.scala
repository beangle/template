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

import org.beangle.commons.collection.page.SinglePage
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ObjectWrapperTest extends AnyFunSpec with Matchers {
  describe("BeangleObjectWrapper") {
    it("wrapper") {
      val wrapper = new BeangleObjectWrapper()
      val page = new SinglePage(2, 2, 100, List(21, 21))
      val wrapped = wrapper.wrap(page)
      val unwrapped = wrapper.unwrap(wrapped)
      assert(unwrapped == page)
    }
    it("wrapper primary array") {
      val wrapper = new BeangleObjectWrapper()
      val ints = Array(1, 2, 3)
      wrapper.wrap(ints)
    }
  }
}
