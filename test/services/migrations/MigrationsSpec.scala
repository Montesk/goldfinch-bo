package services.migrations

import org.scalatestplus.play.PlaySpec

class MigrationsSpec extends PlaySpec {
  {
    "Migrations" must {
      "generate real migrations info without exception" in {
        noException mustBe thrownBy(Migrations.generateInfo())
      }
    }
  }
}
