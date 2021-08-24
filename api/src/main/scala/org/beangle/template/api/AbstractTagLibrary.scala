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

import org.beangle.template.api.{TagLibrary, TemplateEngine}
import org.beangle.commons.text.i18n.TextProvider

object AbstractTagLibrary {
  private val ContextAttributeName = "_beangle_template_component_context"
}

/**
 * @author chaostone
 */
abstract class AbstractTagLibrary extends TagLibrary {

  var templateEngine: TagTemplateEngine = _

  protected def buildComponentContext(): ComponentContext = {
    import AbstractTagLibrary.*
    //FIXME
//    var context = req.getAttribute(ContextAttributeName).asInstanceOf[ComponentContext]
//    if (null == context) {
//      val queryString = req.getQueryString
//      val fullpath = if (null == queryString) req.getRequestURI else req.getRequestURI + queryString
      val services = buildServices()
      val tp = services("textProvider").asInstanceOf[TextProvider]
      val idGenerator = services("idGenerator").asInstanceOf[UIIdGenerator]//, new IndexableIdGenerator(String.valueOf(Math.abs(fullpath.hashCode))))
      new ComponentContext(templateEngine, idGenerator,tp,  services)
//      req.setAttribute(ContextAttributeName, context)
//    }
//    context
  }

  def buildServices(): Map[String, AnyRef]
}
