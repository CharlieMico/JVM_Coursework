package persistance

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import model.ProjectFactory
import model.CriticalPathFactory
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Path
import kotlin.io.path.*

/**
 * Abstract class representing the base functionality of saving and loading tasks and projects
 *
 * @author Charlie Mico
 */
abstract class APersistance {
    /**
     * Saves provided to data to provided location
     *
     * @param url The location to save the data
     * @param data The data to save
     *
     * @param T The type of the data
     *
     * @return True if successful, false otherwise
     */
    protected abstract fun <T> save(url: String, data: T): Boolean

    /**
     * Loads data from the provided location
     *
     * @param url The location to load data from
     * @param T The type of the data
     *
     * @return The data, or null of the load failed
     */
    protected abstract fun <T> load(url: String) : T?

    /**
     * Saves specifically tasks to the provided location.
     *
     * @param url The location to save the data
     * @param data The list of tasks to sace
     *
     * @return True if successful, false otherwise
     *
     * @see save
     */
    abstract fun saveTasks(url: String, data: List<CriticalPathFactory>) : Boolean

    /**
     * Loads specifically tasks from the provided location
     *
     * @param url The location to load tasks from
     *
     * @return List of tasks, or an empty list if it failed
     *
     * @see load
     */
    abstract fun loadTasks(url: String) : List<CriticalPathFactory>

    /**
     * Saves specifically projects to the provided location
     *
     * @param url The location to save the projects
     * @param data The list of projects to save
     *
     * @return True if successful, false otherwise
     */
    abstract fun saveProjects(url: String, data: List<ProjectFactory>) : Boolean

    /**
     * Loads specifically projects from the provided location
     *
     * @param url The location to load the projects from
     *
     * @return A list of projects or an empty list if failed
     */
    abstract fun loadProjects(url: String) : List<ProjectFactory>
}

@ExperimentalPathApi
/**
 * A specific implementation of APersistance wherein the data is stored as json files.
 *
 * @author Charlie Mico
 */
class FilePersistence() : APersistance() {

    /**
     * A token representing a list of Tasks used to properally encode and decode the json file through GSON
     */
    private val taskListToken : TypeToken<List<CriticalPathFactory>> = object: TypeToken<List<CriticalPathFactory>>(){}

    /**
     * A token representing a list of projects used to properally encode and decode the json file through GSON
     */
    private val projectListToken : TypeToken<List<ProjectFactory>> = object: TypeToken<List<ProjectFactory>>(){}

    /**
     * A token representing a single project for encoding and decoding the json file through GSON
     */
    private val singleProjectToken : TypeToken<ProjectFactory> = object: TypeToken<ProjectFactory>(){}

    /**
     * Used to encode/decode a list of Strings
     */
    private val stringListToken : TypeToken<DataProjectIndex> = object: TypeToken<DataProjectIndex>(){}

    /**
     * Writes the given CriticalPathFactorys to the given filepath
     *
     * @param url The filepath to save this file to
     * @param data The list of tasks to save
     *
     * @return True is successful, false otherwise
     *
     * @see taskListToken
     * @see writeJson
     */
    override fun saveTasks(url: String, data: List<CriticalPathFactory>) : Boolean = writeJson(url, taskListToken, data)

    /**
     * Loads the given tasks from the given filepath
     *
     * @param url The filepath to load the tasks from
     *
     * @return A list containing the tasks, or an empty list if it fails
     *
     * @see taskListToken
     * @see readJson
     */
    override fun loadTasks(url: String) : List<CriticalPathFactory> = readJson(url, taskListToken) ?: emptyList()

    /**
     * Saves the given projects to the filepath
     *
     * @param url The filepath to save the projects to
     * @param data The list of projects to save
     *
     * @return True is successful, false otherwise
     *
     * @see projectListToken
     * @see writeJson
     */
    override fun saveProjects(url: String, data: List<ProjectFactory>) : Boolean = writeJson(url, projectListToken, data)

    /**
     * Loads projects from the given filepath
     *
     * @param url The filepath to load from
     *
     * @return A list containing projects, or an empty if loading failed
     *
     * @see projectListToken
     * @see readJson
     */
    override fun loadProjects(url: String) : List<ProjectFactory> = readJson(url, projectListToken) ?: emptyList()


    /**
     * Implementation of [APersistance.save] which wraps the local [writeJson], not recommended to be used
     *
     * @see APersistance.save
     * @see writeJson
     */
    override fun <T> save(url: String, data: T) : Boolean {
        return writeJson(url,  object: TypeToken<T>(){}, data);
    }

    /**
     * Implementation of [APersistance.load] which wraps the local [readJson], not recommended to be used
     *
     * @see APersistance.load
     * @see readJson
     */
    override fun <T> load(url: String) : T? {
        return readJson(url, object: TypeToken<T>(){})
    }


    /**
     * Reads the given token type from the given filepath and returns it. Using Gson
     *
     * @param file_path The file to be loaded
     * @param token The token for encoding and decoding json
     *
     * @param T The type to be loaded
     *
     * @return The loaded values, or null if fails
     *
     * @see Gson
     */
    private fun <T> readJson(file_path: String, token: TypeToken<T>) : T? {
        val path = Path(file_path)
//        println(path)
        if(!path.exists()) return null
        val reader : JsonReader = JsonReader(FileReader(path.toFile()))
        return Gson().fromJson(reader, token.type)
    }

