package ex1_2

import javax.sql.rowset.Predicate

class Cache <K : Any, V : Any>{

    private val storage = mutableMapOf<K, V>()

    fun put (key: K, value: V) {

        storage [key] = value
    }

    fun get (key: K): V? {

        return storage[key]
    }

    fun evict (key: K) {

        storage.remove(key)
    }

    fun size () : Int {

        return storage.size
    }

    fun getOrPut (key: K, default: () -> V) : V {

        val existingValue = get (key)

        if (existingValue != null) {

            return existingValue

        }

        val newValue = default()

        put (key, newValue)

        return newValue
    }

    fun transform (key : K, action: (V) -> V) : Boolean {

        val currentValue = get (key)
        if (currentValue != null) {

            val newValue = action(currentValue)
            put (key, newValue)

            return true
        }

        return false

    }

    fun snapshot () : Map<K, V> {

        return storage.toMap()

    }

    fun filterValues (predicate: (V) -> Boolean ): Map <K, V> {

        return storage.filter { entry -> predicate (entry.value)}

    }

}

fun main (){

    println("--- World frequency cache ---")

    val worldCache = Cache <String, Int> ()

    worldCache.put ("kotlin", 1)
    worldCache.put ("scala", 1)
    worldCache.put ("haskell", 1)

    println ("size: ${worldCache.size()}")
    println ("Frequency of \"kotlin\": ${worldCache.get ("kotlin")}")

    val v1 = worldCache.getOrPut ("kotlin") { 10 }
    val v2 = worldCache.getOrPut ("java") { 0 }

    println ("getOrPut \"kotlin\": $v1")
    println ("getOrPut \"java\": $v2")
    println ("Size after getOrPut: ${worldCache.size ()}")

    val t1 = worldCache.transform ("kotlin") { it + 1}
    val t2 = worldCache.transform ("cobol") {it + 1}

    println ("Transform \"kotlin\": $t1")
    println ("Transform \"cobol\": $t2")

    println ("Snapshot: ${worldCache.snapshot()}")

    println()

    println ("---- Id registory cache ---")

    val idCache = Cache<Int, String> ()

    idCache.put (1, "Alice")
    idCache.put (2, "Bob")

    println ("Id 1 -> ${idCache.get(1)}" )
    println ("Id 2 -> ${idCache.get(2)}")

    idCache.evict (1)

    println ("After evict id 1 size -> ${idCache.size()}")
    println ("id 1after evict -> ${idCache.get(1)}")

    println ("--- Challenge: Filtered Frequency (count > 0) ---")

    val wodlsCache = worldCache.filterValues { it > 0 }
    println ("All words in frequency cache with count >0: $wodlsCache")


}

