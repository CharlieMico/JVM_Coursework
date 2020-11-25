package data

import model.ChildrenFactory

object UltimateTest {
    fun a(b: String) : List<String> {
        val child = ChildrenFactory(b)
        if(child.Children == "") return emptyList()
        val kid = child.Children.replace("\\s*".toRegex(), "")
        val items = kid.split(",").toTypedArray() // Split the string kid separated by , and store to array
        return listOf(*items)
    }
}