package example

import munit.CatsEffectSuite

class HelloSpec extends CatsEffectSuite {
  test("The Hello object should say hello") {
    assertIO_(Hello.run)
  }
}
