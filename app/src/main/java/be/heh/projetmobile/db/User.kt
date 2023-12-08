package be.heh.projetmobile.db

class User(i : Int) {
    var id: Int = 0
        public get
        private set
    var first_name = "null"
        public get
        private set
    var last_name = "null"
        public get
        private set
    var function = "null"
        public get
        private set
    var pwd: String = "null"
        public get
        private set
    var email: String = "null"
        public get
        private set

    constructor(
        i: Int, ln: String, fn: String,
        p: String, e: String, f: String
    ) : this(i) {
        id = i
        first_name = fn
        last_name = ln
        function = f
        pwd = p
        email = e
    }

    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(
            "ID : " + id.toString() + "\n" +
            "First name : " + first_name + "\n" +
            "Last name : " + last_name + "\n" +
            "Rôle : " + function + "\n" +
            "Password : " + pwd + "\n" +
            "Email : " + email
        )
        return sb.toString()
    }
}