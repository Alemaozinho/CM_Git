package cm_vl

//molde geral para os livros
abstract class Livro(
    val titulo: String,
    val autor: String,
    val ano: Int,
    copias_inicio: Int
) {
    //seguranca para o stock nao ser negativo
    var stock: Int = if (copias_inicio < 0) 0 else copias_inicio
        set(valor) {
            field = if (valor < 0) 0 else valor
            if (field == 0) println("aviso: o livro '$titulo' esgotou")
        }

    //define a era do livro conforme o ano
    val era: String
        get() = when {
            ano < 1980 -> "classico"
            ano in 1980..2010 -> "moderno"
            else -> "contemporaneo"
        }

    abstract fun info_guarda(): String

    override fun toString(): String {
        return "titulo: $titulo, autor: $autor, era: $era, disponivel: $stock copias"
    }
}

//tipo de livro digital
class LivroDigital(
    titulo: String, autor: String, ano: Int, copias: Int,
    val tamanho: Double, val formato: String
) : Livro(titulo, autor, ano, copias) {

    override fun info_guarda() = "guardado digitalmente: $tamanho mb, formato: $formato"
}

//tipo de livro fisico
class LivroFisico(
    titulo: String, autor: String, ano: Int, copias: Int,
    val peso: Int, val capa_dura: Boolean = true
) : Livro(titulo, autor, ano, copias) {

    override fun info_guarda(): String {
        val tipo_capa = if (capa_dura) "sim" else "nao"
        return "livro fisico: ${peso}g, capa dura: $tipo_capa"
    }
}

//gestor da biblioteca
class Biblioteca(val nome: String) {
    private val lista_livros = mutableListOf<Livro>()

    companion object {
        private var total_criado = 0
        fun total_sistema() = total_criado
    }

    fun adicionar(livro: Livro) {
        lista_livros.add(livro)
        total_criado++
        println("livro '${livro.titulo}' do autor ${livro.autor} foi guardado")
    }

    fun emprestar(titulo: String) {
        val l = lista_livros.find { it.titulo.equals(titulo, ignoreCase = true) }
        if (l != null && l.stock > 0) {
            l.stock--
            println("sucesso: levaste o '$titulo'. restam: ${l.stock}")
        } else {
            println("erro: o livro '$titulo' nao esta disponivel")
        }
    }

    fun devolver(titulo: String) {
        val l = lista_livros.find { it.titulo.equals(titulo, ignoreCase = true) }
        if (l != null) {
            l.stock++
            println("confirmado: devolveste o '$titulo'. stock atual: ${l.stock}")
        }
    }

    fun mostrar_tudo() {
        println("\n--- catalogo da biblioteca ---")
        lista_livros.forEach {
            println(it.toString())
            println("detalhes: ${it.info_guarda()}")
        }
    }

    fun procurar_autor(quem: String) {
        println("\nlivros de $quem:")
        lista_livros.filter { it.autor.equals(quem, ignoreCase = true) }
            .forEach { println("- ${it.titulo} (${it.era}, ${it.stock} em stock)") }
    }
}

//dados do membro
data class Membro(val nome: String, val id: String, val emprestados: List<String>)

fun main() {
    val b = Biblioteca("biblioteca central")

    val d = LivroDigital("kotlin em acao", "dmitry jemerov", 2017, 5, 4.5, "pdf")
    val f = LivroFisico("clean code", "robert c. martin", 2008, 3, 650, true)
    val c = LivroFisico("1984", "george orwell", 1949, 2, 400, false)

    b.adicionar(d)
    b.adicionar(f)
    b.adicionar(c)

    b.mostrar_tudo()

    println("\n--- a testar emprestimos ---")
    b.emprestar("clean code")
    b.emprestar("1984")
    b.emprestar("1984")
    b.emprestar("1984")

    println("\n--- a devolver ---")
    b.devolver("1984")

    println("\n--- busca por autor ---")
    b.procurar_autor("george orwell")
}