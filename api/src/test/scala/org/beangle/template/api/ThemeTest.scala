package org.beangle.template.api

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers

class ThemeTest extends AnyFunSpec with Matchers {
  describe("Theme") {
    it("load") {
      assert(Themes.list.toSet == Set("bootstrap/list", "bootstrap/html", "ant/html", "ant/list"))
      assert(Themes.Default.name == "bootstrap/html")

      assert(Themes("list").name == "bootstrap/list")
      assert(Themes("ant/list").name == "ant/list")
    }
  }

}
