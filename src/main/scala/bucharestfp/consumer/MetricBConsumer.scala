package bucharestfp
package consumer

import java.time.Instant
import bucharestfp.opentsdb.{ OpenTSDBClient, OpenTSDBQuery, OpenTSDBRequest }
import monix.eval.Task

final class MetricBConsumer(tsdb: OpenTSDBClient) {
  def run: Task[Double] = {
    val request = OpenTSDBRequest(
      start = Instant.parse("2018-05-01T00:10:00Z"),
      end = Instant.parse("2018-05-01T00:10:05Z"),
      queries = List(
        OpenTSDBQuery(metric = "metric-b")
      )
    )

    tsdb.fetch(request).map {
      case List(result) => result.dps.map(_.value).sum
      case _ => ???
    }
  }
}
