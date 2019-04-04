import io.circe.Decoder
import io.circe.Encoder
import io.circe.ObjectEncoder

sealed abstract class Request[T](
  implicit val encoder: Encoder[T],
  val decoder: Decoder[T]
)

object Protocol {
  import io.circe.generic.auto._
  case class LogInRequest(username: String, password: String)
      extends Request[LogInResponse]
  case class LogInResponse(success: Boolean)
}

object Request {
  import io.circe.generic.semiauto._
  implicit def encodeRequest[T]: ObjectEncoder[Request[T]] =
    deriveEncoder[Request[_]].asInstanceOf[ObjectEncoder[Request[T]]]
  implicit def decodeRequest[T]: Decoder[Request[T]] =
    deriveDecoder[Request[_]].asInstanceOf[Decoder[Request[T]]]
}
