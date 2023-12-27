package be.heh.projetmobile.db.material

class Material(
    var id: Int,
    var name: String,
    var type: String,
    var brand: String,
    var ref: Int,
    var maker: String,
    var available: Int
) {
    override fun toString(): String {
        val sb = StringBuilder()
        sb.append(
            "ID : " + id.toString() + "\n" +
            "Name : " + name + "\n" +
            "Type : " + type + "\n" +
            "Brand : " + brand + "\n" +
            "Ref : " + ref + "\n" +
            "Maker : " + maker + "\n" +
            if (available === 1) {
                sb.append("\nStatus : Disponible")
            } else {
                sb.append("\nStatus : Indisponible")
            }
        )
        return sb.toString()
    }
}