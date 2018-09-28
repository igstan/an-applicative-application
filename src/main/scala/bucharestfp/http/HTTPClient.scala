package bucharestfp
package http

import cats.implicits._
import monix.eval.Task
import play.api.libs.json.{ Json, JsValue, Reads, Writes }
import play.api.libs.ws.{ JsonBodyReadables, JsonBodyWritables }
import play.api.libs.ws.ahc.StandaloneAhcWSClient

final class HTTPClient(ws: StandaloneAhcWSClient) extends JsonBodyWritables with JsonBodyReadables {
  def post[W: Writes, R: Reads](url: String, body: W, queryParams: Seq[(String, String)] = Seq.empty): Task[R] = {
    Task.deferFutureAction { implicit scheduler =>
      val jsonReq = Json.toJson(body)

      println(s"request: ${Json.prettyPrint(jsonReq)}")

      ws.url(url)
        .addQueryStringParameters(queryParams:_*)
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
