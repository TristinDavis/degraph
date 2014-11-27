package de.schauderhaft.degraph.analysis.base

import java.io.{File, FilenameFilter}

object ClassFileFilter extends FilenameFilter {
  override def accept(f: File, n: String) =
    n.endsWith(".class") ||
      n.endsWith(".jar")
}
