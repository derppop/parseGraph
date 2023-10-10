package models
import org.apache.hadoop.io.Writable

import NetGraphAlgebraDefs.NodeObject
import java.io.{DataInput, DataOutput}


case class Shard(var id: Int = 0, var subGraph: Set[NodeObject] = null, var perturbedSubGraph: Set[NodeObject] = null) extends Writable{
  override def write(out: DataOutput): Unit = {
    // Serialize shard
    out.writeInt(id)

    // Original subgraph
    out.writeInt(subGraph.size)
    subGraph.foreach(node => writeNode(out, node))

    // Perturbed subgraph
    out.writeInt(perturbedSubGraph.size)
    perturbedSubGraph.foreach(node => writeNode(out, node))
  }

  private def writeNode(out: DataOutput, node: NodeObject): Unit = {
    // Serialize node
    out.writeInt(node.id)
    out.writeInt(node.children)
    out.writeInt(node.props)
    out.writeInt(node.currentDepth)
    out.writeInt(node.propValueRange)
    out.writeInt(node.maxDepth)
    out.writeInt(node.maxBranchingFactor)
    out.writeInt(node.maxProperties)
    out.writeDouble(node.storedValue)
    for (prop <- node.properties) out.writeInt(prop)
  }

  override def readFields(in: DataInput): Unit = {
    // Deserialize shard
    id = in.readInt()

    // Original subgraph
    val subgraphSize = in.readInt()
    subGraph = (0 until subgraphSize).map(_ => readNode(in)).toSet

    // Perturbed Subgraph
    val perturbedSubgraphSize = in.readInt()
    perturbedSubGraph = (0 until perturbedSubgraphSize).map(_ => readNode(in)).toSet
  }

  private def readNode(in: DataInput): NodeObject = {
    // Deserialize node
    val id = in.readInt()
    val children = in.readInt()
    val props = in.readInt()
    val currentDepth = in.readInt()
    val propValueRange = in.readInt()
    val maxDepth = in.readInt()
    val maxBranchingFactor = in.readInt()
    val maxProperties = in.readInt()
    val storedValue = in.readDouble()
    val newProperties: List[Int] = (0 until props).map(_ => in.readInt()).toList

    NodeObject(id, children, props, currentDepth, propValueRange, maxDepth, maxBranchingFactor, maxProperties, storedValue, Option(newProperties))
  }
}
