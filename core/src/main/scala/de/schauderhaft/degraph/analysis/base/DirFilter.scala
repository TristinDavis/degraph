package de.schauderhaft.degraph.analysis.base

import java.io.{File, FileFilter}

object DirFilter extends FileFilter {
  def accept(pathname: File): Boolean = pathname.isDirectory
}
