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

import freemarker.template.Configuration
import org.beangle.commons.aot.{AotHintRegistrar, AotPolicy}
import org.beangle.template.api.*

/** template 的 GraalVM native-image 资源提示。
 *
 * 注册模板机制面的资源：freemarker 模板文件（`*.ftl`）由模板引擎在运行期
 * 按名加载，native 镜像需要内嵌；`themes` 目录下主题资源（theme.properties、
 * themes.list）供 `Themes` 精确名查找（native 下目录枚举不可用）。
 * 使用方无需在 resource-config.json 中手写。
 * 另注册 UIBean 层级：tag 组件（bui 等）继承 `Component`/`UIBean`，`TagModel`
 * 经 `clazz.getConstructor(classOf[ComponentContext])` 实例化、`Properties.set`
 * 经 `BeanInfos` 反射 dig 属性（Scala var 依赖 declared 成员），父类成员也需注册。
 */
class TemplateAotHints extends AotHintRegistrar {

  private val componentPolicy = AotPolicy(Set(
    AotPolicy.Category.PublicConstructors,
    AotPolicy.Category.DeclaredMethods,
    AotPolicy.Category.DeclaredFields))

  override def registering(): Unit = {
    hints.registerPattern(".*\\.ftl")
    hints.registerPattern("themes/.*")
    // 模板模型基类
    hints.registerType(classOf[AbstractModels])
    hints.registerType(classOf[Configuration])
    hints.registerType(classOf[Component], componentPolicy)
    hints.registerType(classOf[ComponentContext], componentPolicy)
    hints.registerType(classOf[UIBean], componentPolicy)
    hints.registerType(classOf[ClosingUIBean], componentPolicy)
    hints.registerType(classOf[IterableUIBean], componentPolicy)

    // 模板引擎与 API
    hints.registerType(
      classOf[org.beangle.template.api.ComponentContextAware],
      classOf[org.beangle.template.api.ModelBuilder],
      classOf[org.beangle.template.api.TagLibrary],
      classOf[org.beangle.template.api.TagLibraryProvider],
      classOf[org.beangle.template.api.TagTemplateEngine])

    // FreeMarker 引擎实现
    hints.registerType(
      classOf[org.beangle.template.freemarker.AbstractTemplateEngine],
      classOf[org.beangle.template.freemarker.Configurator])
  }
}
