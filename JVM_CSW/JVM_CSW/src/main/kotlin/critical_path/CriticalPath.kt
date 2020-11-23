package critical_path

import model.Task

abstract class AbstractDirectionalAnalyticGraph<IDType, TaskType>(val list: List<TaskType>) {
    private val task_relationships: MutableMap<IDType, MutableSet<IDType>> = HashMap()
    private val duration_cache: MutableMap<IDType, Float> = HashMap()
    private val tasks: MutableMap<IDType, TaskType> = HashMap()

    init {
        for (item in list) {
            val id : IDType = taskToId(item)
            val children : MutableSet<IDType> = getTaskChildrenIDs(item).toMutableSet()
            tasks.put(id, item)
            task_relationships.put(id, children)
        }
    }

    protected fun idToTask(id: IDType) : TaskType? {
        return tasks[id]
    }


    protected abstract fun taskToId(task : TaskType?) : IDType
    protected abstract fun getTaskChildrenIDs(task: TaskType) : List<IDType>
    protected abstract fun getDuration(task: TaskType?, recursively : Boolean = true) : Float

    fun extend(item: TaskType, children: List<TaskType>) : Boolean {
        val item_key = taskToId(item)
        val filtered_children : MutableSet<IDType> = task_relationships.getOrDefault(item_key, HashSet())
        filtered_children.addAll(children.map { t -> taskToId(t) }.filter { t -> tasks.containsKey(t) }.toMutableSet())
        if(!tasks.contains(taskToId(item))) tasks.put(taskToId(item), item)
        task_relationships.put(taskToId(item), filtered_children)
        duration_cache.clear()
        return true
    }

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
        TODO("Not Implemented")
    }

    fun findCriticalPath(startKey: IDType): List<TaskType?> {
        val output_list : MutableSet<TaskType?> = mutableSetOf()
        output_list.add(idToTask(startKey))
        val child : TaskType = getTaskChildrenIDs(output_list.elementAt(0)!!).map { item -> idToTask(item) }.maxByOrNull { item -> calcTotalDuration(taskToId(item)) }
            ?: return output_list.toList()
        output_list.addAll(findCriticalPath(taskToId(child)))
        return output_list.toList()
    }

    private fun calcTotalDuration(key: IDType) : Float {
        if(!tasks.containsKey(key)) return 0f
        var duration = duration_cache.getOrDefault(key, 0f);
        if(duration != 0f) return duration
        val children : List<IDType> = getTaskChildrenIDs(idToTask(key)!!).toList()
        var longest = Float.MIN_VALUE
        var r : IDType? = null
        for (item in children) {
            val d = calcTotalDuration(item)
            if(d >= longest) {
                r = item
                longest = d
            }
        }
        if(r == null) return 0f
        duration_cache.put(r, longest)
        return duration + longest
    }

}

class TaskDAG(list: List<Task>) : AbstractDirectionalAnalyticGraph<String, Task>(list) {

    override fun taskToId(task: Task?): String {
        if(task == null) return ""
        return task.id
    }

    override fun getTaskChildrenIDs(task: Task): List<String> {
        return task.children
    }

    override fun getDuration(task: Task?, recursively: Boolean): Float {
        require(task != null)
        var result = task.duration
        if(recursively) {
            for (item in task.children)
                result += getDuration(idToTask(item), recursively)
        }
        return result
    }

}