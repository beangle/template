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

import freemarker.core.CollectionAndSequence
import freemarker.ext.util.WrapperTemplateModel
import freemarker.template.*

import java.time.temporal.TemporalAmount
import java.util as ju
import scala.jdk.javaapi.CollectionConverters.asJava

/** java.time.TemporalAmount（Duration、Period）模板模型的共同骨架。
 *
 * TemporalAmount 不是 java bean（MetaLoader 不支持 java.* 类型），落到
 * [[BeangleObjectWrapper]] 的兜底分支只能得到 ISO-8601 文本，模板里既读不到字段，
 * 也调不了 toSeconds()、toTotalMonths() 之类的方法。子类负责登记成员，基类提供：
 *
 *  - 标量：`${amount}` 输出 ISO-8601 文本
 *  - hash：`?size` `?keys` `?values` 与按名取成员
 *  - 成员模型：可当无参方法调用，也可直接输出（见 NumberMember/BooleanMember）
 *  - 回环：实现 WrapperTemplateModel/AdapterTemplateModel，unwrap 后仍是原对象
 *
 * 都是直接的模型构造，不经反射，GraalVM native 下同样可用。
 */
abstract class TemporalAmountModel[T <: TemporalAmount](amount: T, wrapper: ObjectWrapper)
  extends TemplateHashModelEx, TemplateScalarModel,
  WrapperTemplateModel, AdapterTemplateModel {

  /** hash 上可枚举的成员名，Java 方法之外的别名不列入。 */
  protected def memberNames: Seq[String]

  /** 按名取成员，未知名返回 null，FreeMarker 会据此报 InvalidReference。 */
  protected def member(key: String): TemplateModel

  override def get(key: String): TemplateModel = member(key)

  override def isEmpty: Boolean = false

  override def size(): Int = memberNames.size

  override def keys(): TemplateCollectionModel = {
    val names = memberNames.map(name => new SimpleScalar(name): TemplateModel)
    new CollectionAndSequence(new SimpleSequence(asJava(names), wrapper))
  }

  override def values(): TemplateCollectionModel = {
    new CollectionAndSequence(new SimpleSequence(asJava(memberNames.map(member)), wrapper))
  }

  override def getAsString: String = amount.toString

  override def getWrappedObject: AnyRef = amount

  override def getAdaptedObject(hint: Class[?]): AnyRef = amount
}

object TemporalAmountModel {

  /** 无参数值成员：`${d.toSeconds()}` 与 `${d.toSeconds}` 都可用。 */
  private[freemarker] class NumberMember(name: String, value: => Long)
    extends TemplateMethodModelEx, TemplateNumberModel {

    override def exec(arguments: ju.List[?]): AnyRef = {
      checkNoArgument(name, arguments)
      new SimpleNumber(value)
    }

    override def getAsNumber: Number = java.lang.Long.valueOf(value)
  }

  /** 无参布尔成员：`<#if p.isZero>`、`${p.isZero?c}`、`${p.isZero()}` 都可用。 */
  private[freemarker] class BooleanMember(name: String, value: => Boolean)
    extends TemplateMethodModelEx, TemplateBooleanModel {

    override def exec(arguments: ju.List[?]): AnyRef = {
      checkNoArgument(name, arguments)
      if (value) TemplateBooleanModel.TRUE else TemplateBooleanModel.FALSE
    }

    override def getAsBoolean: Boolean = value
  }

  private[freemarker] def checkNoArgument(name: String, arguments: ju.List[?]): Unit = {
    if (!arguments.isEmpty) {
      throw new TemplateModelException(name + " has 0 params,but receive " + arguments.size() + " arguments.")
    }
  }
}
