package de.schauderhaft.degraph.analysis

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import de.schauderhaft.degraph.graph.Graph
import java.io.File
import javax.xml.transform.Source
import scala.io.Source
import de.schauderhaft.degraph.graph.Graph

@RunWith(classOf[JUnitRunner])
class SpringAnalyzerTest extends FunSuite {
    import org.scalatest.matchers.ShouldMatchers._

    test("Return value should not be null") {
        val graph: Graph = new SpringAnalyzer("").analyze(new Graph)
        graph should not be (null)
    }

    test("Graph must have one node named de.schauderhaft.SomeClass") {
        val res = this.getClass().getResource("/singleBean.xml")
        val graph: Graph = new SpringAnalyzer(res.getPath()).analyze(new Graph)
        graph.allNodes should contain("de.schauderhaft.SomeClass".asInstanceOf[AnyRef])

    }

    test("Graph must have two nodes") {
        val res = this.getClass().getResource("/multiBean.xml")
        val graph: Graph = new SpringAnalyzer(res.getPath()).analyze(new Graph)
        graph.allNodes should contain("de.schauderhaft.SomeClass".asInstanceOf[AnyRef])
        graph.allNodes should contain("de.schauderhaft.SomeOtherClass".asInstanceOf[AnyRef])

    }

    test("analyze(File) calls graphFromSource") {
        var called = false
        val res = this.getClass().getResource("/multiBean.xml")
        val graph: Graph = new SpringAnalyzer(
            res.getPath(),
            (_, _) => { called = true; new Graph() }).analyze(new Graph)

        called should be(true)

    }

    // bean references
    // includes

}