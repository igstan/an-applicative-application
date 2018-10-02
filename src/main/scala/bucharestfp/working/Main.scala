package bucharestfp
package working

import scala.concurrent.duration._
import scala.concurrent.Await
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import monix.eval.Task
import monix.execution.Scheduler.Implicits.global
import play.api.libs.ws.ahc.StandaloneAhcWSClient
import bucharestfp.http.HTTPClient
import bucharestfp.working.opentsdb.{ ExecutionPlan, NonOptimizingOpenTSDB, OptimizingOpenTSDBClient }
import bucharestfp.working.rollup.{ RollUpAB, RollUpAC }

object Main {
  private lazy val system = ActorSystem()
  private lazy val materializer = ActorMaterializer()(system)
  private lazy val ws = StandaloneAhcWSClient()(materializer)

  def main(args: Array[String]): Unit = {
    val mode = args.headOption.getOrElse("unoptimized")

    val task: Task[Unit] =
      if (mode == "optimized") optimized else unoptimized

    run(task)
  }

  def unoptimized: Task[Unit] = {
    val httpClient = new HTTPClient(ws)
    val tsdbClient = new NonOptimizingOpenTSDB(httpClient)
    val rollupAB = new RollUpAB(tsdbClient)
    val rollupAC = new RollUpAC(tsdbClient)

    val taskAB: Task[Unit] =
      rollupAB.run.map(total => println(s"Roll-Up AB: $total"))

    val taskAC: Task[Unit] =
      rollupAC.run.map(total => println(s"Roll-Up AC: $total"))

    // Sequential HTTP calls.
    List(taskAB, taskAC).sequence_

    // Parallel HTTP calls.
    //List(taskAB, taskAC).parSequence.map(_ => ())
  }

  def optimized: Task[Unit] = {
    val httpClient = new HTTPClient(ws)
    val tsdbClient = new NonOptimizingOpenTSDB(httpClient)
    val optimizingTSDBClient = OptimizingOpenTSDBClient
    val rollupAB = new RollUpAB(optimizingTSDBClient)
    val rollupAC = new RollUpAC(optimizingTSDBClient)

    val taskAB: ExecutionPlan[Unit] =
      rollupAB.run.map(total => println(s"Roll-Up AB: $total"))

    val taskAC: ExecutionPlan[Unit] =
      rollupAC.run.map(total => println(s"Roll-Up AC: $total"))

    val tasks: List[ExecutionPlan[Unit]] = List(taskAB, taskAC)
    //val executionPlan: ExecutionPlan[List[Unit]] = tasks.sequence
    val executionPlan: ExecutionPlan[Unit] = tasks.sequence_

    val task = executionPlan.execute(tsdbClient)

    task.map(_ => ())
  }

  private def run(task: Task[Unit]): Unit = {
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
