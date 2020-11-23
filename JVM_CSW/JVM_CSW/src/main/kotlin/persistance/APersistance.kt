package persistance

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import model.ProjectFactory
import model.Task
import java.io.BufferedWriter
import java.io.FileReader
import java.io.FileWriter
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.createFile
import kotlin.io.path.exists

abstract class APersistance() {
    protected abstract fun <T> save(url: String, data: T): Boolean
    protected abstract fun <T> load(url: String) : T?
}

@ExperimentalPathApi
class FilePersistence() : APersistance() {

    private val taskToken : TypeToken<List<Task>> = object: TypeToken<List<Task>>(){}
    private val projectToken : TypeToken<List<ProjectFactory>> = object: TypeToken<List<ProjectFactory>>(){}

    fun saveTasks(file_path: String, data: List<Task>) : Boolean = writeJson(file_path, taskToken, data)
    fun loadTasks(file_path: String) : List<Task> = readJson(file_path, taskToken) ?: emptyList()

    fun saveProjects(file_path: String, data: List<ProjectFactory>) : Boolean = writeJson(file_path, projectToken, data)
    fun loadProjects(file_path: String) : List<ProjectFactory> = readJson(file_path, projectToken) ?: emptyList()



    override fun <T> save(url: String, data: T) : Boolean {
        return writeJson(url,  object: TypeToken<T>(){}, data);
    }

    override fun <T> load(url: String) : T? {
        return readJson(url, object: TypeToken<T>(){})
    }


    // Actual logic
    private fun <T> readJson(file_path: String, token: TypeToken<T>) : T? {
        val path = Path(file_path)
        if(!path.exists()) return null
        val reader : JsonReader = JsonReader(FileReader(path.toFile()))
        return Gson().fromJson(reader, token.type)
    }

    private fun <T> writeJson(file_path: String, token: TypeToken<T>, data: T) : Boolean {
        val path = Path(file_path)
        if(!path.exists()) path.createFile()
        val writer : BufferedWriter = BufferedWriter(FileWriter(path.toFile()))
        writer.write(Gson().toJson(data))
        writer.flush()
        writer.close()
        return false
    }
}