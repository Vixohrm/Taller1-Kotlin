class Repositorio {
    val afps: MutableList<AFP> = mutableListOf()
    val empleados: MutableList<Empleado> = mutableListOf()
    val liquidaciones: MutableList<LiquidacionSueldo> = mutableListOf()


    fun agregarAfp(afp: AFP) { afps.add(afp) }
    fun buscarAfpPorNombre(nombre: String): AFP? = afps.find { it.nombre.equals(nombre, ignoreCase = true) }


    fun agregarEmpleado(empleado: Empleado) {
        require(empleados.none { it.rut.equals(empleado.rut, ignoreCase = true) }) { "Ya existe un empleado con el RUT ${'$'}{empleado.rut}" }
        empleados.add(empleado)
    }


    fun eliminarEmpleadoPorRUT(rut: String): Boolean = empleados.removeIf { it.rut.equals(rut, ignoreCase = true) }
    fun buscarEmpleadoPorRUT(rut: String): Empleado? = empleados.find { it.rut.equals(rut, ignoreCase = true) }


    fun generarLiquidacion(rut: String, periodo: String): LiquidacionSueldo? {
        val empleado = buscarEmpleadoPorRUT(rut) ?: return null
        val liq = LiquidacionSueldo.desdeEmpleado(periodo, empleado)
        liquidaciones.add(liq)
        return liq
    }


    fun filtrarEmpleadosPorAFP(nombreAfp: String): List<Empleado> = empleados.filter { it.afp.nombre.equals(nombreAfp, ignoreCase = true) }


    fun totalDescuentosNomina(periodo: String? = null): Double {
        val lista = if (periodo == null) liquidaciones else liquidaciones.filter { it.periodo == periodo }
        return lista.sumOf { it.totalDescuentos }
    }
}