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
import freemarker.ext.util.WrapperTemplateModel
import freemarker.template.*
import org.beangle.commons.lang.reflect.BeanInfos

class BeangleBeanModel(obj: AnyRef, wrapper: BeansWrapper) extends TemplateHashModelEx,
  AdapterTemplateModel, WrapperTemplateModel, TemplateModelWithAPISupport, TemplateScalarModel {

  private val beaninfo = BeanInfos.get(obj.getClass)

  override def get(key: String): TemplateModel = {
    beaninfo.getGetter(key) match {
      case Some(s) => wrapper.wrap(s.invoke(obj))
      case None => beaninfo.methods.get(key) match {
        case Some(h) =>
          //if method is single and without params,then invoke it directly.
          if h.size == 1 && h.head.getParameterCount == 0 then wrapper.wrap(h.head.invoke(obj))
          else new SimpleMethodModel(obj, h, wrapper)
        case None =>
          if (key == "className") wrapper.wrap(obj.getClass.getName) else wrapper.wrap(null)
      }
    }
  }

  override def size(): Int = {
    beaninfo.properties.size
  }

  override def keys(): TemplateCollectionModel = {
    import scala.jdk.javaapi.CollectionConverters.asJava
    val properties = beaninfo.properties
    new CollectionAndSequence(new SimpleSequence(asJava(properties.keySet), wrapper))
  }

  override def values(): TemplateCollectionModel = {
    val properties = beaninfo.properties
    val values = new java.util.ArrayList[Any](properties.size)
    val it = keys().iterator()
    while (it.hasNext) {
      val key = it.next().asInstanceOf[TemplateScalarModel].getAsString
      values.add(get(key))
    }
    new CollectionAndSequence(new SimpleSequence(values, wrapper))
  }

  override def isEmpty: Boolean = {
    obj match {
      case null => true
      case s: String => s.isEmpty
      case s: java.lang.Boolean => s == java.lang.Boolean.FALSE
      case _ => false
    }
  }

  override def getAsString: String = {
    obj.toString
  }

  override def getAPI: TemplateModel = wrapper.wrapAsAPI(obj)

  override def getWrappedObject: AnyRef = obj

  override def getAdaptedObject(hint: Class[_]): AnyRef = obj
}
