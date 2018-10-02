package bucharestfp
package scratch

import cats.data.State
import cats.Applicative

object WeirdCounter {
  trait Counter[F[_]] {
    def increment: F[Int]
  }

  def usage[F[_] : Applicative](counter: Counter[F]): F[Unit] = {
    (
      counter.increment,
      counter.increment,
      counter.increment,
    ).mapN { (a, b, c) =>
      // a, b and c should all be 3, not 1, 2 and 3
      println(s"""
      | a: $a
      | b: $b
      | c: $c
      """.stripMargin)
    }
  }

  object NormalCounter extends Counter[State[Int, ?]] {
    override def increment: State[Int, Int] =
      State.modify[Int](_ + 1).flatMap(_ => State.get)

    def run[A](s: State[Int, A]): A =
      s.runA(0).value
  }

  object AccumulatingCounter extends Counter[Accumulator] {
    override def increment: Accumulator[Int] = {
      Accumulator {
        State
          .modify[Int](total => total + 1)
          .map { _ =>
            total => total
          }
      }
    }

    def run[A](as: Accumulator[A]): A = {
      val (total, reader) = as.state.run(0).value
      reader(total)
    }
  }

  final case class Accumulator[A](state: State[Int, Int => A])

  object Accumulator {
    implicit object AccStateApplicative extends Applicative[Accumulator] {
      override def pure[A](a: A): Accumulator[A] =
        Accumulator(State.pure(_ => a))

      override def ap[A, B](ff: Accumulator[A => B])(fa: Accumulator[A]): Accumulator[B] = {
        Accumulator {
          for {
            f <- ff.state
            a <- fa.state
          } yield {
            total: Int => f(total)(a(total))
          }

          //ff.state.flatMap { ff: Reader[Int, A => B] =>
          //  fa.state.map { fa: Reader[Int, A] =>
          //    fa.ap(ff): Reader[Int, B]
          //  }
          //}
        }
      }
    }
  }

  def main(args: Array[String]): Unit = {
    NormalCounter.run(usage(NormalCounter))
    AccumulatingCounter.run(usage(AccumulatingCounter))
  }
}
