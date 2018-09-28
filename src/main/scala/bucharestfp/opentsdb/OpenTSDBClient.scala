package bucharestfp
package opentsdb

import bucharestfp.http.HTTPClient
import monix.eval.Task

final class OpenTSDBClient(http: HTTPClient) {
  def fetch(req: OpenTSDBRequest): Task[List[OpenTSDBResult]] = {
    val url = "http://localhost:4242/api/query"
    http.post[OpenTSDBRequest, List[OpenTSDBResult]](url, req, queryParams = Seq(
      "arrays" -> ""
    ))
  }
}
