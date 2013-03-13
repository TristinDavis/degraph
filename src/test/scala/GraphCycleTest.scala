

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers
import scalax.collection.edge.LkDiEdge
import scalax.collection.mutable.{ Graph => SGraph }
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import scalax.collection.mutable.{ Graph => SGraph }

@RunWith(classOf[JUnitRunner])
class GraphCycleTest extends FunSuite with ShouldMatchers {

    test("debugging") {
        import scalax.collection.mutable.{ Graph => SGraph }
        import scalax.collection.GraphPredef._
        import scalax.collection.GraphEdge._
        import scalax.collection.edge.Implicits._
        import scalax.collection.edge.LkDiEdge
        implicit val factory = scalax.collection.edge.LkDiEdge

        val g = SGraph[SimpleNode, LkDiEdge]()

        g.addLEdge(SimpleNode("Package", "com.p3"), SimpleNode("Package", "de.p3"))('references)
        g.addLEdge(SimpleNode("Package", "de.p1"), SimpleNode("Package", "de.p2"))('references)
        g.addLEdge(SimpleNode("Package", "de.p2"), SimpleNode("Package", "de.p1"))('references)
        g.addLEdge(SimpleNode("Package", "de.p2"), SimpleNode("Package", "com.p2"))('references)

        g.findCycle should not be ('empty)
    }
}

case class SimpleNode(
    nodeType: String,
    name: String) {
}