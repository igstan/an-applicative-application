package bucharestfp
package working
package opentsdb

import monix.eval.Task
import bucharestfp.http.HTTPClient

final class NonOptimizingOpenTSDB(http: HTTPClient) extends OpenTSDB[Task] {
  override def fetchOne(req: OpenTSDBRequest): Task[OpenTSDBResult] =
    fetchAll(req).map(_.head)

  override def fetchAll(req: OpenTSDBRequest): Task[List[OpenTSDBResult]] = {
    val url = "http://localhost:4242/api/query?arrays"
    http.post[OpenTSDBRequest, List[OpenTSDBResult]](url, req)
  }
}
