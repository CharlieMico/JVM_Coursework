package CriticalPath

object CriticalPath {
  //TODO: Implement this...
  def findCriticalPath[T](taskNetwork: DAG[T]): List[Set[T]] = {
    print("Scala Finding the Critical Path not implemented")
    List.empty
  }

  def makeDAG[T](list: List[(T, Set[T])]): DAG[T] = {
//    require(list != null)
//    require(list.nonEmpty)

    val result : DAG[T] = DAG.empty
    list.foreach(item => result.extend(item._1, item._2))
    result
  }

}
