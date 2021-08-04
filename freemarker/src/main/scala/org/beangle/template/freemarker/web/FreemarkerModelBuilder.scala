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

package org.beangle.template.freemarker.web

import freemarker.template.{ObjectWrapper, SimpleHash}
import jakarta.servlet.http.{HttpServletRequest, HttpServletResponse}
import org.beangle.commons.lang.annotation.description
import org.beangle.template.core.TagLibraryProvider
import org.beangle.template.freemarker.ParametersHashModel

/**
 * @author chaostone
 */
@description("Freemaker模型构建器")
class FreemarkerModelBuilder(tagLibraryProvider: TagLibraryProvider) {
  final val KEY_REQUEST_PARAMETERS = "Parameters"
  final val templateModelAttribute = ".freemarker.TemplateModel"

  def createModel(wrapper: ObjectWrapper, request: HttpServletRequest, response: HttpServletResponse, params: Map[String, Any]): SimpleHash = {
    val existed = request.getAttribute(templateModelAttribute).asInstanceOf[SimpleHash]
    if (null == existed) {
      val model = new SimpleHttpScopeHashModel(wrapper, request)
      model.put("request", request)
      model.put(KEY_REQUEST_PARAMETERS, new ParametersHashModel(params, wrapper))
      tagLibraryProvider.tagLibraries foreach {
        case (tagName, tag) =>
          model.put(tagName, tag.models())
      }
      model.put("base", request.getContextPath)
      request.setAttribute(templateModelAttribute, model)
      model
    } else {
      existed
    }
  }
}
