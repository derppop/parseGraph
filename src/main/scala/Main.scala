import ParseGraph.Graph

object Main{
  def main(args: Array[String]): Unit = {
    if (args.length > 0) {
      val graphFile = args(0)
      val nodes = parseGraph(graphFile)
      nodes.foreach(pair => {
        println(pair._1 + " " + pair._2)
      })
    } else {
      System.err.println("ERROR: Please enter a graph name")
      System.exit(1)
    }
  }

  private def parseGraph(fileName: String): Map[String, List[String]] = {
    import scala.io.Source
    val file = Source.fromFile(fileName)
    // TODO: Turn nodes into an object in order to store the initial node
    var nodes = Map[String, List[String]]()
    val initNode = file.getLines().drop(2).next().split(" ")(0).replace("\"", "")
    nodes = nodes.updated(initNode, List())
    for(line <- file.getLines()) {
      if (line.contains("->")) {
        val edge = line
          .replace("\"", "")
          .split("->")
          .map(_.trim)
          .map((x:String) => {
            if (x.contains(" ")) x.substring(0, x.indexOf(" ")) else x
          })
        val fromNode: String = edge(0)
        val toNode: String = edge(1)
        val weight = line.substring(line.indexOf("=\"")+2, line.lastIndexOf("\"")).toDouble

        val connectedNodesList = nodes.getOrElse(fromNode, List()) :+ toNode
        nodes = nodes.updated(fromNode, connectedNodesList)
      }
    }
    nodes
  }
}
