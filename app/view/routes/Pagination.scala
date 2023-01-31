package view.routes

import play.api.mvc.{AnyContent, Request}
import view.pagination.Pagination

trait Pagination {
  def currentPage(implicit req: Request[AnyContent]): Int = {
    req.getQueryString("page").getOrElse("1").toInt
  }

  def perPage(implicit req: Request[AnyContent]): Int = {
    req.getQueryString("perPage").getOrElse(Pagination.defaultPerPage.toString).toInt
  }
}
