package cm_ex2
import java.util.Scanner

fun main() {
    //scanner para ler o teclado
    val leitor = Scanner(System.`in`)

    //try para o programa não fechar sozinho caso de engano
    try {
        println("--- calculadora ---")

        print("primeiro numero: ")
        val n1 = leitor.nextInt()

        print("conta a fazer (+, -, *, /, and, or, shl, shr): ")
        //o uppercase serve para aceitar tanto 'and' como 'AND'
        val op = leitor.next().uppercase()

        print("segundo numero: ")
        val n2 = leitor.nextInt()

        //when para escolher a conta e parece um menu
        val resultado: Int = when (op) {
            "+" -> n1 + n2
            "-" -> n1 - n2
            "*" -> n1 * n2
            //se o segundo numero for 0 ele avisa no catch
            "/" -> if (n2 != 0) n1 / n2 else throw ArithmeticException()

            //contas de sim ou nao (booleano)
            "AND" -> if (n1 > 0 && n2 > 0) 1 else 0
            "OR" -> if (n1 > 0 || n2 > 0) 1 else 0

            //contas de bits
            "SHL" -> n1 shl n2 //deslocar para a esquerda
            "SHR" -> n1 shr n2 //deslocar para a direita

            else -> throw Exception()
        }

        //resultados de tres maneiras
        println("\n--- resultados ---")

        //numero normal
        println("em decimal: $resultado")

        //numero em hexadecimal
        println("em hexadecimal: " + resultado.toString(16))

        //verdadeiro ou falso (se for 0 é falso)
        val resBool = resultado != 0
        println("em booleano: $resBool")

    } catch (e: ArithmeticException) {
        println("erro: nao podes dividir por zero")
    } catch (e: Exception) {
        println("erro: escreveste algo mal ou essa operacao nao existe")
    }
}