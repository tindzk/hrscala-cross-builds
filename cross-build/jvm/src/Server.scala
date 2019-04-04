import java.io.File

import io.circe.parser._
import io.circe.syntax._
import cats.effect._
import fs2.StreamApp
import fs2.Stream
import fs2.StreamApp.ExitCode
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.headers._
import org.http4s.MediaType._
import org.http4s.server.blaze._

import scala.language.higherKinds
import scala.concurrent.ExecutionContext.Implicits.global

import pine._

object Server extends StreamApp[IO] {
  val service = HttpService[IO] {
    case request @ GET -> Root / "js" / file =>
      scribe.info(s">>> GET /js/$file")
      StaticFile.fromFile(new File(file), Some(request)).getOrElseF(NotFound())

    case request @ POST -> _ =>
      val path = trail.Path(request.uri.path, request.params.toList)
      scribe.info(s">>> POST ${path.url}")

      path match {
        case Routes.api.logIn(()) =>
          processApiRequest[Protocol.LogInRequest](request) { r =>
            if (Validations
                  .logIn(r.username, r.password)
                  .isEmpty && r.username == "test") Protocol.LogInResponse(true)
            else
              Protocol.LogInResponse(false)
          }
        case _ => NotFound("Invalid API request")
      }

    case request @ GET -> _ =>
      val path = trail.Path(request.uri.path, request.params.toList)
      scribe.info(s">>> GET ${path.url}")

      import Render._

      fromUrl(path) match {
        case None =>
          NotFound(page(notFound()).toHtml, `Content-Type`(`text/html`))
        case Some(body) => Ok(page(body).toHtml, `Content-Type`(`text/html`))
      }
  }

  class ApiHelper[Req <: Request[_]](request: org.http4s.Request[IO]) {
    def apply[T](f: Req => T): IO[Response[IO]] =
      EntityDecoder.decodeString(request).flatMap { bodyString =>
        decode[Request[Any]](bodyString) match {
          case Left(_) => BadRequest("Malformed JSON payload")
          case Right(req) =>
            try {
              val response = f(req.asInstanceOf[Req]).asInstanceOf[Any]
              scribe.info(s"<<< $response")
              val responseJson = response.asJson(req.encoder).noSpaces
              Ok(responseJson, `Content-Type`(`application/json`))
            } catch {
              case e: Throwable =>
                e.printStackTrace()
                InternalServerError(e.toString)
            }
        }
      }
  }

  def processApiRequest[Req <: Request[_]](request: org.http4s.Request[IO]) =
    new ApiHelper[Req](request)

  override def stream(
    args: List[String],
    requestShutdown: IO[Unit]
  ): Stream[IO, ExitCode] =
    BlazeBuilder[IO].bindHttp(8000, "0.0.0.0").mountService(service).serve
}
