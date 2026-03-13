package cm_ex1

fun main() {

    val squaresA = IntArray(50) { i -> (i + 1) * (i + 1) } //para cada posicao i faz o seguinte (->)
    val squaresB = (1..50).map { it * it }.toTypedArray() //it representa o numero
    val squaresC = Array(50) { i -> (i + 1) * (i + 1) } //trabalha com objetos

    println("Alínea A: ${squaresA.joinToString(", ")}")
    println("Alínea B: ${squaresB.joinToString(", ")}")
    println("Alínea C: ${squaresC.joinToString(", ")}")

}