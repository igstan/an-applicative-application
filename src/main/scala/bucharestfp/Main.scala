package bucharestfp

import java.time.Instant
import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import bucharestfp.http.HTTPClient
import bucharestfp.opentsdb.{ OpenTSDBClient, OpenTSDBQuery, OpenTSDBRequest }
import monix.execution.Scheduler.Implicits.global
import play.api.libs.ws.ahc.StandaloneAhcWSClient

object Main {
  private lazy val system = ActorSystem()
  private lazy val materializer = ActorMaterializer()(system)
  private lazy val ws = StandaloneAhcWSClient()(materializer)

  def main(args: Array[String]): Unit = {
    val httpClient = new HTTPClient(ws)
    val tsdbClient = new OpenTSDBClient(httpClient)

    val task =
      tsdbClient
        .fetch(OpenTSDBRequest(
          start = Instant.parse("2018-05-01T00:10:00Z"),
          end = Instant.parse("2018-05-01T00:10:05Z"),
          queries = List(
            OpenTSDBQuery(metric = "metric-a"),
            OpenTSDBQuery(metric = "metric-b"),
          )
        ))
        .map { response =>
          println(s"response: $response")
        }

    try {
      task.runSyncUnsafe(timeout = 5.seconds)
    } catch {
      case e: Exception => e.printStackTrace()
    } finally {
      shutdown()
    }
  }

  private def shutdown(): Unit = {
    ws.close()
    materializer.shutdown()
    Await.result(system.terminate(), Duration.Inf)
    ()
  }
}
