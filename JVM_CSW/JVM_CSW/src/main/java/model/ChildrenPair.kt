package model

import javafx.fxml.FXML
import javafx.scene.control.TextField
import java.util.*

/**
 * @author josed
 */
class ChildrenPair(@field:FXML private val ChildrenTxt: TextField) {
    fun childs(): List<String> {
        val child = ChildrenFactory(ChildrenTxt.text)
        val kid = child.Children
        val items = kid.split(",").toTypedArray() // Split the string kid separated by , and store to array
        return listOf(*items)
    }
}