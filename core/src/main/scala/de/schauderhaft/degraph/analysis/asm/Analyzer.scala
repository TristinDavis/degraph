package de.schauderhaft.degraph.analysis.asm

import de.schauderhaft.degraph.analysis.{NoSelfReference, AnalyzerLike}
import de.schauderhaft.degraph.model.Node
import de.schauderhaft.degraph.graph.Graph
import org.objectweb.asm._
import java.io.{InputStream, File, BufferedInputStream, FileInputStream}
import java.util.zip.ZipFile

object Analyzer extends AnalyzerLike {

  def analyze(sourceFolder: String, categorizer: (Node) => Node, filter: (Node) => Boolean): Graph = {

    val parser = new StreamToGraphParser(categorizer, filter)

    def analyze(f: File) = {
      if (f.getName.endsWith(".class")) {
        parser.parseStream(new FileInputStream(f))
      } else {
        val zipFile = new ZipFile(f)
        val entries = zipFile.entries()
        while (entries.hasMoreElements) {
          val e = entries.nextElement()
          if (e.getName.endsWith(".class")) {
            parser.parseStream(zipFile.getInputStream(e))
          }
        }
      }
    }

    for (folder <- sourceFolder.split(System.getProperty("path.separator"))) {
      val fileFinder = new FileFinder(folder)
      val files = fileFinder.find()
      for {f <- files} {
        analyze(f)
      }
    }
    parser.g
  }


}

private class StreamToGraphParser(categorizer: (Node) => Node, filter: (Node) => Boolean) {

  val g = new Graph(categorizer, filter, new NoSelfReference(categorizer))

  def parseStream(is: InputStream): Unit = {
    val reader = new ClassReader(new BufferedInputStream(is))
    reader.accept(new GraphBuildingClassVisitor(g), 0)
  }
}