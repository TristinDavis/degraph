package de.schauderhaft.degraph.configuration

import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import de.schauderhaft.degraph.graph.SliceSource
import scalax.collection.edge.LkDiEdge
import scalax.collection.mutable.{ Graph => SGraph }
import de.schauderhaft.degraph.model.Node
import de.schauderhaft.degraph.graph.Graph
import de.schauderhaft.degraph.model.SimpleNode

@RunWith(classOf[JUnitRunner])
class MultiElementStrictLayerConstraintTest extends FunSuite with ShouldMatchers {
    val paramTupel = ("t", IndexedSeq(StrictLayer("a"), StrictLayer("b", "c", "d"), StrictLayer("e")))
    val cons = Seq(LayeringConstraint.tupled(paramTupel),
        DirectLayeringConstraint.tupled(paramTupel))

    for (c <- cons) {
        test("dependencies into a multielement layer are ok %s".format(c.getClass())) {
            c.violations(MockSliceSource("t", "a" -> "b", "a" -> "d")) should be(Set())
        }

        test("dependencies from a multielement layer are ok %s".format(c.getClass())) {
            c.violations(MockSliceSource("t", "b" -> "e", "d" -> "e")) should be(Set())
        }

        test("dependencies within a multielement layer are not ok %s".format(c.getClass())) {
            c.violations(MockSliceSource("t", "b" -> "c", "b" -> "d")) should be(Set(
                (SimpleNode("t", "b"), SimpleNode("t", "c")),
                (SimpleNode("t", "b"), SimpleNode("t", "d"))))
        }

        test("self dependencies within a multielement layer are  ok %s".format(c.getClass())) {
            c.violations(MockSliceSource("t", "b" -> "b")) should be(Set())
        }
    }
}