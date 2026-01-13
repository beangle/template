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

import freemarker.ext.beans.{BeansWrapper, _MethodUtil}
import freemarker.template.{TemplateMethodModelEx, TemplateModel, TemplateModelException}
import org.beangle.commons.collection.Collections

import java.lang.reflect.Method
import scala.collection.immutable

class SimpleMethodModel(obj: AnyRef, methods: Seq[Method], wrapper: BeansWrapper) extends TemplateMethodModelEx {

  override def exec(arguments: java.util.List[_]): AnyRef = {
    try {
      val args = unwrapArguments(arguments, wrapper)
      findMethod(args) match {
        case Some(method) =>
          val paramTypes = method.getParameterTypes
          val retval =
            if (paramTypes.isEmpty) {
              if (args.length > 0) {
                throw new IllegalArgumentException(s"${method.getName} has 0 params,but receive only ${args.length} arguments.")
              } else {
                method.invoke(obj)
              }
            } else if (method.isVarArgs) { //java函数，可变参数
              val newArgs = Collections.newBuffer[AnyRef]
              var pIndex = 0
              paramTypes.indices foreach { i =>
                if (paramTypes(i).isArray) {
                  val len = args.length - pIndex
                  val copy = java.lang.reflect.Array.newInstance(paramTypes(i).getComponentType, len)
                  (0 until len) foreach { l =>
                    java.lang.reflect.Array.set(copy, l, args(pIndex + l))
                  }
                  newArgs.addOne(copy)
                } else {
                  newArgs.addOne(args(pIndex))
                  pIndex += 1
                }
              }
              method.invoke(obj, newArgs.toSeq: _*)
            } else {
              val lastIdx = paramTypes.length - 1
              if (paramTypes(lastIdx) == classOf[immutable.Seq[_]]) { //Scala函数，最后一个参数是可变参数
                if (args.length == paramTypes.length) { //1. 将最后一个参数转变为为可变参数
                  if (!classOf[immutable.Seq[_]].isAssignableFrom(args(lastIdx).getClass)) {
                    args(lastIdx) = immutable.Seq(args(lastIdx))
                  }
                  method.invoke(obj, args.toSeq: _*)
                } else if (args.length > paramTypes.length) { //2.将剩余参数转变为可变参数
                  val newArgs = Collections.newBuffer[AnyRef]
                  (0 until lastIdx) foreach { i => newArgs.addOne(args(i)) }
                  val lastArgs = Collections.newBuffer[AnyRef]
                  (lastIdx until args.length) foreach { i => lastArgs.addOne(args(i)) }
                  newArgs.addOne(lastArgs.toSeq)
                  method.invoke(obj, newArgs.toSeq: _*)
                } else if (args.length == paramTypes.length - 1) { //3. 将最后一个参数设置为空数组
                  val newArgs = Collections.newBuffer[AnyRef]
                  newArgs.addAll(args)
                  newArgs.addOne(List.empty)
                  method.invoke(obj, newArgs.toSeq: _*)
                } else { //4. 参数个数不匹配
                  throw new IllegalArgumentException(s"${method.getName} need ${paramTypes.length} params,but receive only ${args.length} arguments.")
                }
              } else { //normal method
                method.invoke(obj, args: _*)
              }
            }
          if (method.getReturnType == classOf[Unit])
            TemplateModel.NOTHING
          else wrapper.wrap(retval)
        case None => null
      }
    } catch {
      case e: TemplateModelException => throw e
      case e: Exception =>
        throw _MethodUtil.newInvocationTemplateModelException(obj, methods.head, e);
    }
  }

  def findMethod(args: Array[AnyRef]): Option[Method] = {
    if (methods.size == 1) {
      methods.headOption
    } else {
      val paramCountMatched = methods.filter(_.getParameterCount == args.length)
      if (paramCountMatched.size == 1) {
        paramCountMatched.headOption
      } else {
        paramCountMatched find { method =>
          args.indices.forall { i =>
            val paramType = method.getParameterTypes()(i)
            (args(i) == null && !paramType.isPrimitive) || (null != args(i) && paramType.isInstance(args(i)))
          }
        }
      }
    }
  }

  private def unwrapArguments(arguments: java.util.List[_], wrapper: BeansWrapper): Array[AnyRef] = {
    if (arguments eq null) return Array.empty[AnyRef]
    val args = new Array[AnyRef](arguments.size())
    var i = 0
    while (i < args.length) {
      args(i) = wrapper.unwrap(arguments.get(i).asInstanceOf[TemplateModel])
      i += 1
    }
    args
  }
}
