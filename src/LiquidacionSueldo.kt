data class LiquidacionSueldo(
    val periodo: String,
    val empleado: Empleado,
    val imponible: Double,
    val descAfp: Double,
    val descSalud: Double,
    val descCesantia: Double
) {
    val totalDescuentos = descAfp + descSalud + descCesantia
    val sueldoLiquido = imponible - totalDescuentos

    private fun to2(x: Double): String {
        val v = if (x >= 0) x else -x
        val cents = (v * 100 + 0.5).toInt()
        val ent = cents / 100
        val dec = cents % 100
        val decTxt = if (dec < 10) "0$dec" else "$dec"
        return (if (x < 0) "-" else "") + "$ent.$decTxt"
    }

    fun resumen(): String = """
        Liquidación de ${empleado.nombre} (${empleado.rut}) - $periodo
        Imponible: ${to2(imponible)}
        AFP: ${to2(descAfp)}
        Salud: ${to2(descSalud)}
        Cesantía: ${to2(descCesantia)}
        Total Descuentos: ${to2(totalDescuentos)}
        Sueldo Líquido: ${to2(sueldoLiquido)}
    """.trimIndent()

    companion object {
        fun desdeEmpleado(periodo: String, emp: Empleado): LiquidacionSueldo {
            val imp = emp.totalHaberes()
            val afp = imp * emp.afp.tasa
            val salud = imp * 0.07
            val ces = imp * 0.006
            return LiquidacionSueldo(periodo, emp, imp, afp, salud, ces)
        }
    }
}

