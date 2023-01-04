package services.cache

import org.scalatestplus.play.PlaySpec

case class SomeObject(value: Int)

class CacheSpec extends PlaySpec {
  "Cache segments read and update" must {
    "cache get existing key" in {
      val ch = new Cache
      ch.update("some_object", SomeObject(value = 18))
      val got = ch.get[SomeObject]("some_object")
      got.value mustEqual 18
    }

    "cache get or else existing key" in {
      val ch = new Cache
      ch.get("settings", SomeObject(value = 9)).value mustEqual SomeObject(value = 9).value
    }

    "cache get or call" in {
      val ch = new Cache
      val got = ch.getOrCall("settings", () => SomeObject(value = 20))
      got.value mustEqual 20
    }

    "cache updated" in {
      val ch = new Cache
      ch.get("settings", SomeObject(value = 9))
      ch.update("settings", SomeObject(value = 10))

      ch.get[SomeObject]("settings").value mustEqual 10
    }

    "throw exception (no data found by this key)" in {
      val ch = new Cache
      a[NoSuchElementException] must be thrownBy ch.get[SomeObject]("some_object")
    }
  }
}
