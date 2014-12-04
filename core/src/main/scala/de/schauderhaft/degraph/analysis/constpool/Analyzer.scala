package de.schauderhaft.degraph.analysis.constpool

import de.schauderhaft.degraph.analysis.base.StreamAnalizer
import de.schauderhaft.degraph.graph.Graph
import de.schauderhaft.degraph.model.Node

object Analyzer extends StreamAnalizer {

  def analyze(
               sourceFolder: String,
               categorizer: (Node) => Node,
               filter: (Node) => Boolean): Graph = {
    parse(sourceFolder, new ConstPoolStreamToGraphParser(categorizer, filter))
  }

}

