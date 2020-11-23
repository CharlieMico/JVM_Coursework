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
    abstract fun <T> save(url: String, data: T): Boolean
    abstract fun <T> load(url: String) : T?
}

@ExperimentalPathApi
class FilePersistance() : APersistance() {

    override fun <T> save(file_path: String, data: T) : Boolean {
        return writeJson(file_path, object: TypeToken<T>(){}, data);
    }

    override fun <T> load(file_path: String) : T? {
        return readJson(file_path, object: TypeToken<T>(){})
    }


    // Actual logic
    private fun <T> readJson(file_path: String, token: TypeToken<T>) : T? {
        val path = Path(file_path)
        if(!path.exists()) return null
        val reader : JsonReader = JsonReader(FileReader(path.toFile()))
        return Gson().fromJson(reader, token.type)
    }

    private fun <T> writeJson(file_path: String, token: TypeToken<T>, list: T) : Boolean {
        val path = Path(file_path)
        if(!path.exists()) path.createFile()
        val writer : BufferedWriter = BufferedWriter(FileWriter(path.toFile()))
        writer.write(Gson().toJson(list))
        writer.flush()
        writer.close()
        return false
    }

}