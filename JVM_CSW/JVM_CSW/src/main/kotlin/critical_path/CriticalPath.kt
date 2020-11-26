package critical_path

import model.CriticalPathFactory

/**
 * The abstract base represntation of a Directional Analytic Graph
 *
 * @param IDType The datatype used to represent the IDs for nodes
 * @param TaskType The datatype representing the actual nodes
 *
 * @property list A list of tasks which to populate this graph with.
 *
 * @author Charlie Mico
 */
abstract class AbstractDirectionalAnalyticGraph<IDType, TaskType>(val list: List<TaskType>) {

    /**
     * The relationships between tasks, a parent to a set of children.
     * A single node existing in multiple child sets is acceptable.
     */
    private val task_relationships: MutableMap<IDType, MutableSet<IDType>> = HashMap()

    /**
     * A cache of the durations calculated for the Critical Path.
     */
    private val duration_cache: MutableMap<IDType, Float> = HashMap()

    /**
     * A map of task ids to the actual tasks
     */
    private val tasks: MutableMap<IDType, TaskType> = HashMap()

    /**
     * Translates the input list into their appropriate maps.
     * @constructor
     */
    init {
        for (item in list) {
            val id : IDType = taskToId(item)
            val children : MutableSet<IDType> = getTaskChildrenIDs(item).toMutableSet()
            tasks[id] = item
            task_relationships[id] = children
        }
    }

    /**
     * Wraps around the tasks map, preventing children from directly accessing it.
     *
     * @param id The id to get the task for
     *
     * @return The task represented by the id, or null if the id doesn't exist
     */
    protected fun idToTask(id: IDType) : TaskType? {
        return tasks[id]
    }


    /**
     * Converts from the task to the id
     *
     * @param task The task to get the id for
     *
     * @return The id of the task
     */
    protected abstract fun taskToId(task : TaskType?) : IDType

    /**
     * Gets the children of the task given.
     *
     * @param task The task to get the children of
     *
     * @return A list of IDTypes representing the task, or an empty list if no children present.
     */
    protected abstract fun getTaskChildrenIDs(task: TaskType) : List<IDType>

    /**
     * Get the duration of the task
     *
     * @param task The task to start from, return's 0 if this is null
     * @param recursively Should this traverse the graph recursively
     *
     * @return The duration of the task, and if recursively is true then all children as well.
     */
    protected abstract fun getDuration(task: TaskType?, recursively : Boolean = true) : Float

    /**
     * Extends this graph
     *
     * @param item The item to add/modify
     * @param children A list of the new children of this node
     *
     * @return True if successful, false otherwise
     */
    fun extend(item: TaskType, children: List<TaskType>) : Boolean {
        val item_key = taskToId(item)
        val filtered_children : MutableSet<IDType> = task_relationships.getOrDefault(item_key, HashSet())
        filtered_children.addAll(children.map { t -> taskToId(t) }.filter { t -> tasks.containsKey(t) }.toMutableSet())
        if(!tasks.contains(taskToId(item))) tasks.put(taskToId(item), item)
        task_relationships.put(taskToId(item), filtered_children)
        duration_cache.clear()
        return true
    }

    /**
     * Adds the content of the given graph to this graph
     *
     * @param graph The other graph to pull from
     */
    fun join(graph: AbstractDirectionalAnalyticGraph<IDType, TaskType>) : Unit {
        for(entry in graph.tasks)
            if(entry.key !in tasks)
                tasks.put(entry.key, entry.value)

        for(entry in graph.task_relationships)
            if(entry.key in task_relationships) {
                entry.value.addAll(task_relationships[entry.key]!!)
                task_relationships.put(entry.key, entry.value)
            }
        duration_cache.clear()
    }

    /**
     * Finds the path with the longest duration from the given startKey to the end of the network
     *
     * @param startKey The id of the node to start with
     *
     * @return A list containing all of the nodes in this path
     */
    fun findCriticalPath(startKey: IDType): List<TaskType?> {
        val output_list : MutableSet<TaskType?> = mutableSetOf()
        if(startKey == null || startKey == "") return emptyList()
        output_list.add(idToTask(startKey))
        val child : TaskType = getTaskChildrenIDs(output_list.elementAt(0)!!).map { item -> idToTask(item) }.maxByOrNull { item -> calcTotalDuration(taskToId(item)) }
            ?: return output_list.toList()
        output_list.addAll(findCriticalPath(taskToId(child)))
        return output_list.toList()
    }

    /**
     * Calculates the total duration a given node, always taking the longest route.
     *
     * @param key The id of the node to start with
     *
     * @return The total duration from the start node
     */
    fun calcTotalDuration(key: IDType) : Float {
        println("test")
        if(!tasks.containsKey(key)) return 0f
        println(" test2")
        var duration = duration_cache.getOrDefault(key, 0f);
        println(" test3")
        if(duration != 0f) return duration
        println(" test4")
        val children : List<IDType> = getTaskChildrenIDs(idToTask(key)!!).toList()
        println(" test5")
        var longest = Float.MIN_VALUE
        println("test6")
        var r : IDType? = null
        println(" test7")
        for (item in children) {
            println(" test8-$item")
            val d = getDuration(idToTask(item))
            println(" test9-$d")
            if(d >= longest) {
                println(" test10")
                r = item
                println(" test11-$r")
                longest = d
                println(" test12-$longest")
            }
            println(" test13")
        }
        if(r == null) return 0f
        println(" test14")
        duration_cache.put(r, longest)
        println(" test15-${duration_cache[r]}")
        return duration + longest
    }

    fun toList() : List<TaskType> {
        return tasks.entries.map { e -> e.value }
    }

}

/**
 * The concrete implementation of [AbstractDirectionalAnalyticGraph] for a IDType of String and TaskType of Task
 *
 * @property list The list of tasks to start this graph with
 *
 * @author Charlie Mico
 *
 * @see AbstractDirectionalAnalyticGraph
 */
class TaskDAG(list: List<CriticalPathFactory>) : AbstractDirectionalAnalyticGraph<String, CriticalPathFactory>(list) {

    /**
     * Implementation of [AbstractDirectionalAnalyticGraph.taskToId] which wraps around [Task.id]
     *
     * @param task The task to get the id from
     *
     * @return [Task.id] or an empty string if task is null
     */
    override fun taskToId(task: CriticalPathFactory?): String {
        if(task == null) return ""
        return task.id
    }

    /**
     * Implementation of [AbstractDirectionalAnalyticGraph.getTaskChildrenIDs] which wrapps [Task.children]
     *
     * @param task The task to get the children from
     *
     * @return [Task.id]
     */
    override fun getTaskChildrenIDs(task: CriticalPathFactory): List<String> {
        return task.children
    }

    /**
     * Implementation of [AbstractDirectionalAnalyticGraph.getDuration] which uses [Task.duration] for it's calculation
     *
     * @param task The task to start from
     * @param recursively Should travel down the graph
     *
     * @return The duration or 0f if task is null
     */
    override fun getDuration(task: CriticalPathFactory?, recursively: Boolean): Float {
        if(task == null) return 0f
        var result = task.Duration
        if(recursively) {
            for (item in task.children)
                result += getDuration(idToTask(item), recursively)
        }
        return result
    }

}