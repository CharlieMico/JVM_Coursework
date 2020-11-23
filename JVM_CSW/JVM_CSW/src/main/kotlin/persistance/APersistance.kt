package persistance

import model.ProjectFactory
import model.Task

abstract class APersistance() {

    // Project Handling
    abstract fun save_projects(url: String) : Boolean
    abstract fun load_projects(url: String) : List<ProjectFactory>

    // Task Handling
    abstract fun save_tasks(url: String) : Boolean
    abstract fun load_tasks(url: String): List<Task>
}

class FilePersistance() : APersistance() {
    override fun save_projects(file_path: String): Boolean {
        println("Saving projects to " + file_path)

        return false
    }

    override fun load_projects(file_path: String): List<ProjectFactory> {
        println("Loading projects from " + file_path)

        return emptyList()
    }

    override fun save_tasks(file_path: String): Boolean {
        println("Saving tasks to " + file_path)

        return false
    }

    override fun load_tasks(file_path: String): List<Task> {
        println("Loading tasks from " + file_path)

        return emptyList()
    }

}