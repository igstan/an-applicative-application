package bucharestfp
package opentsdb

import play.api.libs.json.{ Format, Json }

case class OpenTSDBQuery(
  metric: String,
  aggregator: String = "none",
  tags: Map[String, String] = Map.empty,
)

object OpenTSDBQuery {
  implicit val format: Format[OpenTSDBQuery] = Json.format[OpenTSDBQuery]
}
