object Main{
  def main(args: Array[String]): Unit = {
    if (args.length > 0) {
      val graphFile = args(0)
      val nodes = parseGraph(graphFile)
      nodes.foreach((pair) => {
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
    var nodes = Map[String, List[String]]()
    for(line <- file.getLines()) {
      if (line.contains("->")) {
        val edge = line
          .replace("\"", "")
          .split("->")
          .map(_.trim)
          .map((x:String) => {
            if (x.contains(" ")) x.substring(0, x.indexOf(" ")) else x
          })
        val connectedNodesList = nodes.getOrElse(edge(0), List()) :+ edge(1)
        nodes = nodes.updated(edge(0), connectedNodesList)
      }
    }
    nodes
  }
}