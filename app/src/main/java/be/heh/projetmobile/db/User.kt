package be.heh.projetmobile.db

class User(
    var id: Int,
    var first_name: String,
    var last_name: String,
    var function: String,
    var pwd: String,
    var email: String
) {
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