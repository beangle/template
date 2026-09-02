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

import java.util as ju

class ObjectWrapperTest extends AnyFunSpec with Matchers {
  describe("BeangleObjectWrapper") {
    it("wrapper") {
      val wrapper = new BeangleObjectWrapper()
      val page = new SinglePage(2, 2, 100, List(21, 21))
      val wrapped = wrapper.wrap(page)
      val unwrapped = wrapper.unwrap(wrapped)
      assert(unwrapped == page)
    }
    it("wrapper array") {
      val wrapper = new BeangleObjectWrapper()
      val ints = Array(1, 2, 3)
      wrapper.wrap(ints)
      val strs = Array("1", "2", "3")
      wrapper.wrap(strs)
    }
  }

  describe("FriendlyMapModel") {
    val wrapper = new BeangleObjectWrapper()

    it("java.util.Map key access") {
      val map = new ju.HashMap[String, Any]()
      map.put("name", "beangle")
      map.put("version", 42)
      val model = wrapper.wrap(map).asInstanceOf[FriendlyMapModel]
      model.get("name").toString shouldBe "beangle"
      model.get("version").toString shouldBe "42"
      model.get("nonexistent") shouldBe null
    }

    it("java.util.Map size and isEmpty") {
      val empty = new ju.HashMap[String, String]()
      val model = wrapper.wrap(empty).asInstanceOf[FriendlyMapModel]
      model.isEmpty shouldBe true
      model.size() shouldBe 0

      empty.put("a", "1")
      model.isEmpty shouldBe false
      model.size() shouldBe 1
    }

    it("java.util.Map nested value wrapping") {
      val inner = new ju.HashMap[String, String]()
      inner.put("key", "val")
      val outer = new ju.HashMap[String, Any]()
      outer.put("nested", inner)
      val model = wrapper.wrap(outer).asInstanceOf[FriendlyMapModel]
      val nested = model.get("nested").asInstanceOf[FriendlyMapModel]
      nested.get("key").toString shouldBe "val"
    }

    it("java.util.Map in FTL template rendering") {
      import freemarker.template.Configuration
      import java.io.StringWriter
      val cfg = new Configuration(Configuration.VERSION_2_3_35)
      cfg.setObjectWrapper(wrapper)
      cfg.setTemplateLoader(new freemarker.cache.StringTemplateLoader())
      cfg.getTemplateLoader.asInstanceOf[freemarker.cache.StringTemplateLoader]
        .putTemplate("test", "<#list data?keys as k>${k}=${data[k]} </#list>")
      val template = cfg.getTemplate("test")
      val map = new ju.LinkedHashMap[String, String]()
      map.put("a", "1")
      map.put("b", "2")
      val out = new StringWriter()
      template.process(Map("data" -> map), out)
      out.toString shouldBe "a=1 b=2 "
    }

    it("java.util.Map.get(key) method call in FTL") {
      import freemarker.template.Configuration
      import java.io.StringWriter
      val cfg = new Configuration(Configuration.VERSION_2_3_35)
      cfg.setObjectWrapper(wrapper)
      cfg.setTemplateLoader(new freemarker.cache.StringTemplateLoader())
      cfg.getTemplateLoader.asInstanceOf[freemarker.cache.StringTemplateLoader]
        .putTemplate("getTest",
          """<#list data?keys?sort as k>${k}=${data.get(k)} </#list>""".stripMargin)
      val template = cfg.getTemplate("getTest")
      val map = new ju.LinkedHashMap[String, String]()
      map.put("b", "2")
      map.put("a", "1")
      val out = new StringWriter()
      template.process(Map("data" -> map), out)
      out.toString shouldBe "a=1 b=2 "
    }

    it("java.util.Map with Integer keys via get() in FTL") {
      import freemarker.template.Configuration
      import java.io.StringWriter
      val cfg = new Configuration(Configuration.VERSION_2_3_35)
      cfg.setObjectWrapper(wrapper)
      cfg.setTemplateLoader(new freemarker.cache.StringTemplateLoader())
      cfg.getTemplateLoader.asInstanceOf[freemarker.cache.StringTemplateLoader]
        .putTemplate("intKeyTest",
          """<#list data?keys?sort as k>${k}=${data.get(k)} </#list>""".stripMargin)
      val template = cfg.getTemplate("intKeyTest")
      val map = new ju.LinkedHashMap[java.lang.Integer, String]()
      map.put(3, "c")
      map.put(1, "a")
      map.put(2, "b")
      val out = new StringWriter()
      template.process(Map("data" -> map), out)
      out.toString shouldBe "1=a 2=b 3=c "
    }

    it("java.util.Map with nested map get() in FTL") {
      import freemarker.template.Configuration
      import java.io.StringWriter
      val cfg = new Configuration(Configuration.VERSION_2_3_35)
      cfg.setObjectWrapper(wrapper)
      cfg.setTemplateLoader(new freemarker.cache.StringTemplateLoader())
      cfg.getTemplateLoader.asInstanceOf[freemarker.cache.StringTemplateLoader]
        .putTemplate("nestedGetTest",
          """<#list rows?keys?sort as r>[<#list rows.get(r)?keys?sort as c>${r}.${c}=${rows.get(r).get(c)} </#list>]</#list>""".stripMargin)
      val template = cfg.getTemplate("nestedGetTest")
      val inner1 = new ju.LinkedHashMap[String, String]()
      inner1.put("y", "2")
      inner1.put("x", "1")
      val inner2 = new ju.LinkedHashMap[String, String]()
      inner2.put("a", "3")
      val rows = new ju.LinkedHashMap[String, ju.Map[String, String]]()
      rows.put("1", inner1)
      rows.put("2", inner2)
      val out = new StringWriter()
      template.process(Map("rows" -> rows), out)
      out.toString shouldBe "[1.x=1 1.y=2 ][2.a=3 ]"
    }
  }
}
