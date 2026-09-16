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

import freemarker.template.*

import java.time.Period

/** java.time.Period 的模板模型。
 *
 * 登记 Period 的无参 API，公共部分见 [[TemporalAmountModel]]：
 *
 *  - `${p}`                                ISO-8601 文本（P1Y2M3D）
 *  - `${p.years}` `${p.months}` `${p.days}`  年、月、日字段
 *  - `${p.toTotalMonths}` `${p.toTotalMonths()}`  折算成月的总数
 *  - `${p.isZero?c}` `<#if p.isNegative()>`       布尔
 *
 * 成员统一是「可调用 + 可直接输出」的模型，属性写法和 Java 方法写法都成立。
 * normalized()/negated() 返回 Period 本身，未在此暴露，需要时在模型层先算好。
 * 不做本地化格式化：要「1年2个月」这类文案，由模板或模型层自行组装。
 */
class PeriodModel(period: Period, wrapper: ObjectWrapper) extends TemporalAmountModel[Period](period, wrapper) {

  import TemporalAmountModel.*

  /** getYears/getMonths/getDays 只是别名，不列入 hash 的键。 */
  override protected val memberNames: Seq[String] = Seq(
    "years", "months", "days", "isZero", "isNegative", "toTotalMonths")

  override protected def member(key: String): TemplateModel = key match {
    case "years" | "getYears" => NumberMember(key, period.getYears.toLong)
    case "months" | "getMonths" => NumberMember(key, period.getMonths.toLong)
    case "days" | "getDays" => NumberMember(key, period.getDays.toLong)
    case "isZero" => BooleanMember(key, period.isZero)
    case "isNegative" => BooleanMember(key, period.isNegative)
    case "toTotalMonths" => NumberMember(key, period.toTotalMonths)
    case _ => null
  }
}
