package com.openclassrooms.magicgithub.api

import com.openclassrooms.magicgithub.model.User
import java.util.*

object FakeApiServiceGenerator {

    @JvmField
    var FAKE_USERS = mutableListOf(
        User("001", "Jake", "https://robohash.org/Jake.png"),
        User("002", "Paul", "https://robohash.org/Paul.png"),
        User("003", "Phil", "https://robohash.org/Phil.png"),
        User("004", "Guillaume", "https://robohash.org/Guillaume.png"),
        User("005", "Francis", "https://robohash.org/Francis.png"),
        User("006", "George", "https://robohash.org/George.png"),
        User("007", "Louis", "https://robohash.org/Louis.png"),
        User("008", "Mateo", "https://robohash.org/Mateo.png"),
        User("009", "April", "https://robohash.org/April.png"),
        User("010", "Louise", "https://robohash.org/Louise.png"),
        User("011", "Elodie", "https://robohash.org/Elodie.png"),
        User("012", "Helene", "https://robohash.org/Helene.png"),
        User("013", "Fanny", "https://robohash.org/Fanny.png"),
        User("014", "Laura", "https://robohash.org/Laura.png"),
        User("015", "Gertrude", "https://robohash.org/Gertrude.png"),
        User("016", "Chloé", "https://robohash.org/Chloé.png"),
        User("017", "April", "https://robohash.org/April.png"),
        User("018", "Marie", "https://robohash.org/Marie.png"),
        User("019", "Henri", "https://robohash.org/Henri.png"),
        User("020", "Rémi", "https://robohash.org/Rémi.png")
    )

    @JvmField
    var FAKE_USERS_RANDOM = Arrays.asList(
        User("021", "Lea", "https://robohash.org/Lea.png"),
        User("022", "Geoffrey", "https://robohash.org/Geoffrey.png"),
        User("023", "Simon", "https://robohash.org/Simon.png"),
        User("024", "André", "https://robohash.org/André.png"),
        User("025", "Leopold", "https://robohash.org/Leopold.png")
    )
}