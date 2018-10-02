package bucharestfp
package working
package rollup

import java.time.Instant
import cats.Applicative
import bucharestfp.working.opentsdb.{ OpenTSDB, OpenTSDBQuery, OpenTSDBRequest }

final class RollUpAC[F[_]: Applicative](tsdb: OpenTSDB[F]) {
  def run: F[Double] = {
    val metricA = OpenTSDBRequest(
      start = Instant.parse("2018-05-01T00:10:00Z"),
      end = Instant.parse("2018-05-01T00:10:05Z"),
      queries = List(
        OpenTSDBQuery(metric = "metric-a")
      )
    )

    val metricC = OpenTSDBRequest(
      start = Instant.parse("2018-05-01T00:10:00Z"),
      end = Instant.parse("2018-05-01T00:10:05Z"),
      queries = List(
        OpenTSDBQuery(metric = "metric-c")
      )
    )

    (
      tsdb.fetchOne(metricA),
      tsdb.fetchOne(metricC),
    ).mapN { (resultA, resultC) =>
      resultA.dps
        .zip(resultC.dps)
        .map {
          case (a, c) => a.value + c.value
        }
        .sum
    }
  }
}
