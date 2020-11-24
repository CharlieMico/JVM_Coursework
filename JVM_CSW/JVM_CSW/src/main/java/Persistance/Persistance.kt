package Persistance

import utils.Constants
import com.google.gson.Gson
import model.ProjectFactory
import java.io.BufferedReader
import java.io.FileWriter
import java.io.IOException

/**
 * @author josed
 */
class Persistance {
    @Throws(IOException::class)
    fun saveData(factory: ProjectFactory?, list: MutableList<ProjectFactory?>, url: BufferedReader?) {
        val gson = Gson()

        list.add(factory)
        val json = gson.toJson(list)
        println(json)
        val fileWriter = FileWriter(Constants.TRIAL_FILE) // writing back to the file
        fileWriter.write(json)
        fileWriter.flush()
    }
}