package de.schauderhaft.degraph.analysis

import java.util.Collections

import scala.collection.JavaConversions.collectionAsScalaIterable
import scala.collection.JavaConversions.mapAsScalaMap
import scala.collection.JavaConverters._
import com.jeantessier.classreader.LoadListenerVisitorAdapter
import com.jeantessier.classreader.TransientClassfileLoader
import com.jeantessier.dependency.ClassNode
import com.jeantessier.dependency.CodeDependencyCollector
import com.jeantessier.dependency.FeatureNode
import com.jeantessier.dependency.Node
import com.jeantessier.dependency.NodeFactory

import de.schauderhaft.degraph.filter.NoSelfReference
import de.schauderhaft.degraph.graph.Graph

/**
 * analyzes whatever it finds in the sourceFolder using the Dependency Finder library and returns a Graph instance which captures the relevant dependency information
 */
object Analyzer {
    def analyze(sourceFolder: String, categorizer: AnyRef => AnyRef, filter: AnyRef => Boolean): Graph = {

        val g = new Graph(categorizer, filter, new NoSelfReference(categorizer))

        new JavaAnalyzer(sourceFolder).analyze(g)
        new SpringAnalyzer(sourceFolder).analyze(g)
    }
}