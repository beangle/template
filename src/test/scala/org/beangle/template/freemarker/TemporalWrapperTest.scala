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
import freemarker.template.{Configuration, SimpleDate, SimpleScalar, TemplateDateModel}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

import java.io.StringWriter
import java.time.*
import java.time.chrono.JapaneseChronology
import java.time.temporal.{Temporal, TemporalField, TemporalUnit, UnsupportedTemporalTypeException}

class TemporalWrapperTest extends AnyFunSpec with Matchers {

  private val wrapper = new BeangleObjectWrapper

  private def dateType(obj: AnyRef): Int = {
    val model = wrapper.wrap(obj)
    model shouldBe a[SimpleDate]
    model.asInstanceOf[SimpleDate].getDateType
  }

  private def render(template: String, value: AnyRef): String = {
    val cfg = new Configuration(Configuration.VERSION_2_3_35)
    cfg.setObjectWrapper(wrapper)
    cfg.setTemplateLoader(new StringTemplateLoader())
    val loader = cfg.getTemplateLoader.asInstanceOf[StringTemplateLoader]
    loader.putTemplate("temporal", template)
    val out = new StringWriter()
    cfg.getTemplate("temporal").process(Map("t" -> value), out)
    out.toString
  }

  /** 非 JDK 的 Temporal 实现，验证兜底分支不再抛 MatchError。 */
  private class CustomTemporal extends Temporal {
    override def isSupported(unit: TemporalUnit): Boolean = false
    override def isSupported(field: TemporalField): Boolean = false
    override def `with`(field: TemporalField, newValue: Long): Temporal = unsupported
    override def plus(amount: Long, unit: TemporalUnit): Temporal = unsupported
    override def until(endExclusive: Temporal, unit: TemporalUnit): Long = unsupported
    override def getLong(field: TemporalField): Long = unsupported
    override def toString: String = "custom-temporal"
    private def unsupported: Nothing = throw new UnsupportedTemporalTypeException("unsupported")
  }

  describe("BeangleObjectWrapper temporal") {
    it("wraps datetime family") {
      dateType(Instant.now()) shouldBe TemplateDateModel.DATETIME
      dateType(ZonedDateTime.now()) shouldBe TemplateDateModel.DATETIME
      dateType(OffsetDateTime.now()) shouldBe TemplateDateModel.DATETIME
      dateType(LocalDateTime.now()) shouldBe TemplateDateModel.DATETIME
      dateType(JapaneseChronology.INSTANCE.localDateTime(LocalDateTime.now())) shouldBe TemplateDateModel.DATETIME
      dateType(JapaneseChronology.INSTANCE.zonedDateTime(ZonedDateTime.now())) shouldBe TemplateDateModel.DATETIME
    }

    it("wraps date family") {
      dateType(LocalDate.of(2026, 1, 2)) shouldBe TemplateDateModel.DATE
      dateType(JapaneseChronology.INSTANCE.date(2026, 1, 2)) shouldBe TemplateDateModel.DATE
      dateType(YearMonth.of(2026, 1)) shouldBe TemplateDateModel.DATE
      dateType(Year.of(2026)) shouldBe TemplateDateModel.DATE
    }

    it("wraps time family") {
      dateType(LocalTime.of(9, 30)) shouldBe TemplateDateModel.TIME
      dateType(OffsetTime.of(9, 30, 0, 0, ZoneOffset.ofHours(8))) shouldBe TemplateDateModel.TIME
    }

    it("renders with explicit format") {
      render("${t?string('yyyy-MM-dd')}", LocalDate.of(2026, 1, 2)) shouldBe "2026-01-02"
      render("${t?string('HH:mm')}", LocalTime.of(9, 30)) shouldBe "09:30"
      render("${t?string('HH:mm')}", OffsetTime.of(9, 30, 0, 0, ZoneOffset.ofHours(8))) shouldBe "09:30"
      render("${t?string('yyyy-MM-dd')}", YearMonth.of(2026, 1)) shouldBe "2026-01-01"
      render("${t?string('yyyy')}", Year.of(2026)) shouldBe "2026"
      render("${t?string('yyyy-MM-dd')}", JapaneseChronology.INSTANCE.date(2026, 1, 2)) shouldBe "2026-01-02"
      render("${t?string('yyyy-MM-dd HH:mm')}",
        JapaneseChronology.INSTANCE.localDateTime(LocalDateTime.of(2026, 1, 2, 3, 4))) shouldBe "2026-01-02 03:04"
    }

    it("falls back to text for unknown temporal") {
      val custom = new CustomTemporal
      wrapper.wrap(custom) shouldBe a[SimpleScalar]
      render("${t}", custom) shouldBe "custom-temporal"
    }
  }
}
