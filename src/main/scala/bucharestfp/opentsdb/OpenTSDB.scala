package bucharestfp
package opentsdb

import java.time.Instant
import play.api.libs.functional.syntax._
import play.api.libs.json.{ Format, Json, JsString, Reads, Writes }

trait OpenTSDB[F[_]] {
  def fetchOne(req: OpenTSDBRequest): F[OpenTSDBResult]
  def fetchAll(req: OpenTSDBRequest): F[List[OpenTSDBResult]]
}

case class OpenTSDBQuery(
  metric: String,
  aggregator: String = "none",
  tags: Map[String, String] = Map.empty,
)

object OpenTSDBQuery {
  implicit val format: Format[OpenTSDBQuery] = Json.format[OpenTSDBQuery]
}

case class OpenTSDBRequest(
  start: Instant,
  end: Instant,
  queries: List[OpenTSDBQuery],
) {
  def interval: Interval = start -> end
}

object OpenTSDBRequest {
  implicit val instantWrites: Writes[Instant] =
    Writes(instant => JsString(instant.getEpochSecond.toString))

  implicit val writes: Writes[OpenTSDBRequest] = Json.writes[OpenTSDBRequest]

  def batch(reqs: List[OpenTSDBRequest]): OpenTSDBRequest =
    reqs.reduceLeft[OpenTSDBRequest] { (batchedRequest, req) =>
      batchedRequest.copy(
        queries = batchedRequest.queries ++ req.queries
      )
    }
}

case class OpenTSDBResult(
  metric: String,
  tags: Map[String, String],
  aggregateTags: Set[String],
  dps: List[Datapoint]
)

object OpenTSDBResult {
  implicit val format: Format[OpenTSDBResult] = Json.format[OpenTSDBResult]
}

case class Datapoint(
  timestamp: Instant,
  value: Double,
)

object Datapoint {
  implicit val readsDatapoint: Reads[Datapoint] =
    implicitly[Reads[(Long, Double)]].map {
      case (timestamp, value) =>
        val ts = Instant.ofEpochSecond(timestamp)
        Datapoint(ts, value)
    }

  implicit val writesDatapoint: Writes[Datapoint] =
    implicitly[Writes[(Long, Double)]].contramap { d =>
      d.timestamp.getEpochSecond -> d.value
    }
}
