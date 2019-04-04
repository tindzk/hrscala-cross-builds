import trail._

object Routes {
  class UI(root: Route[Unit]) {
    val index = root
    val logIn = root / "logIn"
  }

  class API(root: Route[Unit]) {
    val logIn = root / "logIn"
  }

  val ui  = new UI(Root)
  val api = new API(Root / "api")
}
