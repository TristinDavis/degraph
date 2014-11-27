package de.schauderhaft.degraph.analysis.asm

import de.schauderhaft.degraph.analysis.base.StreamAnalizer
import de.schauderhaft.degraph.model.Node
import de.schauderhaft.degraph.graph.Graph

object Analyzer extends StreamAnalizer {

  def analyze(
               sourceFolder: String,
               categorizer: (Node) => Node,
               filter: (Node) => Boolean): Graph = {
    parse(sourceFolder, new AsmStreamToGraphParser(categorizer, filter))
  }

}

