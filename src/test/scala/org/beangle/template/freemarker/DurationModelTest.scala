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

import freemarker.cache.StringTemplateLoader
import freemarker.template.Configuration
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.StringWriter
import java.time.Duration

class DurationModelTest extends AnyFunSpec with Matchers {

  private val wrapper = new BeangleObjectWrapper

  /** number_format 固定为 0，避免输出受默认 Locale 的千分位影响。 */
  private def render(template: String, duration: Duration): String = {
    val cfg = new Configuration(Configuration.VERSION_2_3_35)
    cfg.setObjectWrapper(wrapper)
    cfg.setNumberFormat("0")
    cfg.setTemplateLoader(new StringTemplateLoader())
    val loader = cfg.getTemplateLoader.asInstanceOf[StringTemplateLoader]
    loader.putTemplate("duration", template)
    val out = new StringWriter()
    cfg.getTemplate("duration").process(Map("d" -> duration), out)
    out.toString
  }

  describe("DurationModel") {
    it("wraps as scalar with iso text") {
      render("${d}", Duration.ofMinutes(90)) shouldBe "PT1H30M"
    }

    it("supports toXxx() method call") {
      val duration = Duration.ofMinutes(90)
      render("${d.toSeconds()}", duration) shouldBe "5400"
      render("${d.toMinutes()}", duration) shouldBe "90"
      render("${d.toHours()}", duration) shouldBe "1"
      render("${d.toMillis()}", duration) shouldBe "5400000"
      render("${d.toNanos()}", duration) shouldBe "5400000000000"
    }

    it("supports property style and parameterless call") {
      val duration = Duration.ofMinutes(90)
      render("${d.seconds}", duration) shouldBe "5400"
      render("${d.getSeconds()}", duration) shouldBe "5400"
      render("${d.toSeconds}", duration) shouldBe "5400"
      render("${d.seconds()}", duration) shouldBe "5400"
      render("${d.nano}", Duration.ofSeconds(61, 500)) shouldBe "500"
    }

    it("supports boolean members") {
      render("<#if d.isZero>zero<#else>nonzero</#if>", Duration.ZERO) shouldBe "zero"
      render("<#if d.isNegative()>negative<#else>positive</#if>", Duration.ofSeconds(-3)) shouldBe "negative"
    }

    it("is usable in expressions") {
      val duration = Duration.ofMinutes(90)
      render("${d.toMinutes() + 30}", duration) shouldBe "120"
      render("<#if d.toSeconds() gt 3600>long<#else>short</#if>", duration) shouldBe "long"
    }

    it("exposes hash keys") {
      val duration = Duration.ofMinutes(90)
      render("${d?size}", duration) shouldBe "10"
      render("<#list d?keys?sort as k>${k},</#list>", duration) shouldBe
        "isNegative,isZero,nano,seconds,toDays,toHours,toMillis,toMinutes,toNanos,toSeconds,"
    }

    it("unwraps to duration") {
      val duration = Duration.ofSeconds(61, 500)
      val model = wrapper.wrap(duration)
      model shouldBe a[DurationModel]
      wrapper.unwrap(model) shouldBe duration
    }
  }
}
