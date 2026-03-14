package cm_ex3

fun main() {

    //começa em 100.0 e multiplica sempre por 0.6
    val saltos = generateSequence(100.0) { it * 0.6 }

        .take(15) //primeiros 15 para não encravar
        .toList() //criar a lista

    println("--- resultado dos saltos ---")

    for (s in saltos) {
        //salto for maior que 1 metro, mostramos
        if (s >= 1.0) {
            println("altura: " + "%.2f".format(s) + " metros")
        }
    }
}