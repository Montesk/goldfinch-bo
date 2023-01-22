package view.pagination

object Pagination {
  val defaultPerPage: Int = 5

  def pages(total: Int, perPage: Int): Int = {
    math.ceil(total.toDouble / perPage.toDouble).toInt
  }
}
