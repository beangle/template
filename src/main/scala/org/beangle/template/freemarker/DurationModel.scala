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

import java.time.Duration

/** java.time.Duration 的模板模型。
 *
 * 登记 Duration 的无参 API，公共部分见 [[TemporalAmountModel]]：
 *
 *  - `${d}`                                 ISO-8601 文本（PT1H30M），与旧行为一致
 *  - `${d.seconds}` `${d.nano}`             秒、纳秒
 *  - `${d.isZero?c}` `<#if d.isZero()>`     布尔
 *  - `${d.toMinutes}` `${d.toMinutes()}`    无参方法，写不写括号都可以
 *
 * 成员统一是「可调用 + 可直接输出」的模型，属性写法和 Java 方法写法都成立。
 * 不做本地化格式化：要「1小时30分」这类文案，由模板或模型层自行组装。
 */
class DurationModel(duration: Duration, wrapper: ObjectWrapper) extends TemporalAmountModel[Duration](duration, wrapper) {

  import TemporalAmountModel.*

  /** getSeconds/getNano 只是别名，不列入 hash 的键。 */
  override protected val memberNames: Seq[String] = Seq(
    "seconds", "nano", "isZero", "isNegative",
    "toDays", "toHours", "toMinutes", "toSeconds", "toMillis", "toNanos")

  override protected def member(key: String): TemplateModel = key match {
    case "seconds" | "getSeconds" => NumberMember(key, duration.getSeconds)
    case "nano" | "getNano" => NumberMember(key, duration.getNano)
    case "isZero" => BooleanMember(key, duration.isZero)
    case "isNegative" => BooleanMember(key, duration.isNegative)
    case "toDays" => NumberMember(key, duration.toDays)
    case "toHours" => NumberMember(key, duration.toHours)
    case "toMinutes" => NumberMember(key, duration.toMinutes)
    case "toSeconds" => NumberMember(key, duration.toSeconds)
    case "toMillis" => NumberMember(key, duration.toMillis)
    case "toNanos" => NumberMember(key, duration.toNanos)
    case _ => null
  }
}
