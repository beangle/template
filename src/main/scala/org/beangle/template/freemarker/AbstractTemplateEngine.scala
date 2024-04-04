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

import freemarker.core.ParseException
import freemarker.template.{Configuration, Template}
import org.beangle.commons.lang.Throwables
import org.beangle.commons.logging.Logging
import org.beangle.template.api.{TemplateEngine, TemplateRender}

import java.io.IOException

abstract class AbstractTemplateEngine extends TemplateEngine with Logging {
  /**
   * Load template
   */
  protected def getTemplate(config: Configuration, templateName: String): Template = {
    try {
      config.getTemplate(templateName, "UTF-8")
    } catch {
      case e: ParseException => throw e
      case e: IOException =>
        logger.error(s"Couldn't load template '$templateName',loader is ${config.getTemplateLoader.getClass}")
        throw Throwables.propagate(e)
    }
  }

  def forTemplate(template: String): TemplateRender = {
    val cfg = config
    new DefaultTemplateRender(cfg, getTemplate(cfg, template))
  }

  def config: Configuration

  def suffix: String = ".ftl"
}
