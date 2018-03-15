/*
 * Beangle, Agile Development Scaffold and Toolkits.
 *
 * Copyright © 2005, The Beangle Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.beangle.template.freemarker

import org.beangle.commons.lang.reflect.{ BeanInfos, ClassInfos }

import freemarker.core.CollectionAndSequence
import freemarker.ext.beans.{ BeanModel, BeansWrapper }
import freemarker.template.{ SimpleSequence, TemplateCollectionModel, TemplateModel, TemplateScalarModel }

class StringModel(`object`: AnyRef, wrapper: BeansWrapper) extends BeanModel(`object`, wrapper) with TemplateScalarModel {

  override def get(key: String): TemplateModel = {
    if (key == "class") return wrapper.wrap(`object`.getClass)
    else {
      val clazzName = `object`.getClass.getName
      if (clazzName.startsWith("java.") || clazzName.startsWith("scala.")) {
        super.get(key)
      } else {
        BeanInfos.Default.get(`object`.getClass).getGetter(key) match {
          case Some(s) =>
            wrapper.wrap(s.invoke(`object`))
          case None =>
            val methods = ClassInfos.get(`object`.getClass).getMethods(key)
            if (methods.isEmpty) wrapper.wrap(null) else new SimpleMethodModel(`object`, methods, wrapper)
        }
      }
    }
  }

  override def size(): Int = {
    BeanInfos.Default.get(`object`.getClass).properties.size
  }

  override def keys(): TemplateCollectionModel = {
    val properties = BeanInfos.Default.get(`object`.getClass).properties
    val keySet = collection.JavaConverters.setAsJavaSet(properties.keySet)
    new CollectionAndSequence(new SimpleSequence(keySet, wrapper))
  }

  override def values(): TemplateCollectionModel = {
    val properties = BeanInfos.Default.get(`object`.getClass).properties
    val values = new java.util.ArrayList[Any](properties.size)
    val it = keys().iterator()
    while (it.hasNext()) {
      val key = it.next().asInstanceOf[TemplateScalarModel].getAsString()
      values.add(get(key))
    }
    return new CollectionAndSequence(new SimpleSequence(values, wrapper))
  }

  override def isEmpty(): Boolean = {
    `object` match {
      case null                 => true
      case s: String            => s.length() == 0
      case s: java.lang.Boolean => s == java.lang.Boolean.FALSE
      case _                    => false
    }
  }
  override def getAsString(): String = {
    `object`.toString
  }
}
