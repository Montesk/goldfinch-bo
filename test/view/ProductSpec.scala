package view

import akka.http.scaladsl.model.Uri
import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest

class ProductSpec extends PlaySpec {
  "ProductView" must {
    "product makes link with identifier" in {
      val expect = Product.ProductPath(10)
      Product.uriTo(10) mustBe expect
    }
    "product makes link with identifier and saves the query" in {
      val expect = s"${Product.ProductPath(10)}?view=custom"
      val req = FakeRequest(method = "GET", path = Uri.from(path = "/", queryString = Some("view=custom")).toString())
      Product.saveQueryUriTo(10, req) mustBe expect
    }
  }
}