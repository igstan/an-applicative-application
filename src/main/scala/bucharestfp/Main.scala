package bucharestfp

import scala.concurrent.Await
import scala.concurrent.duration._
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import bucharestfp.consumer.{ MetricAConsumer, MetricBConsumer }
import bucharestfp.http.HTTPClient
import bucharestfp.opentsdb.OpenTSDBClient
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import play.api.libs.ws.ahc.StandaloneAhcWSClient

object Main {
  private lazy val system = ActorSystem()
  private lazy val materializer = ActorMaterializer()(system)
  private lazy val ws = StandaloneAhcWSClient()(materializer)

  def main(args: Array[String]): Unit = {
    val httpClient = new HTTPClient(ws)
    val tsdbClient = new OpenTSDBClient(httpClient)
    val metricAConsumer = new MetricAConsumer(tsdbClient)
    val metricBConsumer = new MetricBConsumer(tsdbClient)

    val taskA =
      metricAConsumer.run.map(total => println(s"Metric A Total: $total"))

    val taskB =
      metricBConsumer.run.map(total => println(s"Metric B Total: $total"))

    // Sequential HTTP calls.
    //val task = Task.sequence(List(taskA, taskB)).map(_ => ())

    // Parallel HTTP calls.
    val task = Task.gather(List(taskA, taskB)).map(_ => ())

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
