package CriticalPath
import Model.TasksModel

import scala.collection.immutable.HashMap

// Notes: This is required to be as functional as possible, or at least this is my understanding on it.
// Inheritance of the immutable form of HashMap is disallowed, due to it being final.
// Join and Extend are the only formally accepted functions
// NodeType will be a scala implementation of the task data container, the conversion occurs in extend(List[TasksModel])
case class DAG[NodeType]() {

  // This is likely the wrong way to do this part, got to re-read all the scala slides to make sure.
  def DAG(value: NodeType, children: Set[NodeType]): Unit = {
//    root_data = value
  }

  //This is private 'cause you can't extend the immutable HashMap as it's final
//  private val _local_map: HashMap[NodeType, Set[NodeType]] = HashMap[NodeType, Set[NodeType]]()

//  var root_data: NodeType // Currently not functional

  // Creates a DAG containing all data from both this is being called on.
  def join(other: DAG[NodeType]): DAG[NodeType] = {
    null
  }

  // Adds a single item, and it's dependencies (what's required for it to be completed)
  def extend(item: NodeType, dependencies: Set[NodeType]) : DAG[NodeType] = {

    null
  }

  // Essentially an extension of the extend function which is applied to the whole provided list.
  def extend(taskList: List[TasksModel]): DAG[NodeType] = {

    null
  }
}


//val d = DAG[Int](0, Set.empty)
//d.extend(10, Set(10, 10, 5, 4))
//val a = DAG[Int](0, Set.empty)
//val g = a.join(d)