    /**
     * Writes the given token type to the given filepath using Gson
     *
     * @param file_path The file to be saves
     * @param token The token for encoding and decoding json
     * @param data The data to be written
     *
     * @param T The type of the data
     *
     * @return True if successful, or false otherwise
     *
     * @see Gson
     */
    private fun <T> writeJson(file_path: String, token: TypeToken<T>, data: T) : Boolean {
        val path = Path(file_path)
        if(!path.exists()) makeFile(path)//path.createFile()
        val writer : BufferedWriter = BufferedWriter(FileWriter(path.toFile()))
        writer.write(Gson().toJson(data))
        writer.flush()
        writer.close()
        return true
    }

    //TODO: Rename to better
    private fun saveProject(file_path: String, project: ProjectFactory) : Boolean {
//        if(Path(file_path).isWritable())
            return writeJson(file_path, singleProjectToken, project)
//        return false
    }

    //TODO: Create signiture in parent class
    fun saveProject(folder_path: String, project: ProjectFactory, task_list: List<CriticalPathFactory>) : Boolean {
        if(!Path(folder_path).exists()) makeDirectory(Path(folder_path))//Path(folder_path).createDirectory()
        val path = Path(folder_path + "/" + project.id)
        if(!path.exists()) makeDirectory(path)//path.createDirectory()
        if(!saveTasks("$path\\tasks.json", task_list))
            println("Couldn't Save ${path.toAbsolutePath()}/tasks.json")   else println("Saved ${path.toAbsolutePath()}/task.json")
        if(!saveProject("$path\\details.json", project))
            println("Couldn't save ${path.toAbsolutePath()}/details.json") else println("Saved ${path.toAbsolutePath()}/details.json")
        return true
    }

    /**
     * Load's a given project from it's directory name.
     *
     * @param folder_path The location to load from
     *
     * @return A pair of Project and list of Tasks; Project can be null, list of Tasks is empty in this case
     */
    //TODO: Create signature in parent class
    fun loadProject(folder_path: String) : Pair<ProjectFactory?, List<CriticalPathFactory>> {
        val folder  = Path(folder_path)
//        println(folder)
        if(!folder.exists()) return Pair(null, emptyList())
        val project : ProjectFactory?
                = readJson(folder_path.plus(folder.fileSystem.separator).plus("details.json"), singleProjectToken)
        val taskList : List<CriticalPathFactory>
                = readJson(folder_path.plus(folder.fileSystem.separator).plus("tasks.json"), taskListToken) ?: emptyList()
        return Pair(project, taskList)
    }

    fun makeDirectory(path: Path) {
        if(!path.parent.exists()) makeDirectory(path.parent)
        path.createDirectory()
    }

    fun makeFile(path: Path) {
        if(!path.parent.exists()) makeDirectory(path.parent)
        path.createFile()
    }

    // Takes projects and saves their ids to disk
    fun saveProjectIndex(file_path: String, projects: List<ProjectFactory>) : Boolean {
        val file = Path(file_path.plus("project_index.json") )
        if(!file.exists()) makeFile(file) //file.createFile()
        val ids : List<String> = projects.filter{ p -> p != null }.map { project -> project.id }
        println(projects)
        println(ids)
        return writeJson(file.toString(), stringListToken, DataProjectIndex(ids))
    }



    // Returns the above list of ids
    fun loadProjectIndex(file_path: String) : List<String> {
        val file = Path(file_path + "project_index.json")
        if(!file.exists()) return emptyList()
        val a = readJson(file_path + "project_index.json", stringListToken)
        if(a != null) return a.data
        return emptyList()
    }

    fun loadAllProjects(folder_path: String) : MutableList<Pair<ProjectFactory?, List<CriticalPathFactory>>> {
        val index : List<String> = loadProjectIndex(folder_path)
        if(index.isEmpty()) return ArrayList()

        index.forEach { e -> println(e) }

        val output : ArrayList<Pair<ProjectFactory?, List<CriticalPathFactory>>> = ArrayList()

        index
            .asSequence() // IDE Said this might improve performance
            .filter  { i -> i.isNotEmpty() } // Shouldn't be nessisary
            .map     { i -> loadProject(folder_path.plus(Path(folder_path).fileSystem.separator).plus(i)) }
            .forEach { i -> output.add(i) }
        return output
    }

    fun saveAllProjects(folder_path: String, data: List<Pair<ProjectFactory?, List<CriticalPathFactory>>>) : Boolean {
        data.filter { e -> e.first != null }.forEach { e -> saveProject(folder_path, e.first!!, e.second) }
        saveProjectIndex(folder_path, data.filter {e -> e.first != null }.map { e -> e.first!! })
        return true
    }

    fun loadAllProjectsFromIndex(file_path: String) : MutableList<Pair<ProjectFactory?, List<CriticalPathFactory>>> {
        return loadAllProjects(Path(file_path).parent.toString())
    }

}

private data class DataProjectIndex(val data: List<String>)