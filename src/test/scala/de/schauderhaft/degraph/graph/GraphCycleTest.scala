package de.schauderhaft.degraph.graph

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

import de.schauderhaft.degraph.model.Node
import de.schauderhaft.degraph.model.SimpleNode
import scalax.collection.edge.LkDiEdge
import scalax.collection.mutable.{Graph => SGraph}
import scalax.collection.mutable.Graph.apply$default$3

@RunWith(classOf[JUnitRunner])
class GraphCycleTest extends FunSuite with ShouldMatchers {

    test("debugging") {
        import scalax.collection.mutable.{ Graph => SGraph }
        import scalax.collection.GraphPredef._
        import scalax.collection.GraphEdge._
        import scalax.collection.edge.Implicits._
        import scalax.collection.edge.LkDiEdge
        import de.schauderhaft.degraph.model.SimpleNode
        import de.schauderhaft.degraph.model.Node
        implicit val factory = scalax.collection.edge.LkDiEdge

        val g = SGraph[Node, LkDiEdge]()

        //        add: SimpleNode(Package,com.p3)->SimpleNode(Package,de.p3)
        //add: SimpleNode(Package,de.p1)->SimpleNode(Package,de.p2)
        //add: SimpleNode(Package,de.p2)->SimpleNode(Package,de.p1)
        //add: SimpleNode(Package,de.p2)->SimpleNode(Package,com.p2)
        g.addLEdge(SimpleNode("Package", "com.p3"), SimpleNode("Package", "de.p3"))('references)
        g.addLEdge(SimpleNode("Package", "de.p1"), SimpleNode("Package", "de.p2"))('references)
        g.addLEdge(SimpleNode("Package", "de.p2"), SimpleNode("Package", "de.p1"))('references)
        g.addLEdge(SimpleNode("Package", "de.p2"), SimpleNode("Package", "com.p2"))('references)

        g.findCycle should not be ('empty)
    }
}