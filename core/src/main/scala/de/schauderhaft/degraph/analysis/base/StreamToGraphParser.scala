package de.schauderhaft.degraph.analysis.base

import java.io.InputStream

import de.schauderhaft.degraph.graph.Graph

trait StreamToGraphParser {
   def g: Graph

   def parseStream(is: InputStream): Unit
 }
