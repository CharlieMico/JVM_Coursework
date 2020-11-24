package critpath

import scala.collection.immutable.HashMap

// Notes: This is required to be as functional as possible, or at least this is my understanding on it.
// Inheritance of the immutable form of HashMap is disallowed, due to it being final.
// Join and Extend are the only formally accepted functions
// NodeType will be a scala implementation of the task data container, the conversion occurs in extend(List[TasksModel])
/**
 * Directional Analytical Graph
 *
 * @param local_map The base map this graph is built on, this is assumed to be correct
 *
 * @tparam NodeType The type of the taskIDs stored in this graph
 *
 * @author Charlie Mico
 */
class DAG[NodeType](val local_map: HashMap[NodeType, Set[NodeType]]) {

  /**
   * Created a new DAG containing the data from both this and the provided graph.
   * Tasks with conflicting ids are assumed to be referring to the same task and as such are merged together.
   *
   * @param other The other graph to pull from
   *
   * @return The DAG containing all values
   */
  def join(other: DAG[NodeType]): DAG[NodeType] = {
    require(other != null)

    //Resorting to mutable map while processing, might not be the best but was a pain trying to get it to work purely
    //with immutable maps
    val tmp = new scala.collection.mutable.HashMap[NodeType, Set[NodeType]]
    // Place all items in both maps into the tmp map
    local_map
      .filter(node => other.hasTask(node._1))
      .foreach(node => tmp.put(node._1, node._2 ++ other.getChildren(node._1)))

    // Place all items in only the other map into the tmp map
    other.local_map
      .filter(node => !tmp.contains(node._1))
      .foreach(node => tmp.put(node._1, node._2))

    // Place all items only in the local map into the tmp map
    local_map
      .filter(node => !tmp.contains(node._1))
      .foreach(node => tmp.put(node._1, node._2))

    // Shifting from mutable to immutable by adding the mutable map to an immutable one.
    new DAG[NodeType](new HashMap[NodeType, Set[NodeType]] ++ tmp.toMap)
  }

  /**
   * Extends the graph by a single item
   *
   * @param item The item being added
   * @param children The children of the item being added
   *
   * @return A new graph containing the new item in addition to the old graph data
   */
  def extend(item: NodeType, children: Set[NodeType]) : DAG[NodeType] = {
    require(item != null)
    require(children != null)
    // For logical consistency, add the dependencies to the existing ones
    val tmp_dep = local_map.getOrElse(item, Set()) ++ children
    new DAG[NodeType](local_map.updated(item, tmp_dep))
  }

  /**
   * Gets the children of a given task
   *
   * @param item The task being queried
   *
   * @return The set of taskIDs
   */
  def getChildren(item: NodeType) : Set[NodeType] = local_map.getOrElse(item, Set())

  /**
   * Returns if the task exists
   *
   * @param item The task being queried
   *
   * @return True if it exists and False otherwise
   */
  def hasTask(item: NodeType) : Boolean = local_map.contains(item)

  /**
   * Prints the contents of the local_map
   */
  def print_map(): Unit = {
    local_map.foreachEntry((k, e) => println("Key:", k, ";Entry:", e))
  }

  /**
   * Prints all dependencies of the start_point in the local_map
   *
   * @param start_point The point to start with
   */
  def print_dependency_lines(start_point: NodeType): Unit = {
    require(local_map.contains(start_point))
    var deps = Set(start_point)
    local_map.getOrElse(start_point, Set()).foreach(item => deps = deps + item)
    print("Start At", deps.slice(0, 1), deps.slice(1, deps.size))
  }

}

/**
 * Companion object for the DAG class
 */
object DAG {
  // Feels like I've done this wrong, but not sure how to do otherwise.
  /**
   * Constructs an empty DAG
   *
   * @tparam T The type of the DAG
   *
   * @return The empty DAG
   */
  def empty[T] = new DAG[T](new HashMap[T, Set[T]]())
}