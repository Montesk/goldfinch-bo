package view

import akka.http.scaladsl.model.Uri

import org.scalatestplus.play.PlaySpec
import play.api.test.FakeRequest

class ViewSpec extends PlaySpec {
  "ProductView" must {
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
    "request with unsupported view query value must return exception" in {
      val req = FakeRequest(method = "GET", path = Uri.from(path = "/", queryString = Some("view=undefined")).toString())
      assertThrows[Products.UnsupportedViewException](Products.getViewFromRequest(req))
    }
  }
}