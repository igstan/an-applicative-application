organization := "bucharestfp"
name         := "meetup-034"
version      := "0.1.0"
scalaVersion := "2.12.7"

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")

libraryDependencies ++= Seq(
  "ch.qos.logback"              % "logback-classic"         % "1.2.3",
  "com.typesafe.play"          %% "play-ahc-ws-standalone"  % "2.0.0-M4",
  "com.typesafe.play"          %% "play-ws-standalone-json" % "2.0.0-M4",
  "com.typesafe.scala-logging" %% "scala-logging"           % "3.9.0",
  "io.monix"                   %% "monix"                   % "3.0.0-RC1",
  "org.typelevel"              %% "cats-core"               % "1.0.1",
)

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
)

// http://tpolecat.github.io/2017/04/25/scalac-flags.html
scalacOptions ++= Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  //"-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
  //"-Ywarn-unused:locals",              // Warn if a local definition is unused.
  //"-Ywarn-unused:params",              // Warn if a value parameter is unused.
  //"-Ywarn-unused:-implicits",          // Don't warn if an implicit parameter is unused, as it emits spurious warnings for evidence params.
  //"-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
  //"-Ywarn-unused:privates",            // Warn if a private member is unused.
  "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
)

Compile / console / scalacOptions --= Seq(
  "-Ywarn-unused:imports",
  "-Xfatal-warnings",
)

consoleQuick / initialCommands := """
  import java.time._
  import scala.concurrent._
  import scala.concurrent.duration._
  import cats._
  import cats.data._
  import cats.implicits._
  import monix.eval._
  import monix.execution._
  import monix.execution.cancelables._
  import monix.execution.Scheduler.Implicits.global
  import monix.reactive._
  import monix.reactive.observers._
"""

console / initialCommands := (consoleQuick / initialCommands).value + """
  import bucharestfp._
"""

assembly / assemblyOutputPath := file("target") / "meetup-034.jar"

val root = Project("meetup-034", file("."))
