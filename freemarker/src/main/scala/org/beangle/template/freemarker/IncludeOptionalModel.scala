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

import freemarker.core.Environment
import freemarker.template.{TemplateDirectiveBody, TemplateDirectiveModel, TemplateModel}

/** 如果存在则包含
 * @see {@code .get_optional_template(name, options)} and GetOptionalTemplateMethod
 */
class IncludeOptionalModel extends TemplateDirectiveModel {

  override def execute(env: Environment, map: java.util.Map[_, _], templateModel: Array[TemplateModel],
                       directiveBody: TemplateDirectiveBody): Unit = {
    val currentTemplateName = env.getCurrentNamespace.getTemplate.getName
    val path = map.get("path").toString
    val fullTemplatePath = env.toFullTemplateName(currentTemplateName, path)

    val templateLoader = env.getConfiguration.getTemplateLoader
    if (templateLoader.findTemplateSource(fullTemplatePath) != null) {
      env.include(env.getTemplateForInclusion(fullTemplatePath, null, true));
    } else {
      if directiveBody != null then directiveBody.render(env.getOut)
    }
  }
}
