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
import java.time.Period

class PeriodModelTest extends AnyFunSpec with Matchers {

  private val wrapper = new BeangleObjectWrapper

  /** number_format 固定为 0，避免输出受默认 Locale 的千分位影响。 */
  private def render(template: String, period: Period): String = {
    val cfg = new Configuration(Configuration.VERSION_2_3_35)
    cfg.setObjectWrapper(wrapper)
    cfg.setNumberFormat("0")
    cfg.setTemplateLoader(new StringTemplateLoader())
    val loader = cfg.getTemplateLoader.asInstanceOf[StringTemplateLoader]
    loader.putTemplate("period", template)
    val out = new StringWriter()
    cfg.getTemplate("period").process(Map("p" -> period), out)
    out.toString
  }

  describe("PeriodModel") {
    it("wraps as scalar with iso text") {
      render("${p}", Period.of(1, 2, 3)) shouldBe "P1Y2M3D"
    }

    it("supports fields and method call") {
      val period = Period.of(1, 2, 3)
      render("${p.years}", period) shouldBe "1"
      render("${p.months}", period) shouldBe "2"
      render("${p.days}", period) shouldBe "3"
      render("${p.getYears()}", period) shouldBe "1"
      render("${p.years()}", period) shouldBe "1"
      render("${p.toTotalMonths()}", period) shouldBe "14"
      render("${p.toTotalMonths}", period) shouldBe "14"
    }

    it("supports boolean members") {
      render("<#if p.isZero>zero<#else>nonzero</#if>", Period.ZERO) shouldBe "zero"
      render("<#if p.isNegative()>negative<#else>positive</#if>", Period.ofMonths(-3)) shouldBe "negative"
    }

    it("is usable in expressions") {
      val period = Period.ofMonths(14)
      render("${p.toTotalMonths() / 12}", period) shouldBe "1"
      render("<#if p.toTotalMonths() gt 12>more<#else>less</#if>", period) shouldBe "more"
    }

    it("exposes hash keys") {
      val period = Period.of(1, 2, 3)
      render("${p?size}", period) shouldBe "6"
      render("<#list p?keys?sort as k>${k},</#list>", period) shouldBe
        "days,isNegative,isZero,months,toTotalMonths,years,"
    }

    it("renders textual duration") {
      val period = Period.of(1, 2, 3)
      render("${p.years}年${p.months}个月${p.days}天", period) shouldBe "1年2个月3天"
    }

    it("unwraps to period") {
      val period = Period.of(1, 2, 3)
      val model = wrapper.wrap(period)
      model shouldBe a[PeriodModel]
      wrapper.unwrap(model) shouldBe period
    }
  }
}
