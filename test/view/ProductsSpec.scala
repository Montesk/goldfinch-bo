package view

import akka.http.scaladsl.model.Uri

import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest

class ProductsSpec extends PlaySpec {
  "ProductsView" must {
    "request with tableView query must return isTableView()" in {
      val req = FakeRequest(method = "GET", path = Uri.from(path = "/", queryString = Some(s"view=${Products.tableView}")).toString())
      Products.isTableView(req) mustBe (true)
    }
    "request with albumView query must return isAlbumView()" in {
      val req = FakeRequest(method = "GET", path = Uri.from(path = "/", queryString = Some(s"view=${Products.albumView}")).toString())
      Products.isAlbumView(req) mustBe (true)
    }
    "request with empty query must return default view" in {
      val req = FakeRequest(method = "GET", path = Uri.from(path = "/").path.toString())
      Products.isTableView(req) mustBe (true)
    }
    "request products with query table view" in {
      val expect = s"${Products.ProductsPath}?view=${Products.tableView}"
      Products.withQuery(Products.ProductsPath, Some(s"view=${Products.tableView}")) mustBe expect
    }
    "request products with query album view" in {
      val expect = s"${Products.ProductsPath}?view=${Products.albumView}"
      Products.withQuery(Products.ProductsPath, Some(s"view=${Products.albumView}")) mustBe expect
    }
    "request product with some query" in {
      val expect = s"${Products.ProductsPath}?key=value"
      Products.withQuery(Products.ProductsPath, Some("key=value")) mustBe expect
    }
    "request with unsupported view query value must return excpection" in {
      val req = FakeRequest(method = "GET", path = Uri.from(path = "/", queryString = Some("view=undefined")).toString())
      assertThrows[Products.UnsupportedViewException](Products.getViewFromRequest(req))
    }
  }
}