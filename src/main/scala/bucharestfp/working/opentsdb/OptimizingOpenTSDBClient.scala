package bucharestfp
package working
package opentsdb

import cats.data.{ Reader, State }
import cats.Applicative
import bucharestfp.working.opentsdb.ExecutionPlan.ResultByRequest

object OptimizingOpenTSDBClient extends OpenTSDB[ExecutionPlan] {
  override def fetchOne(req: OpenTSDBRequest): ExecutionPlan[OpenTSDBResult] =
    ExecutionPlan.fetchOne(req)

  override def fetchAll(req: OpenTSDBRequest): ExecutionPlan[List[OpenTSDBResult]] =
    ??? // Left as an exercise to the reader.
}

final case class ExecutionPlan[A](state: State[List[OpenTSDBRequest], Reader[ResultByRequest, A]]) {
  def execute[F[_]: Applicative](tsdb: OpenTSDB[F]): F[A] = {
    val (seenRequests, backPropagator) = state.runEmpty.value

    val requestsByInterval: List[(List[OpenTSDBRequest], OpenTSDBRequest)] =
      seenRequests
        .groupBy(_.interval)
        .values
        .toList
        .map { requests =>
          val distinctRequests = requests.distinct
          val batchRequest = OpenTSDBRequest.batch(distinctRequests)

          distinctRequests -> batchRequest
        }

    requestsByInterval
      .map {
        case (originalRequests, batchRequest) =>
          tsdb
            .fetchAll(batchRequest)
            .map { results =>
              originalRequests
                .zip(results)
                .foldLeft(Map.empty: ResultByRequest) {
                  case (resultByRequest, (request, result)) =>
                    resultByRequest + (request -> result)
                }
            }
      }
      .sequence
      .map { resultsByRequest =>
        val resultByRequest =
          resultsByRequest.foldLeft(Map.empty: ResultByRequest)(_ ++ _)

        backPropagator.run(resultByRequest)
      }
  }
}

object ExecutionPlan {
  type ResultByRequest = Map[OpenTSDBRequest, OpenTSDBResult]

  def fetchOne(req: OpenTSDBRequest): ExecutionPlan[OpenTSDBResult] = {
    ExecutionPlan {
      for {
        seenRequests <- State.get
        _            <- State.set(req :: seenRequests)
      } yield {
        Reader(results => results(req))
      }
    }
  }

  implicit object ApplicativeExecutionPlan extends Applicative[ExecutionPlan] {
    override def pure[A](a: A): ExecutionPlan[A] =
      ExecutionPlan(State.pure(Reader(_ => a)))

    override def ap[A, B](ff: ExecutionPlan[A => B])(fa: ExecutionPlan[A]): ExecutionPlan[B] =
      ExecutionPlan {
        for {
          f <- ff.state
          a <- fa.state
        } yield {
          a.ap(f)
        }
      }
  }
}
