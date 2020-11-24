package critpath

import java.util
import model.ProjectFactory;
import model.CriticalPathFactory;

/**
 * This object is the point of entry for Java Applications to hook into.
 */
object CriticalPath {
  //TODO: Implement this...
  // Takes in java list to simplify integration
  /**
   * Finds the critical path of the given network of tasks
   *
   * @param startPoint The point in the network to start calculating from
   * @param taskIDNetwork The network as whole
   * @param idToTaskMap A function to convert from a taskID to a given task
   * @param taskToDuration A function mapping from a task to it's duration
   *                       (keeps the scala apart from the specifics of data storage)
   *
   * @tparam T The type of the taskIDs
   * @tparam Q The type of the task itself
   *
   * @return A list of tuples containing both the startPoint and a Set containing the path making up the critical path
   */
  def findCriticalPath[T, Q](startPoint: T, taskIDNetwork: DAG[T], idToTaskMap: Function[T, Q], taskToDuration: Function[Q, Float]): java.util.List[(T, Set[T])] = {
    if(!taskIDNetwork.hasTask(startPoint)) return new util.ArrayList[(T, Set[T])]()

    // This needs to be more efficient, this would touch every node once for itself then once for every parent
    // O(n^2)
    /**
     * Gets the sum of each given task and the child paths
     *
     * @param tasks The tasks to start from
     *
     * @return The total duration of the tasks given or zero if tasks is empty
     */
    def get_total_duration(tasks: Set[T]) : Float = {
      if(tasks.isEmpty) 0
      else {
        tasks.map(
          e => taskToDuration(idToTaskMap(e)) + get_total_duration(taskIDNetwork.getChildren(e))
        ).sum
      }
    }

    // Currently only supports a single max path, this needs fixing
    /**
     * Finds the longest path of children from the starting points
     *
     * @param tasks The tasks to start from
     *
     * @return A set of taskIDs or empty if tasks empty
     */
    def longest_child_path(tasks: Set[T]) : Set[T] = {
      if(tasks.isEmpty) Set.empty
      else {
        val largest_child = tasks.maxBy(e => get_total_duration(Set[T](e)))
        print(largest_child)
        Set[T](largest_child) ++ longest_child_path(taskIDNetwork.getChildren(largest_child))
      }
    }

    val l = List[(T, Set[T])]((startPoint, longest_child_path(taskIDNetwork.getChildren(startPoint))))
    println(l)

    // Converting to java list
    val result = new util.ArrayList[(T, Set[T])]()
    l.foreach(a => result.add(a))
    result
    // Feels dodgy, probably should do another way
  }

  /**
   * Constructs A directional analytic graph (DAG) from the given map of taskID to taskID lists (their relationships)
   *
   * @param map The data to construct the graph from
   *
   * @tparam T The type of the taskIDs
   *
   * @return The graph constructed
   */
  def makeDAG[T](map: java.util.Map[T, java.util.List[T]]): DAG[T] = {
    /**
     * Constructs the graph recursovly, extending it for each item in the map.
     *
     * @param map The data to construct the graph from
     * @param dag The graph from the previous iteration
     * @param idx The iterator from the map's keySet
     *
     * @return
     */
    def fromJavaList(map: java.util.Map[T, java.util.List[T]], dag: DAG[T], idx: java.util.Iterator[T]): DAG[T] = {
      if (!idx.hasNext) dag
      else {
        val id: T = idx.next()
        val set: Set[T] = scala.jdk.CollectionConverters.ListHasAsScala(map.get(id)).asScala.toSet
        fromJavaList(map, dag.extend(id, set), idx)
      }
    }

    fromJavaList(map, DAG.empty, map.keySet().iterator())
  }

}
