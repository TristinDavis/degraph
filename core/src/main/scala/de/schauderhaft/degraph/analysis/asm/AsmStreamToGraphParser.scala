package de.schauderhaft.degraph.analysis.asm

import java.io.{BufferedInputStream, InputStream}

import de.schauderhaft.degraph.analysis.NoSelfReference
import de.schauderhaft.degraph.analysis.base.StreamToGraphParser
import de.schauderhaft.degraph.graph.Graph
import de.schauderhaft.degraph.model.Node
import org.objectweb.asm.ClassReader


private class AsmStreamToGraphParser(
                                      categorizer: (Node) => Node,
                                      filter: (Node) => Boolean
                                      ) extends StreamToGraphParser {

  val g = new Graph(categorizer, filter, new NoSelfReference(categorizer))

  def parseStream(is: InputStream): Unit = {
    val reader = new ClassReader(new BufferedInputStream(is))
    reader.accept(new GraphBuildingClassVisitor(g), 0)
  }
}
