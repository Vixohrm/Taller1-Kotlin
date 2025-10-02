data class Empleado(
    val rut: String,
    val nombre: String,
    val afp: AFP,
    val direccion: Direccion,
    val sueldoBase: Double,
    val horasExtras: Int = 0,
    val valorHoraExtra: Double = 0.0
) {
    fun totalHaberes(): Double {
        return sueldoBase + horasExtras * valorHoraExtra
    }
}
