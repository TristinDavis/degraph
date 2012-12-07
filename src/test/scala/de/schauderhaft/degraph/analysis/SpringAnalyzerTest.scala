package de.schauderhaft.degraph.analysis

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import de.schauderhaft.degraph.graph.Graph
import java.io.File

@RunWith(classOf[JUnitRunner])
class SpringAnalyzerTest extends FunSuite {
    import org.scalatest.matchers.ShouldMatchers._

    test("Return value should not be null") {
        val graph: Graph = SpringAnalyzer.analyze(null)
        graph should not be (null)
    }

    test("Graph must have one node named de.schauderhaft.SomeClass") {

        val res = this.getClass().getResource("/singleBean.xml")
        //        res should not be (null)
        val graph: Graph = SpringAnalyzer.analyze(new File(res.getPath()))
        graph.allNodes should contain("de.schauderhaft.SomeClass".asInstanceOf[AnyRef])

    }

    test("Graph must have two nodes") {
        val res = this.getClass().getResource("/multiBean.xml")
        val graph: Graph = SpringAnalyzer.analyze(new File(res.getPath()))
        graph.allNodes should contain("de.schauderhaft.SomeClass".asInstanceOf[AnyRef])
        graph.allNodes should contain("de.schauderhaft.MultiClass".asInstanceOf[AnyRef])

    }
}