package ex1_3

class Pipeline {

    private val stages = mutableListOf <Pair<String, (List<String>) -> List<String>>>()

    fun addStage (name: String, transform: (List<String>) -> List<String>) {

        stages.add(name to transform)

    }

    fun execute (input : List<String>): List<String> {

        var currentData = input

        for ((_, transform) in stages) {

            currentData = transform(currentData)

        }

        return currentData
    }

    fun describe () {

        println ("Pipeline Stages")

        stages.forEachIndexed { index, stage -> println ("${index + 1}) ${stage.first}") }

    }
}

fun buildPipeline (action : Pipeline.() -> Unit) : Pipeline {

    val pipeline = Pipeline()
    pipeline.action()

    return pipeline

}

fun main(){

    val logProcessor = buildPipeline {

        addStage("Trim") {lines -> lines.map{ it.trim() } }

        addStage("Filter errors") { lines -> lines.filter { it.contains ("ERROR", ignoreCase = true) } }

        addStage("Uppercase") { lines -> lines.map{ it.uppercase() } }

        addStage ("Add index") { lines -> lines.mapIndexed { index, line -> "${index + 1}. $line" } }

    }

    val Logs = listOf ("INFO: server started ",
        "ERROR: disk full ",
        "DEBUG: checking config ",
        "ERROR: out of memory ",
        "ERROR: connection timeout " )

    logProcessor.describe()

    val finalOutput = logProcessor.execute(Logs)
    finalOutput.forEach { println(it) }
}


