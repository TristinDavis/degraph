package de.schauderhaft.degraph.analysis.base

import java.io.File

class FileFinder(val rootPath: String) {
  private def singleDirFind(root: File): Set[File] = {
    if (root.isDirectory) {
      val files = root.list(ClassFileFilter).toSet
      files.map(new File(root.getCanonicalPath, _)) ++ expandDirs(root.listFiles(DirFilter).toSet)
    } else
      Set(root.getAbsoluteFile).filter(
        (f: File) => ClassFileFilter.accept(f.getParentFile, f.getName)
      )
  }

  private def expandDirs(files: Set[File]) =
    files.flatMap(singleDirFind)

  def find(): Set[File] =
    singleDirFind(new File(rootPath))
}
