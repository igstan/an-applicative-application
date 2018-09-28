package bucharestfp
package opentsdb

import play.api.libs.json.{ Format, Json }

case class OpenTSDBResult(
  metric: String,
  tags: Map[String, String],
  aggregateTags: Set[String],
  dps: List[Datapoint]
)

object OpenTSDBResult {
  implicit val format: Format[OpenTSDBResult] = Json.format[OpenTSDBResult]
}
