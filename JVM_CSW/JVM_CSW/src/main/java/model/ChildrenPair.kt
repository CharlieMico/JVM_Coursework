package model

import javafx.fxml.FXML
import javafx.scene.control.TextField
import java.util.*

/**
 * @author josed
 */
class ChildrenPair(@field:FXML private val ChildrenTxt: TextField) {

    fun childs(b: String): List<String> {
        val child = ChildrenFactory(b)
        if (child.Children == "") return emptyList()
        val kid = child.Children.replace("\\s*".toRegex(), "")
        val items = kid.split(",").toTypedArray() // Split the string kid separated by , and store to array
        return listOf(*items)
    }
}