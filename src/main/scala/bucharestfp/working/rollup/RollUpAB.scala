package bucharestfp
package working
package rollup

import java.time.Instant
import cats.Applicative
import bucharestfp.working.opentsdb.{ OpenTSDB, OpenTSDBQuery, OpenTSDBRequest }

final class RollUpAB[F[_]: Applicative](tsdb: OpenTSDB[F]) {
  def run: F[Double] = {
    val metricA = OpenTSDBRequest(
      start = Instant.parse("2018-05-01T00:10:00Z"),
      end = Instant.parse("2018-05-01T00:10:05Z"),
      queries = List(
        OpenTSDBQuery(metric = "metric-a")
      )
    )

    val metricB = OpenTSDBRequest(
      start = Instant.parse("2018-05-01T00:10:00Z"),
      end = Instant.parse("2018-05-01T00:10:05Z"),
      queries = List(
        OpenTSDBQuery(metric = "metric-b")
      )
    )

    (
      tsdb.fetchOne(metricA),
      tsdb.fetchOne(metricB),
    ).mapN { (resultA, resultB) =>
      resultA.dps
        .zip(resultB.dps)
        .map {
          case (a, b) => a.value + b.value
        }
        .sum
    }
  }
}
