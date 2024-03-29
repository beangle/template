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

package org.beangle.template.api

import org.beangle.commons.bean.Properties
import org.beangle.commons.lang.{Chars, Strings}

import java.io.Writer
import java.util as ju

class UIBean(context: ComponentContext) extends Component(context) {

  var id: String = _

  var cssClass: String = _

  override def end(writer: Writer, body: String): Boolean = {
    evaluateParams()
    mergeTemplate(writer)
    false
  }

  @throws(classOf[Exception])
  protected final def mergeTemplate(writer: Writer): Unit = {
    val engine = context.engine
    engine.renderTo(context.theme.getTemplatePath(getClass, engine.suffix), this, writer)
  }

  /**
   * 获得对应的国际化信息
   *
   * @param text 国际化key
   * @return 当第一个字符不是字母或者不包含.或者包含空格的均返回原有字符串
   */
  protected final def getText(text: String): String = getText(text, text)

  protected def getText(text: String, defaultText: String): String = {
    if (Strings.isEmpty(text)) return defaultText
    if (!Chars.isAsciiAlpha(text.charAt(0))) return defaultText
    if (-1 == text.indexOf('.') || -1 < text.indexOf(' ')) {
      defaultText
    } else {
      if (text.endsWith(".id")) {
        val key = Strings.substringBeforeLast(text, ".id")
        context.textResource(key, defaultText)
      } else {
        context.textResource(text, defaultText)
      }
    }
  }

  protected def getValue(obj: Any, property: String): Any = {
    obj match {
      case null => null
      case map: collection.Map[_, _] => map.asInstanceOf[collection.Map[String, Any]].get(property).orNull
      case javaMap: ju.Map[_, _] => javaMap.get(property)
      case o: AnyRef => Properties.get[Any](o, property)
    }
  }

  final def generateIdIfEmpty(): Unit = {
    if (Strings.isEmpty(id)) id = context.idGenerator.generate(getClass)
  }

  /**
   * Process label,convert empty to null
   */
  protected final def processLabel(label: String, name: String): String = {
    if (null != label) {
      if (Strings.isEmpty(label)) null else getText(label)
    } else if (this.theme != "html") {
      getText(name)
    } else {
      null
    }
  }

  protected final def addClass(added: String): Unit = {
    if (null == cssClass) cssClass = added
    else {
      val clazzes = Strings.split(cssClass, ' ').map(_.trim)
      if (!clazzes.contains(added)) {
        cssClass = added + " " + cssClass
      }
    }
  }
}

class ClosingUIBean(context: ComponentContext) extends UIBean(context) {
  var body: String = _

  override def start(writer: Writer): Boolean = {
    evaluateParams()
    true
  }

  override final def usesBody(): Boolean = true

  override final def end(writer: Writer, body: String): Boolean = {
    doEnd(writer, body)
  }

  def doEnd(writer: Writer, body: String): Boolean = {
    this.body = body
    mergeTemplate(writer)
    false
  }

}

class IterableUIBean(context: ComponentContext) extends ClosingUIBean(context) {

  protected def next(): Boolean = false

  protected def iterator(writer: Writer, body: String): Unit = {
    this.body = body
    mergeTemplate(writer)
  }

  override def start(writer: Writer): Boolean = {
    evaluateParams()
    next()
  }

  override def doEnd(writer: Writer, body: String): Boolean = {
    iterator(writer, body)
    next()
  }

}
