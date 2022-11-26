package view.routes

import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest
import play.api.test.Helpers.GET

class MatcherSpec extends PlaySpec {
  "Matcher" must {
    "match simple predefined in routes page" in {
      Matcher.isHomePage(FakeRequest(GET, "/")) must be(true)
    }

    "match dynamic path group" in {
      Matcher.isInProductEditGroupPage(FakeRequest(GET, "/product/edit/10")) must be(true)
    }

    "match constant path" in {
      Matcher.isInProductGroupPage(FakeRequest(GET, "/product")) must be(true)
    }

    "not match static path" in {
      Matcher.isWarehouseCreatePage(FakeRequest(GET, "/product")) must be(false)
    }

    "not match same parent group but different child path" in {
      Matcher.isProductCreatePage(FakeRequest(GET, "/product/edit/20")) must be(false)
    }
  }
}
