package critpath

import java.util

import Model.TasksModel

object CriticalPath {
  //TODO: Implement this...
  // Takes in java list to simplify integration
  def findCriticalPath[T, Q](startPoint: T, taskIDNetwork: DAG[T], idToTaskMap: Function[T, TasksModel]): java.util.List[(T, Set[T])] = {
    // Prevent crash when startPoint not in the existing network.
    if(!taskIDNetwork.hasTask(startPoint)) return new util.ArrayList[(T, Set[T])]()


    // This needs to be more efficient, this would touch every node once for itself then once for every parent
    // O(n^2)
    def get_total_duration(tasks: Set[T]) : Float = {
      if(tasks.isEmpty) 0
      else {
        tasks.map(
          e => idToTaskMap(e).getDuration + get_total_duration(taskIDNetwork.getChildren(e))
        ).sum
      }
    }

    // Currently only supports a single max path, this needs fixing
    def longest_child_path(tasks: Set[T]) : Set[T] = {
      if(tasks.isEmpty) Set.empty
      else {
        val largest_child = tasks.maxBy(e => get_total_duration(Set[T](e)))
        print(largest_child)
        Set[T](largest_child) ++ longest_child_path(taskIDNetwork.getChildren(largest_child))
      }
    }

    // Gathering the data completely
    def tmp_func(item: T, network: DAG[T]): List[(T, Set[T])] = {
      // Double () required to makesure item and idToTaskMap(item) are recognised as the same tuple not seperate ones
      //      require(network.hasTask(item))
      val tmp_val = List[(T, Set[T])]((item, longest_child_path(network.getChildren(item))))
      println("Durations", + get_total_duration(Set(item)))
      println("Longest Child Path", tmp_val)
      tmp_val
    }

    val l = tmp_func(startPoint, taskIDNetwork)
    println(l)

    // Outputing
    val result = new util.ArrayList[(T, Set[T])]()
    l.foreach(a => result.add(a))
    result // Feels dodgy, probably should do another way
  }

  // Takes in java list to simplify integration
  // Map is map of keys to child keys
  def makeDAG[T](list: java.util.Map[T, java.util.List[T]]): DAG[T] = {
    def fromJavaList(list: java.util.Map[T, java.util.List[T]], dag: DAG[T], idx: java.util.Iterator[T]): DAG[T] = {
      if (!idx.hasNext) dag
      else {
        val id: T = idx.next()
        val set: Set[T] = scala.jdk.CollectionConverters.ListHasAsScala(list.get(id)).asScala.toSet
        fromJavaList(list, dag.extend(id, set), idx)
      }
    }


    val result: DAG[T] = fromJavaList(list, DAG.empty, list.keySet().iterator())
    result.print_map()
    result
  }

}
