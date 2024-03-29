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

import java.{util => ju}

import freemarker.template._

import scala.jdk.javaapi.CollectionConverters.asJava

class ParametersHashModel(val params: collection.Map[String, Any], wrapper: ObjectWrapper) extends TemplateHashModelEx {
  override def get(key: String): TemplateModel = {
    params.get(key) match {
      case Some(v) =>
        if (v.getClass.isArray) {
          new SimpleScalar(v.asInstanceOf[Array[_]](0).asInstanceOf[String])
        } else new SimpleScalar(v.asInstanceOf[String])
      case None => null
    }
  }

  override def isEmpty: Boolean = {
    params.isEmpty
  }

  override def size: Int = {
    params.size
  }

  override def keys: TemplateCollectionModel = {
    new SimpleCollection(asJava(params.keys.iterator), wrapper)
  }

  override def values: TemplateCollectionModel = {
    val iter = params.keys.iterator
    val javaIter = new ju.Iterator[Any]() {
      override def hasNext: Boolean = {
        iter.hasNext
      }

      override def next(): Any = {
        params(iter.next())
      }

      override def remove(): Unit = {
        throw new UnsupportedOperationException()
      }
    }

    new SimpleCollection(javaIter, wrapper)
  }
}
