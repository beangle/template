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
import freemarker.ext.beans.{BeanModel, BeansWrapper}
import freemarker.template.{SimpleSequence, TemplateCollectionModel, TemplateModel, TemplateScalarModel}
import org.beangle.commons.lang.reflect.BeanInfos

class StringModel(`object`: AnyRef, wrapper: BeansWrapper) extends BeanModel(`object`, wrapper) with TemplateScalarModel {

  override def get(key: String): TemplateModel = {
    if (key == "class") {
      wrapper.wrap(`object`.getClass)
    } else {
      if BeanInfos.cached(`object`.getClass) then
        val bi = BeanInfos.get(`object`.getClass)
        bi.getGetter(key) match {
          case Some(s) => wrapper.wrap(s.invoke(`object`))
          case None => bi.methods.get(key).headOption match {
            case Some(h) => new SimpleMethodModel(`object`, h, wrapper)
            case None => wrapper.wrap(null)
          }
        }
      else super.get(key)
    }
  }

  override def size(): Int = {
    BeanInfos.get(`object`.getClass).properties.size
  }

  override def keys(): TemplateCollectionModel = {
    import scala.jdk.javaapi.CollectionConverters.asJava
    val properties = BeanInfos.get(`object`.getClass).properties
    new CollectionAndSequence(new SimpleSequence(asJava(properties.keySet), wrapper))
  }

  override def values(): TemplateCollectionModel = {
    val properties = BeanInfos.get(`object`.getClass).properties
    val values = new java.util.ArrayList[Any](properties.size)
    val it = keys().iterator()
    while (it.hasNext) {
      val key = it.next().asInstanceOf[TemplateScalarModel].getAsString
      values.add(get(key))
    }
    new CollectionAndSequence(new SimpleSequence(values, wrapper))
  }

  override def isEmpty: Boolean = {
    `object` match {
      case null => true
      case s: String => s.length() == 0
      case s: java.lang.Boolean => s == java.lang.Boolean.FALSE
      case _ => false
    }
  }

  override def getAsString: String = {
    `object`.toString
  }
}
