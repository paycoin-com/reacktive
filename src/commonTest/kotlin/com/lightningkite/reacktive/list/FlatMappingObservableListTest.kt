package com.lightningkite.reacktive.list

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Created by joseph on 9/26/16.
 */
class FlatMappingObservableListTest {

    fun makeTestList() = mutableObservableListOf(
            mutableObservableListOf('a', 'b', 'c'),
            mutableObservableListOf('e', 'f', 'g'),
            mutableObservableListOf('h', 'i', 'j')
    )

    @Test
    fun addToSub() {
        var callbackOccurred = false
        val list = makeTestList()
        val flat = list.flatMapping { it }

        flat.onListAdd += { char, index ->
            assertEquals(char, 'z')
            assertEquals(4, index)

            callbackOccurred = true
        }
        list[1].add(1, 'z')

        assertTrue(callbackOccurred)
    }

    @Test
    fun addToWhole() {
        var callbackOccurred = false
        val list = makeTestList()
        val flat = list.flatMapping { it }

        val sublist = mutableObservableListOf('x', 'y', 'z')
        var currentIndex = 0

        flat.onListAdd += { char, index ->
            assertEquals(sublist[currentIndex], char)
            assertEquals(currentIndex + 3, index)
            currentIndex++

            callbackOccurred = true
        }
        list.add(1, sublist)

        assertTrue(callbackOccurred)
    }

    @Test
    fun removeFromSub() {
        var callbackOccurred = false
        val list = makeTestList()
        val flat = list.flatMapping { it }

        flat.onListRemove += { char, index ->
            assertEquals(char, 'f')
            assertEquals(4, index)

            callbackOccurred = true
        }
        list[1].removeAt(1)

        assertTrue(callbackOccurred)
    }

    @Test
    fun removeFromWhole() {
        var callbackOccurred = false
        val list = makeTestList()
        val flat = list.flatMapping { it }

        val sublist = list[1]
        var currentIndex = sublist.lastIndex

        flat.onListRemove += { char, index ->
            assertEquals(sublist[currentIndex], char)
            assertEquals(currentIndex + 3, index)
            currentIndex--

            callbackOccurred = true
        }
        list.removeAt(1)

        assertTrue(callbackOccurred)
    }

    @Test
    fun changeToSub() {
        var callbackOccurred = false
        val list = makeTestList()
        val flat = list.flatMapping { it }

        flat.onListChange += { old, char, index ->
            assertEquals(old, 'f')
            assertEquals(char, 'z')
            assertEquals(4, index)

            callbackOccurred = true
        }
        list[1][1] = 'z'

        assertTrue(callbackOccurred)
    }

    @Test
    fun changeToWhole() {
        var callbackOccurred = false
        val list = makeTestList()
        val flat = list.flatMapping { it }

        val newList = mutableObservableListOf('x', 'y')
        val oldList = list[1]
        var newIndex = 0
        var oldIndex = oldList.lastIndex

        flat.onListRemove += { char, index ->
            assertEquals(oldList[oldIndex], char)
            assertEquals(oldIndex + 3, index)
            oldIndex--

            callbackOccurred = true
        }
        flat.onListAdd += { char, index ->
            assertEquals(newList[newIndex], char)
            assertEquals(newIndex + 3, index)
            newIndex++

            callbackOccurred = true
        }
        list[1] = newList

        assertTrue(callbackOccurred)
    }

    @Test
    fun moveInSub() {
        var callbackOccurred = false
        val list = makeTestList()
        val flat = list.flatMapping { it }

        flat.onListMove += { char, oldIndex, index ->
            assertEquals(char, 'f')
            assertEquals(4, oldIndex)
            assertEquals(5, index)
            callbackOccurred = true
        }
        list[1].move(1, 2)

        assertTrue(callbackOccurred)
    }

    @Test
    fun moveInWhole() {
        var callbackOccurred = false
        val list = makeTestList()
        val flat = list.flatMapping { it }

        val sublist = list[1]
        var removeIndex = sublist.lastIndex
        var addIndex = 0

        flat.onListRemove += { char, index ->
            assertEquals(sublist[removeIndex], char)
            assertEquals(removeIndex + 3, index)
            removeIndex--

            callbackOccurred = true
        }
        flat.onListAdd += { char, index ->
            assertEquals(sublist[addIndex], char)
            assertEquals(addIndex + 6, index)
            addIndex++

            callbackOccurred = true
        }
        list.move(1, 2)

        assertTrue(callbackOccurred)
    }

    @Test
    fun addCheckTest() {
        val list = makeTestList()
        val flat = list.flatMapping { it }
        println(flat.boundaryIndexes)
        list[1].add('z')
        println(flat.boundaryIndexes)
        for (i in 0..flat.size - 1) {
            println(i)
            flat[i]
        }
    }

    @Test
    fun addToEmptyCheckTest() {
        val list = mutableObservableListOf(
                mutableObservableListOf('a', 'b', 'c'),
                mutableObservableListOf(),
                mutableObservableListOf('h', 'i', 'j')
        )
        val flat = list.flatMapping { it }
        println(flat.boundaryIndexes)
        list[1].add('z')
        println(flat.boundaryIndexes)
        for (i in 0..flat.size - 1) {
            println(i)
            flat[i]
        }
    }

    @Test
    fun removeCheckTest() {
        val list = makeTestList()
        val flat = list.flatMapping { it }
        println(flat.boundaryIndexes)
        list[1].removeAt(1)
        println(flat.boundaryIndexes)
        for (i in 0..flat.size - 1) {
            println(i)
            flat[i]
        }
    }

    @Test
    fun removeMultipleTest() {
        val multiple = mutableObservableListOf(
                mutableObservableListOf('a', 'b', 'c', 'd'),
                mutableObservableListOf('e', 'f', 'g'),
                mutableObservableListOf('h', 'i', 'j')
        )
        val flattened = multiple.flatMapping { it }
        flattened.onListAdd += {_, _ ->}

        multiple[0].removeAll { it.toInt() % 2 == 0 }
    }

}
