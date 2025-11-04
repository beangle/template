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

import org.beangle.commons.lang.reflect.BeanInfos
import org.beangle.template.api.DynaProfile
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class DefaultTemplateEngineTest extends AnyFunSpec, Matchers {
  describe(" DefaultTemplateEngine") {
    it("render") {
      val template = DefaultTemplateEngine().forTemplate("/templates/test.ftl")
      val datas = Map("name" -> "world!", "scheme" -> new Scheme)
      val a = BeanInfos.of(classOf[Scheme])
      println(template.render(datas))
    }

    it("profile loader") {
      DynaProfile.set("dev")
      val template = DefaultTemplateEngine().forTemplate("/templates/index.ftl")
      val datas = Map("name" -> "world!")
      val html = template.render(datas)
      assert(html.nonEmpty)
    }
  }
}
