package services.config

import io.circe.DecodingFailure
import org.scalatestplus.play.PlaySpec

import scala.concurrent.duration.{Duration, FiniteDuration, SECONDS}

class ConfigSpec extends PlaySpec {
  "Config" must {
    "read all repositories settings from YAML config - ok" in {
      val ymlString =
        """
          |app:
          |  theme: "green"
          |repository:
          |  defaults:
          |    dbWaitTimeout: "12s"
          |  product:
          |    dbWaitTimeout: "13s"
          |  delivery:
          |    dbWaitTimeout: "14s"
          |  order:
          |    dbWaitTimeout: "15s"
          |""".stripMargin

      val expect = Repositories(
        defaults = Repository(Duration("12s")),
        product = Option(Repository(Duration("13s"))),
        delivery = Option(Repository(Duration("14s"))),
        order = Option(Repository(Duration("15s"))),
      )

      Config.readConfig(ymlString)

      val got = Config.repositories()

      got mustBe expect
    }

    "read some repositories settings from YAML config - ok" in {
      val ymlString =
        """
          |app:
          |  theme: "green"
          |repository:
          |  defaults:
          |    dbWaitTimeout: "12s"
          |  product:
          |    dbWaitTimeout: "13s"
          |""".stripMargin

      val expect = Repositories(
        defaults = Repository(Duration(12, SECONDS)),
        product = Option(Repository(Duration(13, SECONDS))),
      )

      Config.readConfig(ymlString)

      val got = Config.repositories()

      got mustBe expect
    }

    "read only required field from YAML config - ok" in {
      val ymlString =
        """
          |app:
          |  theme: "green"
          |repository:
          |  defaults:
          |    dbWaitTimeout: "5s"
          |""".stripMargin

      val expect = Repositories(
        defaults = Repository(Duration(5, SECONDS))
      )

      Config.readConfig(ymlString)

      val got = Config.repositories()

      got mustBe expect
    }

    "missing required field - fail" in {
      val ymlString =
        """
          |repository:
          |  defaults:
          |    # dbWaitTimeout: 0
          |""".stripMargin

      val expectException = a[DecodingFailure]
      expectException mustBe thrownBy(Config.readConfig(ymlString))
    }

    "missing only required field - fail" in {
      val ymlString =
        """
          |app:
          |  theme: "green"
          |repository:
          |  defaults:
          |    # dbWaitTimeout: "12s"
          |  product:
          |    dbWaitTimeout: "13s"
          |  delivery:
          |    dbWaitTimeout: "14s"
          |  order:
          |    dbWaitTimeout: "15s"
          |""".stripMargin

      val expectException = a[DecodingFailure]

      expectException mustBe thrownBy(Config.readConfig(ymlString))
    }

    "missing only required field and object - fail" in {
      val ymlString =
        """
          |app:
          |  theme: "green"
          |repository:
          |  product:
          |    dbWaitTimeout: "13s"
          |  delivery:
          |    dbWaitTimeout: "14s"
          |  order:
          |    dbWaitTimeout: "15s"
          |""".stripMargin

      val expectException = a[DecodingFailure]

      expectException mustBe thrownBy(Config.readConfig(ymlString))
    }

    "invalid format for required config field - fail" in {
      val ymlString =
        """
          |app:
          |  theme: "green"
          |repository:
          |  defaults:
          |    dbWaitTimeout: 13
          |""".stripMargin

      val expectException = a[DecodingFailure]

      expectException mustBe thrownBy(Config.readConfig(ymlString))
    }
  }
}