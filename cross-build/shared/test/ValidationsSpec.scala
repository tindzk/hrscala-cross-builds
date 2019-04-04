import minitest.SimpleTestSuite

object ValidationsSpec extends SimpleTestSuite {
  test("Empty username") {
    assertEquals(
      Validations.logIn("", "test"),
      List("Username cannot be empty")
    )
  }

  test("Valid input") {
    assertEquals(Validations.logIn("hello", "world"), List())
  }
}
