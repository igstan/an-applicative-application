//package bucharestfp
//package scratch
//
//import cats.Applicative
//import monix.eval.Task
//import bucharestfp.opentsdb.OpenTSDBResult
//
//object Playground {
//  def main(args: Array[String]): Unit = {
//    {
//      def example[A, B](f: A => B, a: A): B =
//        f(a)
//    }
//
//    {
//      def example[F[_]: Applicative, A, B](f: A => B, a: A): B = {
//        val ff = Applicative[F].pure(f)
//        val fa = Applicative[F].pure(a)
//        fa.ap(ff)
//      }
//    }
//
//    {
//      // Independent computations
//      val resultAB = rollupAB(metricA(), metricB())
//      val resultAC = rollupAC(metricA(), metricC())
//    }
//
//    {
//      // Optimize: common subexpression elimination.
//      val metricA = metricA()
//      val resultAB = rollupAB(mA, metricB())
//      val resultAC = rollupAC(mA, metricC())
//    }
//
//
//    {
//      def rollupAB(metricA: OpenTSDBResult, metricB: OpenTSDBResult): Double = ???
//      def rollupAC(metricA: OpenTSDBResult, metricC: OpenTSDBResult): Double = ???
//
//      def fetchMetricA: Task[OpenTSDBResult] = ???
//      def fetchMetricB: Task[OpenTSDBResult] = ???
//      def fetchMetricC: Task[OpenTSDBResult] = ???
//
//      def rollupAEffectful: Task[Double] =
//        (fetchMetricA, fetchMetricB).mapN {
//          (metricA, metricB) => rollupAB(metricA, metricB)
//        }
//
//      def rollupBEffectful: Task[Double] =
//        (fetchMetricA, fetchMetricC).mapN {
//          (metricA, metricC) => rollupAC(metricA, metricC)
//        }
//
//      val rollups: Task[List[Double]] =
//        List(rollupAEffectful, rollupBEffectful).sequence[Task, Double]
//    }
//
//
//    //def rollup(
//    //  metricA: List[Datapoint],
//    //  metricC: List[Datapoint],
//    //  ...
//    //  metricZ: List[Datapoint],
//    //): Double
//
//
//    {
//// Possibly effectful, but we don't know.
//def foo(): String = ???
//def bar(): String = ???
//
//// No data dependency between function calls.
//val a = foo()
//val b = bar()
//    }
//
//    {
//// Effectful functions; encoded in the type signature.
//def foo(): Task[String] = ???
//def bar(): Task[String] = ???
//
//// No data dependency between function calls.
//val a = foo()
//val b = bar()
//val c = a.flatMap(_ => b)          // sequential
//val d = (a, b).mapN((a, b) => ...) // parallel
//
//c.unsafeRunSync()
//(a >> b).unsafeRunSync()
//    }
//
//    {
//// Potential side-effects, but we don't know.
//def foo(): String = ???
//def bar(a: String): String = ???
//
//      // Data-dependency between functions calls.
//val a = foo()
//val b = bar(a)
//    }
//
//    {
//// Effectful functions; encoded in signature.
//def foo(): Task[String] = ???
//def bar(a: String): Task[String] = ???
//
//// Data-dependency between functions calls.
//val c = foo().flatMap(a => bar(a))
//c.unsafeRunSync()
//
//      val b2 = for {
//        a <- foo()
//        b <- bar(a)
//      } yield {
//        b
//      }
//      b2.unsafeRunSync()
//    }
//
//    {
//      // Effectful functions; encoded in the type signature.
//      def foo(): Task[String] = ???
//      def bar(a: String, b: String): Task[String] = ???
//
//      // We have data-dependency between functions calls.
//      val b1 = foo().flatMap(a => bar(a, a))
//      b1.unsafeRunSync()
//
//      val b2 = for {
//        a1 <- foo()
//        a2 <- foo()
//        b <- bar(a1, a2)
//      } yield {
//        b
//      }
//      b2.unsafeRunSync()
//    }
//  }
//}
