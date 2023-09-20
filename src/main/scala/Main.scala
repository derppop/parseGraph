import NetGraphAlgebraDefs.{NetGraph, NetModel, NetModelAlgebra}
import NetGraphAlgebraDefs.NetModelAlgebra.outputDirectory
import Utilz.NGSConstants
object Main{
  def main(args: Array[String]): Unit = {
    val graph: NetGraph = NetModelAlgebra().get
    graph.persist("C:\\Users\\yortb\\Desktop\\cs441\\parseGraph\\graphs", NGSConstants.OUTPUTFILENAME)
  }
}