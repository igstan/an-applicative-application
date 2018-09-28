package bucharestfp
package opentsdb

import java.time.Instant
import play.api.libs.json.{ Json, JsString, Writes }

final case class OpenTSDBRequest(
  start: Instant,
  end: Instant,
  queries: List[OpenTSDBQuery],
)

object OpenTSDBRequest {
  implicit val instantWrites: Writes[Instant] =
    Writes(instant => JsString(instant.getEpochSecond.toString))

  implicit val writes: Writes[OpenTSDBRequest] = Json.writes[OpenTSDBRequest]
}
