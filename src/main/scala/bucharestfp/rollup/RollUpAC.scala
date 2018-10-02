package bucharestfp
package rollup

import java.time.Instant
import cats.Applicative
import bucharestfp.opentsdb.{ OpenTSDB, OpenTSDBQuery, OpenTSDBRequest, OpenTSDBResult }

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

    val resultA = tsdb.fetchOne(metricA)
    val resultC = tsdb.fetchOne(metricC)

    (resultA, resultC).mapN(rollup)
  }

  private def rollup(resultA: OpenTSDBResult, resultC: OpenTSDBResult): Double =
    resultA
      .dps
      .zip(resultC.dps)
      .map { case (a, c) => a.value + c.value }
      .sum
}
