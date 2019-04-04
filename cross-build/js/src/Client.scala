import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import pine._
import pine.dom._

import trail._

import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.window

import io.circe.parser._
import io.circe.syntax._

object API {
  def request[T](url: String, request: Request[T])(result: T => Unit): Unit =
    Ajax
      .post(url, request.asJson.noSpaces, withCredentials = true)
      .flatMap(
        result =>
          Future.fromTry(decode(result.responseText)(request.decoder).toTry)
      )
      .foreach(result)
}

object Manage {
  def logIn(): Unit = {
    val username  = TagRef.ById[tag.Input]("username")
    val password  = TagRef.ById[tag.Input]("password")
    val errorsMsg = TagRef.ById[tag.Div]("errors")

    TagRef.ById[tag.Button]("logIn").click := {
      DOM.render { implicit ctx =>
        val errors = Validations.logIn(username.dom.value, password.dom.value)
        errorsMsg.hide(errors.isEmpty)

        if (errors.nonEmpty) errorsMsg := errors.map(t => tag.Div.set(Text(t)))
        else
          API.request(
            Routes.api.logIn(()),
            Protocol.LogInRequest(username.dom.value, password.dom.value)
          ) {
            case Protocol.LogInResponse(true) =>
              window.alert("Log in successful!")

            case Protocol.LogInResponse(false) =>
              DOM.render { implicit ctx =>
                errorsMsg := tag.Div.set("Log in failed")
              }
          }
      }
    }
  }

  def fromUrl(path: Path): Unit =
    path match {
      case Routes.ui.logIn(()) => logIn()
      case _                   =>
    }
}

object Client {
  def render(body: Node): Unit =
    DOM.render { implicit ctx =>
      TagRef.ById("body") := body
    }

  def main(args: Array[String]): Unit = {
    val path = PathParser.parse(window.location.pathname)
    Manage.fromUrl(path)
  }
}
