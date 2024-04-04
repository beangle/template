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

import org.beangle.commons.io.{IOs, ResourcePatternResolver}
import org.beangle.commons.lang.Strings

object Themes {

  private val themes = loadThemeProps()
  private var defaultPrefix: String = null
  val Default: Theme = loadDefault()

  def list: List[String] = themes.keys.toList

  def getParentTemplate(template: String): String = {
    val start = template.indexOf('/', 1) + 1
    val end = template.lastIndexOf('/')

    themes(template.substring(start, end)).parent match {
      case Some(parentTheme) => Strings.concat(template.substring(0, start), parentTheme, template.substring(end))
      case None => null
    }
  }

  private def loadThemeProps(): Map[String, Theme] = {
    val themePropMap = new collection.mutable.HashMap[String, Theme]
    val resolver = new ResourcePatternResolver
    val urls = resolver.getResources("themes/**/theme.properties")
    urls foreach { url =>
      val themeName = Strings.substringBetween(url.getPath, "themes/", "/theme.properties")
      var parent = IOs.readJavaProperties(url).get("parent").orNull
      if Strings.isBlank(parent) then parent = null
      if (themeName.contains("/") && Strings.isNotBlank(parent) && !parent.contains("/")) {
        parent = Strings.substringBefore(themeName, "/") + "/" + parent.trim
      }
      themePropMap.put(themeName, Theme(themeName, Option(parent)))
    }
    themePropMap.toMap
  }

  private def loadDefault(): Theme = {
    val resolver = new ResourcePatternResolver
    val urls = resolver.getResources("themes/default.properties")
    val defaultTheme =
      if (urls.isEmpty) {
        val tops = themes.filter(_._2.parent.isEmpty)
        if tops.size == 1 then tops.head._2 else Theme("notdefined")
      } else {
        val defaultName = IOs.readJavaProperties(urls.head).getOrElse("default", "notdefined")
        Theme(defaultName)
      }
    defaultPrefix = defaultTheme.prefix
    defaultTheme
  }

  def apply(name: String): Theme = {
    if (defaultPrefix == null) {
      themes(name)
    } else {
      if name.contains("/") then themes(name) else themes(defaultPrefix + name)
    }
  }

}

/**
 * name: Theme's name ,html,list,xhtml etc.
 */
class Theme(val name: String, val parent: Option[String] = None) {

  def getTemplatePath(clazz: Class[_], suffix: String): String = {
    val sb = new StringBuilder(20)
    sb.append("/themes/").append(name).append('/').append(Strings.uncapitalize(clazz.getSimpleName)).append(suffix)
    sb.toString()
  }

  def prefix: String = {
    if name.contains("/") then Strings.substringBeforeLast(name,"/") + "/" else null
  }

  override def equals(obj: Any): Boolean = name.equals(obj.toString)

  override def toString: String = name

  override def hashCode: Int = name.hashCode()
}
