package ex1_4
import kotlin.math.sqrt

data class Vec2 (val x: Double, val y: Double): Comparable<Vec2> {

    operator fun plus (other: Vec2) = Vec2(x + other.x, y + other.y)
    operator fun minus (other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun times (other: Double) = Vec2(x * other, y * other)
    operator fun unaryMinus() = Vec2(-x, -y)

    //private fun magnitude() = sqrt(x * x + y * y)

    override fun compareTo (other: Vec2) : Int { return this.magnitude().compareTo(other.magnitude())}

    fun magnitude(): Double = sqrt (x * x + y * y)
    fun dot (other: Vec2) = x * other.x + y * other.y
    fun normalized() : Vec2 {

        val mag = magnitude()
        if (mag == 0.0){

            throw IllegalArgumentException ("impossivel normalizar um vetor zero (magnitude já é zero)")
        }
        return Vec2(x / mag, y / mag)
    }

    operator fun get(index: Int): Double {
        return when (index) {
            0 -> x
            1 -> y
            else -> throw IndexOutOfBoundsException("O índice $index está fora dos limites para Vec2 (apenas 0 e 1 são permitidos).")
        }
    }
}


fun main(){

    val v1 = Vec2 (3.0, 4.0)
    val v2 = Vec2 (1.0, 2.0)

    println("v1        = $v1")
    println("v2        = $v2")
    println("v1 + v2   = ${v1 + v2}")
    println("v1 - v2   = ${v1 - v2}")
    println("v1 * 2.0  = ${v1 * 2.0}")
    println("-v1       = ${-v1}")
    println("|v1|      = ${v1.magnitude()}")
    println("v1 dot v2 = ${v1.dot(v2)}")
    println("norm(v1)  = ${v1.normalized()}")

    println("v1[0]     = ${v1[0]}")
    println("v1[1]     = ${v1[1]}")
    println("v1 > v2   = ${v1 > v2}")
    println("v1 < v2   = ${v1 < v2}")

    val vectors = listOf(Vec2(1.0, 0.0), Vec2(3.0, 4.0), Vec2(0.0, 2.0))

    println("Longest   = ${vectors.maxByOrNull { it.magnitude() }}")
    println("Shortest  = ${vectors.minByOrNull { it.magnitude() }}")

}