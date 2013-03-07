package de.schauderhaft.degraph.graph

import org.junit.runner.RunWith
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import NodeTestUtil.n
import de.schauderhaft.degraph.model.SimpleNode.classNode
import de.schauderhaft.degraph.model.SimpleNode.packageNode
import de.schauderhaft.degraph.slicer.PackageCategorizer
import de.schauderhaft.degraph.slicer.ParallelCategorizer
import de.schauderhaft.degraph.model.SimpleNode
import de.schauderhaft.degraph.slicer.PatternMatchingCategorizer

@RunWith(classOf[JUnitRunner])
class GraphCycleTest extends FunSuite with ShouldMatchers {
    test("an empty graph has no cycles") {
        pending
        val g = new Graph()
        g.edgesInCycles should be(Set())
    }

    test("a graph with two cyclic dependent nodes without slices does not report those as cycles") {
        pending
        val g = new Graph()
        g.connect(n("a"), n("b"))
        g.connect(n("b"), n("a"))
        g.edgesInCycles should be(Set())
    }

    test("a graph with a cyclic dependency between to packages returns dependencies between those packages as cyclic") {
        pending
        val g = new Graph(PackageCategorizer)
        g.connect(classNode("de.p1.A1"), classNode("de.p2.B2"))
        g.connect(classNode("de.p2.B1"), classNode("de.p3.C2"))
        g.connect(classNode("de.p3.C1"), classNode("de.p1.A2"))

        g.edgesInCycles should be(Set(
            (packageNode("de.p1"), packageNode("de.p2")),
            (packageNode("de.p2"), packageNode("de.p3")),
            (packageNode("de.p3"), packageNode("de.p1"))))
    }

    test("detecting package cycles works with combined slices") {
        pending
        val g = new Graph(new ParallelCategorizer(PackageCategorizer, _ => SimpleNode("tld", "de")))
        g.connect(classNode("de.p1.A1"), classNode("de.p2.B2"))
        g.connect(classNode("de.p2.B1"), classNode("de.p3.C2"))
        g.connect(classNode("de.p3.C1"), classNode("de.p1.A2"))

        g.edgesInCycles should be(Set(
            (packageNode("de.p1"), packageNode("de.p2")),
            (packageNode("de.p2"), packageNode("de.p3")),
            (packageNode("de.p3"), packageNode("de.p1"))))
    }

    test("detects slice nodes on every slice type") {
        val g = new Graph(new ParallelCategorizer(PackageCategorizer,
            PatternMatchingCategorizer("country", "(*).**")))

        g.connect(classNode("de.p1.A1"), classNode("de.p2.B2"))
        g.connect(classNode("de.p2.B1"), classNode("de.p1.C2"))
        g.connect(classNode("de.p2.B1"), classNode("com.p2.B1"))
        g.connect(classNode("com.p3.C1"), classNode("de.p3.C2"))

        g.edgesInCycles should be(Set(
            (packageNode("de.p1"), packageNode("de.p2")),
            (packageNode("de.p2"), packageNode("de.p1")),
            (SimpleNode("country", "de"), SimpleNode("country", "com")),
            (SimpleNode("country", "com"), SimpleNode("country", "de"))))
    }

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
        g.add(
            SimpleNode("Package", "com.p2"))
        g.add(SimpleNode("Package", "com.p3"))
        g.add(SimpleNode("Package", "de.p1"))
        g.add(SimpleNode("Package", "de.p2"))
        g.add(SimpleNode("Package", "de.p3"))
        g.addLEdge(SimpleNode("Package", "com.p3"), SimpleNode("Package", "de.p3"))('references)
        g.addLEdge(SimpleNode("Package", "de.p1"), SimpleNode("Package", "de.p2"))('references)
        g.addLEdge(SimpleNode("Package", "de.p2"), SimpleNode("Package", "com.p2"))('references)
        g.addLEdge(SimpleNode("Package", "de.p2"), SimpleNode("Package", "de.p1"))('references)

        g.findCycle should not be ('empty)
    }
}