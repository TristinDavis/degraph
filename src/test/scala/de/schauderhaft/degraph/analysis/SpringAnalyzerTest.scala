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
    test("a") {
        val graph: Graph = SpringAnalyzer.analyze(new File(this.getClass().getClassLoader().getResource("/singleBean.xml").getPath()))
        graph.allNodes contains ("myBean")
    }

}