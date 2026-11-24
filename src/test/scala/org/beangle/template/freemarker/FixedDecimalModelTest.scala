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

import freemarker.template.Configuration
import org.beangle.commons.lang.math.{DecimalConvertible, Decimal5, TinyDecimal5}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.StringWriter
import java.math.{BigDecimal as JBigDecimal}

class FixedDecimalModelTest extends AnyFunSpec with Matchers {
  class TinyCreditBean(var owedCredits: TinyDecimal5)
  class CreditBean(var owedCredits: Decimal5)
  class CreditPairBean(var owedCredits: Decimal5, var owedCredits2: Decimal5)

  class InterfaceDecimal(val value: JBigDecimal) extends Number, DecimalConvertible {
    override def intValue(): Int = value.intValue()
    override def longValue(): Long = value.longValue()
    override def floatValue(): Float = value.floatValue()
    override def doubleValue(): Double = value.doubleValue()
    override def toBigDecimal: JBigDecimal = value
  }

  class InterfaceCreditBean(var owedCredits: InterfaceDecimal)

  private def render(templateText: String, data: Map[String, Any]): String = {
    val cfg = new Configuration(Configuration.VERSION_2_3_35)
    cfg.setObjectWrapper(new BeangleObjectWrapper)
    cfg.setTagSyntax(Configuration.SQUARE_BRACKET_TAG_SYNTAX)
    cfg.setTemplateLoader(new freemarker.cache.StringTemplateLoader)
    cfg.getTemplateLoader.asInstanceOf[freemarker.cache.StringTemplateLoader]
      .putTemplate("test", templateText)
    val out = new StringWriter
    cfg.getTemplate("test").process(data, out)
    out.toString
  }

  describe("Fixed decimal numbers in FreeMarker") {
    it("supports TinyDecimal5 numeric comparison and formatting") {
      render("[#if bean.owedCredits > 0]${bean.owedCredits?string(\"0.##\")}[/#if]",
        Map("bean" -> { val bean = new TinyCreditBean(TinyDecimal5.of("1.23456")); bean })) shouldBe "1.23"
    }

    it("supports Decimal5 numeric comparison and formatting") {
      render("[#if bean.owedCredits > 0]${bean.owedCredits?string(\"0.##\")}[/#if]",
        Map("bean" -> { val bean = new CreditBean(Decimal5.of("123456.78901")); bean })) shouldBe "123456.79"
    }

    it("supports Decimal5 equality comparison") {
      render("[#if bean.owedCredits != bean.owedCredits2]different[/#if]",
        Map("bean" -> {
          new CreditPairBean(Decimal5.of("1.23456"), Decimal5.of("1.23457"))
        })) shouldBe "different"
    }

    it("supports other DecimalConvertible numbers") {
      render("[#if bean.owedCredits > 0]${bean.owedCredits?string(\"0.##\")}[/#if]",
        Map("bean" -> new InterfaceCreditBean(new InterfaceDecimal(new JBigDecimal("7.89012"))))) shouldBe "7.89"
    }
  }
}
