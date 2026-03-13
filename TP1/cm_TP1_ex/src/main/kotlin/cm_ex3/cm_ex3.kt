package cm_ex3

//ficha com titulo, autor e ano
data class Livro(val titulo: String, val autor: String, val ano: Int)

fun main() {
    //lista com alguns livros para ter onde procurar
    val livros = listOf(
        Livro("mensagens", "fernando pessoa", 1934),
        Livro("os maias", "eça de queirós", 1888),
        Livro("o desassossego", "fernando pessoa", 1982),
        Livro("memorial do convento", "josé saramago", 1982)
    )

    println("--- biblioteca ---")

    //o quero procurar
    val autorParaPesquisar = "fernando pessoa"
    println("a procurar livros do autor: $autorParaPesquisar")
    println("---------------------------")

    //percorrer a lista de livros um por um
    for (l in livros) {
        // se o autor do livro for o que eu escrevi em cima, ele mostra o titulo
        if (l.autor == autorParaPesquisar) {
            println("encontrei: " + l.titulo + " que saiu em " + l.ano)
        }
    }

    //mostrar o tamanho da lista total
    println("---------------------------")
    println("tenho " + livros.size + " livros na lista")
}