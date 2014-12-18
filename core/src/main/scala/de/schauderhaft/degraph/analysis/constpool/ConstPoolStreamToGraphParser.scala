package de.schauderhaft.degraph.analysis.constpool

import java.io.{DataInputStream, BufferedInputStream, InputStream}

import de.schauderhaft.degraph.analysis.NoSelfReference
import de.schauderhaft.degraph.analysis.base.StreamToGraphParser
import de.schauderhaft.degraph.graph.Graph
import de.schauderhaft.degraph.model.{SimpleNode, Node}

import scala.collection.mutable


private class ConstPoolStreamToGraphParser(
                                            categorizer: (Node) => Node,
                                            filter: (Node) => Boolean
                                            ) extends StreamToGraphParser {

  // tag bytes of the various constant pool entries
  private val CONSTANT_Class = 7
  private val CONSTANT_Fieldref = 9
  private val CONSTANT_Methodref = 10
  private val CONSTANT_InterfaceMethodref = 11
  private val CONSTANT_String = 8
  private val CONSTANT_Integer = 3
  private val CONSTANT_Float = 4
  private val CONSTANT_Long = 5
  private val CONSTANT_Double = 6
  private val CONSTANT_NameAndType = 12
  private val CONSTANT_Utf8 = 1
  private val CONSTANT_MethodHandle = 15
  private val CONSTANT_MethodType = 16
  private val CONSTANT_InvokeDynamic = 18

  /**
   * length of the various constant pool entries, excluding the tag byte.
   * No entries are available of variable length entries.
   */
  private val lengthOfEntry = Map(
    CONSTANT_Class -> 2,
    CONSTANT_Fieldref -> 4,
    CONSTANT_Methodref -> 4,
    CONSTANT_InterfaceMethodref -> 4,
    CONSTANT_String -> 2,
    CONSTANT_Integer -> 4,
    CONSTANT_Float -> 4,
    CONSTANT_Long -> 8,
    CONSTANT_Double -> 8,
    CONSTANT_NameAndType -> 4,
    CONSTANT_MethodHandle -> 3,
    CONSTANT_MethodType -> 2,
    CONSTANT_InvokeDynamic -> 4
  )

  val g = new Graph(categorizer, filter, new NoSelfReference(categorizer))


  def classNode(slashSeparatedName: String): SimpleNode = {
    if (slashSeparatedName.contains(";"))
      new IllegalArgumentException("parameter has unexpected content. This is an internal error, please open a bug for degraph with this output: " + slashSeparatedName).printStackTrace()
    SimpleNode.classNode(slashSeparatedName.replace("/", "."))
  }

  def classNodeFromSingleType(singleTypeDescription: String) = {
    val Pattern = """\[*L([\w/$]+);""".r
    singleTypeDescription match {
      case Pattern(x) => classNode(x)
      case _ => classNode(singleTypeDescription)
    }
  }

  def classNodeFromDescriptor(desc: String): Set[SimpleNode] = {
    def internal: Set[SimpleNode] =
      if (desc == null || desc == "")
        Set()
      else {
        val pattern = """(?<=L)([\w/$]+)(?=[;<])""".r
        val matches = pattern.findAllIn(desc)
        matches.map(classNode(_)).toSet
      }


    internal
  }


  private def readIsJvmFile(stream: DataInputStream) = {
    stream.readInt() == 0xCAFEBABE
  }

  private def nextConstPoolEntry(stream: DataInputStream) = {
    val tag = stream.readUnsignedByte()
    val value =
      tag match {
        case CONSTANT_Class => ClassPointer(stream.readUnsignedShort()) // utf-8
        case CONSTANT_Fieldref => DontCare
        case CONSTANT_Methodref => DontCare
        case CONSTANT_InterfaceMethodref => DontCare
        case CONSTANT_String => DontCare
        case CONSTANT_Integer => DontCare
        case CONSTANT_Float => DontCare
        case CONSTANT_Long => DoubleEntry
        case CONSTANT_Double => DoubleEntry
        case CONSTANT_NameAndType =>
          stream.skipBytes(2)
          DescriptorPointer(stream.readUnsignedShort()) // descriptor
        case CONSTANT_MethodHandle => DontCare
        case CONSTANT_MethodType => DescriptorPointer(stream.readUnsignedShort()) // descriptor
        case CONSTANT_InvokeDynamic => DontCare
        case CONSTANT_Utf8 => Utf8(stream.readUTF())
        case _ =>
          println("Unknown tag: " + tag)
          DontCare
      }
    if (value == DontCare || value == DoubleEntry) stream.skipBytes(lengthOfEntry.getOrElse(tag, 0))
    value
  }


  @Override
  def parseStream(is: InputStream): Unit = {
    val bis = new DataInputStream(new BufferedInputStream(is))
    if (!readIsJvmFile(bis))
      return
    bis.skip(4) // skipping version information to the start of the constant pool
    val constPoolSize = bis.readUnsignedShort() - 1
    val constPool = new Array[ConstPoolEntry](constPoolSize)
    var index = 0
    while (index < constPoolSize) {
      val entry = nextConstPoolEntry(bis)
      constPool(index) = entry
      if (entry == DoubleEntry) {
        index += 1
        constPool(index) = DontCare
      }
      index += 1
    }


    val pointer = for {
      cp <- constPool
      ns = cp match {
        case p: ClassPointer => Set(classNodeFromSingleType(constPool(p.index - 1).asInstanceOf[Utf8].content))
        case p: DescriptorPointer => classNodeFromDescriptor(constPool(p.index - 1).asInstanceOf[Utf8].content)
        case _ => Set[SimpleNode]()
      }
      n <- ns
    } yield n

    bis.skipBytes(2)

    val thisClass = constPool(constPool(bis.readUnsignedShort() - 1).asInstanceOf[PointerConstPoolEntry].index - 1).asInstanceOf[Utf8]
    val thisClassNode = classNode(thisClass.content)

    for {
      p <- pointer
      if thisClassNode != p
    } {
      g.connect(thisClassNode, p)
    }
  }
}

sealed trait ConstPoolEntry

object DontCare extends ConstPoolEntry

object DoubleEntry extends ConstPoolEntry

case class Utf8(content: String) extends ConstPoolEntry

sealed trait PointerConstPoolEntry extends ConstPoolEntry {
  def index: Int
}

case class ClassPointer(index: Int) extends PointerConstPoolEntry

case class DescriptorPointer(index: Int) extends PointerConstPoolEntry