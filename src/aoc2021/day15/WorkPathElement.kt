package aoc2021.day15

import java.io.Serializable

data class WorkPathElement<E>(val element: E) : Serializable {

    companion object {
        // build a path element with a distance of 1
        fun <E> buildNext(element: E): WorkPathElement<E> {
            val workPathElement = WorkPathElement(element)
            workPathElement.distance = 1
            return workPathElement
        }
    }

    /**
     * distance to the source object, updated continuously during processing
     */
    var distance = Int.MAX_VALUE

    /**
     * predecessor to this node, updated continuously during processing
     */
    var predecessor: E? = null

    /**
     * flag set to true when the node has been processed
     */
    var isEvaluated = false

}
