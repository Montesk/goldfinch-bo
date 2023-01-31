package filters

import javax.inject.Inject
import akka.stream.Materializer

import services.settings.Settings

import scala.concurrent.{ExecutionContext, Future}
import play.api.Logging
import play.api.mvc.Results.Redirect
import play.api.mvc._

class StartPageFilter @Inject() (implicit
    val mat: Materializer,
    ec: ExecutionContext,
) extends Filter
    with Logging {
  def apply(nextFilter: RequestHeader => Future[Result])(requestHeader: RequestHeader): Future[Result] = {
    nextFilter(requestHeader).map { result =>
      val redirected: Boolean = requestHeader.cookies.get("GOLDFINCH_START_PAGE").isDefined

      if (!redirected) {
        Redirect(Settings.startPage()).withCookies(Cookie("GOLDFINCH_START_PAGE", "true"))
      } else {
        result
      }
    }
  }
}
