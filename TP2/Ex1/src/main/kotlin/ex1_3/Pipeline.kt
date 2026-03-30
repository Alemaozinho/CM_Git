package ex1_3

class Pipeline {

    private val stages = mutableListOf <Pair<String, (List<String) -> List<String>>>()

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

        stages.forEachIndexed { index, stage -> println ("${index + 1}) ${stage.first}{") }

    }

    fun buildPipeline (action : Pipeline.() -> Unit) : Pipeline {

        val Pipeline = Pipeline()
        Pipeline.action()

        return Pipeline

    }

    


}