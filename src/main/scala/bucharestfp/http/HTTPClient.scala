package bucharestfp
package http

import monix.eval.Task
import play.api.libs.json.{ Json, JsValue, Reads, Writes }
import play.api.libs.ws.JsonBodyReadables._
import play.api.libs.ws.JsonBodyWritables._
import play.api.libs.ws.ahc.StandaloneAhcWSClient

final class HTTPClient(ws: StandaloneAhcWSClient) {
  def post[W: Writes, R: Reads](url: String, body: W): Task[R] = {
    Task.deferFutureAction { implicit scheduler =>
      val jsonReq = Json.toJson(body)
      println(s"request: ${Json.prettyPrint(jsonReq)}")
      ws.url(url)
        .post(jsonReq)
        .map { response =>
          if (response.status / 100 === 2) {
            val jsonRes = response.body[JsValue]
            println(s"response: ${Json.prettyPrint(jsonRes)}")
            jsonRes.as[R]
          } else {
            throw new RuntimeException(s"HTTP request failed: ${response.status} ${response.body}")
          }
        }
    }
  }
}
