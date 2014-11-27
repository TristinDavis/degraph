package de.schauderhaft.degraph.analysis.base

import java.io.{File, FileInputStream}
import java.util.zip.ZipFile

import de.schauderhaft.degraph.analysis.AnalyzerLike
import de.schauderhaft.degraph.analysis.base.FileFinder
import de.schauderhaft.degraph.graph.Graph

abstract class StreamAnalizer extends AnalyzerLike {

  def parse(sourceFolder: String, parser: StreamToGraphParser): Graph = {
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
      for {
        f <- files
      } {
        analyze(f)
      }
    }
    parser.g
  }
}
