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

package org.beangle.template.aot

import org.beangle.commons.aot.AotHintRegistrar

/** template 的 GraalVM native-image 资源提示。
  *
  * 注册模板机制面的资源：freemarker 模板文件（`*.ftl`）由模板引擎在运行期
  * 按名加载，native 镜像需要内嵌。使用方无需在 resource-config.json 中手写。
  */
class TemplateAotHints extends AotHintRegistrar {

  override def registering(): Unit = {
    hints.registerPattern(".*\\.ftl")
  }
}
