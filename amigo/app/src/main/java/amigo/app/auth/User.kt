package amigo.app.auth

class User(val uid: String = "",
           val firstname: String = "",
           val lastname: String = "",
           val iscarer: Boolean = false,
           val onlinestatus: Boolean = false,
           val tripstatus: Boolean = false,
           val currentlocation: UserLocation = UserLocation()) {

    // default constructor enables deserialization
    // like this: (User::class.java)
    constructor() : this("", "", "", false, false, false, UserLocation())
}

class UserLocation(val latitude: Double = 0.0,
                   val longitude: Double = 0.0,
                   val time: Long = 0) {
    constructor() : this(0.0, 0.0, 0)
}