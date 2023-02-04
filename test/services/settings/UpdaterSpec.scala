package services.settings

import org.scalatestplus.play.PlaySpec

import scala.xml.Elem

class UpdaterSpec extends PlaySpec {
  val successUpdate: (Elem, Options) => (Boolean, Elem) = { (_, _) =>
    (true, null)
  }

  val failedUpdate: (Elem, Options) => (Boolean, Elem) = { (_, _) =>
    (false, null)
  }

  val updateWithException: (Elem, Options) => (Boolean, Elem) = { (_, _) =>
    throw new Exception("exception while performing version update")
  }

  "Updater" must {
    "perform single update (single version up)" in {
      val ms = new MockService(null, successUpdate)

      val (success, _) = ms.update(
        currentVersion = "0.0.3",
        targetVersion = "0.0.4",
        currentSettings = null,
        defaults = null,
      )

      success mustBe true
    }
    "perform multi-update" in {
      val ms = new MockService(successUpdate, successUpdate)

      val (success, _) = ms.update(
        currentVersion = "0.0.2",
        targetVersion = "0.0.4",
        currentSettings = null,
        defaults = null,
      )

      success mustBe true
    }
    "perform no update (versions are equal - success true)" in {
      val ms = new MockService(updateWithException, updateWithException)

      val (success, _) = ms.update(
        currentVersion = "0.0.4",
        targetVersion = "0.0.4",
        currentSettings = null,
        defaults = null,
      )

      success mustBe true
    }
    "no update - broken logic in single update" in {
      val ms = new MockService(null, failedUpdate)

      val (success, _) = ms.update(
        currentVersion = "0.0.3",
        targetVersion = "0.0.4",
        currentSettings = null,
        defaults = null,
      )

      success mustBe false
    }
    "no update - exception in logic in single update" in {
      val ms = new MockService(null, updateWithException)

      val (success, _) = ms.update(
        currentVersion = "0.0.3",
        targetVersion = "0.0.4",
        currentSettings = null,
        defaults = null,
      )

      success mustBe false
    }
    "no update - broken logic in update chain (1st update)" in {
      val ms = new MockService(failedUpdate, successUpdate)

      val (success, _) = ms.update(
        currentVersion = "0.0.2",
        targetVersion = "0.0.4",
        currentSettings = null,
        defaults = null,
      )

      success mustBe false
    }
    "no update - broken logic in update chain (2nd update)" in {
      val ms = new MockService(successUpdate, failedUpdate)

      val (success, _) = ms.update(
        currentVersion = "0.0.2",
        targetVersion = "0.0.4",
        currentSettings = null,
        defaults = null,
      )

      success mustBe false
    }
    "no update - broken logic in update chain (all updates)" in {
      val ms = new MockService(failedUpdate, failedUpdate)

      val (success, _) = ms.update(
        currentVersion = "0.0.2",
        targetVersion = "0.0.4",
        currentSettings = null,
        defaults = null,
      )

      success mustBe false
    }
    "no update - broken logic in update chain (all with exception)" in {
      val ms = new MockService(updateWithException, updateWithException)

      val (success, _) = ms.update(
        currentVersion = "0.0.2",
        targetVersion = "0.0.4",
        currentSettings = null,
        defaults = null,
      )

      success mustBe false
    }
  }
}

class MockService(
    update_v0_0_3: (Elem, Options) => (Boolean, Elem),
    update_v0_0_4: (Elem, Options) => (Boolean, Elem),
) extends Updater {
  override def v0_0_3(prev: Elem, defaults: Options): (Boolean, Elem) = update_v0_0_3(prev, defaults)

  override def v0_0_4(prev: Elem, defaults: Options): (Boolean, Elem) = update_v0_0_4(prev, defaults)
}
