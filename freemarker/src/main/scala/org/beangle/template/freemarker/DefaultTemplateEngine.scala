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

import freemarker.cache.StrongCacheStorage
import freemarker.template.Configuration

import java.io.Writer

object DefaultTemplateEngine {
  def apply(): DefaultTemplateEngine = {
    val configer = new Configurer()
    configer.init()
    configer.config.setCacheStorage(new StrongCacheStorage())
    new DefaultTemplateEngine(configer.config)
  }
}

class DefaultTemplateEngine(val config: Configuration) extends AbstractTemplateEngine {

  @throws(classOf[Exception])
  override def renderTo(template: String, model: Any, writer: Writer): Unit = {
    forTemplate(template).renderTo(model, writer)
  }

}


