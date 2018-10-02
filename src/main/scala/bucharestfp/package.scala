import java.time.Instant

package object bucharestfp
  extends cats.instances.AllInstances
     with cats.syntax.AllSyntax {

  type Interval = (Instant, Instant)
}
