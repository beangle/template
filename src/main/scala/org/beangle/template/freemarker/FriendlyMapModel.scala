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
import freemarker.ext.beans.BeansWrapper
import freemarker.template.*

import java.util as ju

/** Map wrapper that directly implements template model interfaces without
 * inheriting from FreeMarker's MapModel/BeanModel chain. This avoids the
 * ClassIntrospector → Introspector.getBeanInfo() call in BeanModel's
 * constructor, eliminating XBeanInfo/XCustomizer reflection probes.
 *
 * FTL usage: `map.key`, `map?keys`, `map.get(key)`, `?size`, `?values`.
 */
class FriendlyMapModel(map: ju.Map[_, _], wrapper: BeansWrapper) extends TemplateHashModelEx
  with TemplateMethodModelEx {

  /** FTL `map.key` or `map["key"]` access. */
  override def get(key: String): TemplateModel = {
    // 拦截 `map.get(key)` 方法调用：FreeMarker 先查 hash 中的 "get" 键，
    // 若返回 TemplateMethodModel 则直接调用 exec()，无需走 Java 反射回退。
    if ("get" == key) return new MapGetMethod
    wrapValue(map.get(key), key)
  }

  override def isEmpty: Boolean = map.isEmpty

  override def size(): Int = map.size

  override def keys(): TemplateCollectionModel = {
    new CollectionAndSequence(new SimpleSequence(new ju.ArrayList(map.keySet()), wrapper))
  }

  override def values(): TemplateCollectionModel = {
    new CollectionAndSequence(new SimpleSequence(map.values(), wrapper))
  }

  /** FTL `map.get(key)` 方法调用的回退入口（hash 中无 "get" 键时走此路径）。 */
  override def exec(arguments: java.util.List[_]): AnyRef = {
    val key = unwrapArg(arguments.get(0))
    wrapValue(map.get(key), key.toString)
  }

  private def wrapValue(value: AnyRef, key: String): TemplateModel = {
    if (value == null) {
      if (map.containsKey(key)) wrapper.wrap(null) else null
    } else {
      wrapper.wrap(value)
    }
  }

  /** 包装 `map.get(key)` 方法调用，供 FTL `map.get(k)` 语法使用。 */
  private class MapGetMethod extends TemplateMethodModelEx {
    override def exec(arguments: java.util.List[_]): AnyRef = {
      val key = unwrapArg(arguments.get(0))
      wrapValue(map.get(key), key.toString)
    }
  }

  /** 解包参数：FreeMarker 可能传 TemplateModel 或已解包的 Java 对象。 */
  private def unwrapArg(arg: Any): AnyRef = arg match {
    case tm: TemplateModel => wrapper.unwrap(tm)
    case other => other.asInstanceOf[AnyRef]
  }
}
