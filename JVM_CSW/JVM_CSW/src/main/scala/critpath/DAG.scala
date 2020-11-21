package critpath

import scala.collection.immutable.HashMap

// Notes: This is required to be as functional as possible, or at least this is my understanding on it.
// Inheritance of the immutable form of HashMap is disallowed, due to it being final.
// Join and Extend are the only formally accepted functions
// NodeType will be a scala implementation of the task data container, the conversion occurs in extend(List[TasksModel])
class DAG[NodeType](val local_map: HashMap[NodeType, Set[NodeType]]) {

  // Creates a critpath.DAG containing all data from both this is being called on.
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

  // Adds a single item, and it's dependencies (what's required for it to be completed)
  def extend(item: NodeType, dependencies: Set[NodeType]) : DAG[NodeType] = {
    require(item != null)
    require(dependencies != null)
    // For logical consistency, add the dependencies to the existing ones
    val tmp_dep = local_map.getOrElse(item, Set()) ++ dependencies
    new DAG[NodeType](local_map.updated(item, tmp_dep))
  }

  def getChildren(item: NodeType) : Set[NodeType] = local_map.getOrElse(item, Set())
  def hasTask(item: NodeType) : Boolean = local_map.contains(item)

  def print_map(): Unit = {
    local_map.foreachEntry((k, e) => println("Key:", k, ";Entry:", e))
  }

  def print_dependency_lines(start_point: NodeType): Unit = {
    require(local_map.contains(start_point))
    var deps = Set(start_point)
//    print("Start Point: ", start_point)
    local_map.getOrElse(start_point, Set()).foreach(item => deps = deps + item)
    print("Start At", deps.slice(0, 1), deps.slice(1, deps.size))
  }

}

object DAG {
  // Feels like I've done this wrong, but not sure how to do otherwise.
  def empty[T] = new DAG[T](new HashMap[T, Set[T]]())

  def test(): Unit = {
    val t_1 = empty[Int]
    println("t_1")
//    t_1.print_map()
    val t_2 = empty[Int]
    println("t_2")
//    t_2.print_map()

    val a = t_1.extend(0, Set(1, 2))
//    a.print()
    val b = a.extend(1, Set(2))
//    b.print()

    val c = b.extend(2, Set())
//    b.print()
    val d = a.join(c)
//    d.print_map()
    d.extend(2, Set(5)).print_map()

    d.print_dependency_lines(0)

  }

}

//val d = critpath.DAG[Int](0, Set.empty)
//d.extend(10, Set(10, 10, 5, 4))
//val a = critpath.DAG[Int](0, Set.empty)
//val g = a.join(d)