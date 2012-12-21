package de.schauderhaft.degraph.analysis

import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.ShouldMatchers

@RunWith(classOf[JUnitRunner])
class AnalyzerTest extends FunSuite with ShouldMatchers {
    private val testClassFolder = "./bin"
    private val graph = Analyzer.analyze(testClassFolder, (x) => x, _ => true)

    test("No self references") {
        for (
            n <- graph.topNodes;
            n2 <- graph.connectionsOf(n)
        ) n should not be n2
    }

}