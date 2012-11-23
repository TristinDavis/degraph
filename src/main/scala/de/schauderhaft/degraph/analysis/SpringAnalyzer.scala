package de.schauderhaft.degraph.analysis

import de.schauderhaft.degraph.graph.Graph
import de.schauderhaft.degraph.graph.Graph
import java.io.File

/**
 * provides a single method that
 * - takes a list of source paths
 * - searches in these paths for spring xml files
 * and creates a Graph object with the following properties:
 * - Every spring file is a node
 * - every import of a file means that file depends on the imported file
 * - Every bean is a node
 * - Every bean is contained in the category represented by the file it is defined in
 * - Every bean depends on the class it is implemented by
 * - if A gets injected into B  B depends on A
 */

object SpringAnalyzer {

    def analyze(file: File): Graph = {
        new Graph
    }
}