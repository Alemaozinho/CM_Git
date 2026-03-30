package ex1

sealed class Event {

    abstract val username: String
    abstract val timestamp: Long

    data class Login (

        override val username: String,
        override val timestamp: Long

    ) : Event()

    data class Purchase (

        override val username: String,
        val amount: Double,
        override val timestamp: Long


    ) : Event()

    data class Logout (

        override val username: String,
        override val timestamp: Long

    ) : Event()
}



fun List <Event>.filterByUser (username: String) : List <Event> {

    return this.filter { it.username == username }
}

fun List <Event>.totalSpent (usernameToFilter: String) : Double {
    return this.filterIsInstance <Event.Purchase> ().filter { it.username == usernameToFilter }.sumOf { it.amount }

}

fun processEvent (events: List <Event>, handler: (Event) -> Unit) {

    for (event in events) {

        handler(event)
    }
}

fun main (){

    val events = listOf(

        Event.Login ("alice", 1_000),
        Event.Purchase ("alice", 49.99, 1_100),
        Event.Purchase ("bob", 19.99, 1_020),
        Event.Login ("bob", 1_050),
        Event.Purchase ("alice", 15.00, 1_300),
        Event.Logout ("alice", 1_400),
        Event.Logout ("bob", 1_500)

    )

    processEvent (events) {

        event -> val msg = when (event) {

            is Event.Login -> "[Login] ${event.username} logged in at t=${event.timestamp}"
            is Event.Logout -> "[Logout] ${event.username} logout at t=${event.timestamp}"
            is Event.Purchase -> "[Purchase] ${event.username} spent ${event.amount}$ at t=${event.timestamp}"
        }
        println(msg)
    }
}


