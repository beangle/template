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

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.util
import java.util.Collections

class SimpleMethodModelTest extends AnyFunSpec with Matchers {
  describe("SimpleMethodModel") {
    it("exec scala normal   methods") {
      val wrapper = new BeangleObjectWrapper
      val clz = classOf[Teacher]
      val methods = clz.getMethods().filter(_.getName == "updateName").toIndexedSeq
      val model = new SimpleMethodModel(new Teacher(), methods, wrapper)
      model.exec(util.Arrays.asList(wrapper.wrap("jack")))
    }

    it("exec java varargs methods") {
      val wrapper = new BeangleObjectWrapper
      val clz = classOf[Coach]
      val methods = clz.getMethods().filter(_.getName == "addSkills").toIndexedSeq
      val model = new SimpleMethodModel(new Coach(), methods, wrapper)
      model.exec(util.Arrays.asList(wrapper.wrap("pe")))
      model.exec(util.Arrays.asList(wrapper.wrap("pe"), wrapper.wrap("football")))
      model.exec(util.Arrays.asList(wrapper.wrap("pe"), wrapper.wrap("basketball"), wrapper.wrap("football")))
    }
    it("exec scala varargs methods") {
      val wrapper = new BeangleObjectWrapper
      val clz = classOf[Teacher]
      val methods = clz.getMethods().filter(_.getName == "addSkills").toIndexedSeq
      val model = new SimpleMethodModel(new Teacher(), methods, wrapper)
      model.exec(util.Arrays.asList(wrapper.wrap("art")))
      model.exec(util.Arrays.asList(wrapper.wrap("art"), wrapper.wrap("panting")))
      model.exec(util.Arrays.asList(wrapper.wrap("art"), wrapper.wrap("panting"), wrapper.wrap("music")))
    }

    it("exec scala simple varargs methods") {
      val wrapper = new BeangleObjectWrapper
      val clz = classOf[Teacher]
      val methods = clz.getMethods().filter(_.getName == "addTitles").toIndexedSeq
      val model = new SimpleMethodModel(new Teacher(), methods, wrapper)
      model.exec(Collections.emptyList())
      model.exec(util.Arrays.asList(wrapper.wrap("hero")))
      model.exec(util.Arrays.asList(wrapper.wrap("master"), wrapper.wrap("director")))
    }
  }

}
