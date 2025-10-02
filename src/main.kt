fun main() {
    val repo = Repositorio()
    seedAfps(repo)

    var op = -1
    while (op != 0) {
        println("""
            1) Listar empleados
            2) Agregar empleado
            3) Generar liquidación por RUT
            4) Listar liquidaciones
            5) Filtrar por AFP (ordenar por líquido desc)
            6) Eliminar empleado
            7) Prueba completa
            0) Salir
        """.trimIndent())
        op = leerInt("Opción: ")

        when (op) {
            1 -> listarEmpleados(repo)
            2 -> agregarEmpleado(repo)
            3 -> generarLiquidacion(repo)
            4 -> listarLiquidaciones(repo)
            5 -> filtrarPorAfp(repo)
            6 -> eliminarEmpleado(repo)
            7 -> pruebaCompleta(repo)
            0 -> println("Saliendo...")
            else -> println("Opción inválida")
        }
        println()
    }
}

fun leerTxt(p: String): String { while (true) { print(p); val s = readLine()?.trim(); if (!s.isNullOrEmpty()) return s; println("No vacío") } }
fun leerInt(p: String): Int { while (true) { print(p); val n = readLine()?.trim()?.toIntOrNull(); if (n != null) return n; println("Entero válido") } }
fun leerDbl(p: String): Double { while (true) { print(p); val n = readLine()?.trim()?.replace(",", ".")?.toDoubleOrNull(); if (n != null) return n; println("Número válido") } }

fun seedAfps(repo: Repositorio) {
    if (repo.afps.isEmpty()) {
        repo.afps.add(AFP("Cuprum", 0.114))
        repo.afps.add(AFP("Habitat", 0.112))
        repo.afps.add(AFP("Modelo", 0.108))
        repo.afps.add(AFP("Provida", 0.115))
    }
}

fun listarEmpleados(repo: Repositorio) {
    if (repo.empleados.isEmpty()) { println("No hay empleados"); return }
    for (e in repo.empleados) println("${e.nombre} (${e.rut}) | AFP ${e.afp.nombre} | Sueldo base: ${e.sueldoBase}")
}

fun agregarEmpleado(repo: Repositorio) {
    val rut = leerTxt("RUT: ")
    val nombre = leerTxt("Nombre: ")
    println("AFP disponibles:"); for (a in repo.afps) println("- ${a.nombre}")
    val nombreAfp = leerTxt("AFP: ")
    var afp = repo.afps.find { it.nombre.equals(nombreAfp, true) }
    if (afp == null) { println("No existe, se crea con 11%"); afp = AFP(nombreAfp, 0.11); repo.afps.add(afp) }

    val calle = leerTxt("Calle: ")
    val numero = leerInt("Número: ")
    val comuna = leerTxt("Comuna: ")
    val ciudad = leerTxt("Ciudad: ")
    val region = leerTxt("Región: ")

    val sueldoBase = leerDbl("Sueldo base: ")
    val horasExtras = leerInt("Horas extras (0 si no): ")
    val valorHoraExtra = if (horasExtras > 0) leerDbl("Valor hora extra: ") else 0.0

    val dir = Direccion(calle, numero, comuna, ciudad, region)
    val emp = Empleado(rut, nombre, afp, dir, sueldoBase, horasExtras, valorHoraExtra)
    repo.agregarEmpleado(emp)
    println("Empleado agregado")
}

fun generarLiquidacion(repo: Repositorio) {
    val rut = leerTxt("RUT: ")
    val periodo = leerTxt("Periodo (YYYY-MM): ")
    val liq = repo.generarLiquidacion(rut, periodo)
    if (liq == null) println("No existe el empleado") else println(liq.resumen())
}

fun listarLiquidaciones(repo: Repositorio) {
    if (repo.liquidaciones.isEmpty()) { println("No hay liquidaciones"); return }
    for (l in repo.liquidaciones) { println(l.resumen()); println("-".repeat(40)) }
}

fun filtrarPorAfp(repo: Repositorio) {
    val n = leerTxt("Nombre AFP: ")
    val p = leerTxt("Periodo (YYYY-MM): ")
    val lista = repo.empleados.filter { it.afp.nombre.equals(n, true) }
    if (lista.isEmpty()) { println("Sin empleados en esa AFP"); return }
    val liqs = lista.map { LiquidacionSueldo.desdeEmpleado(p, it) }.sortedByDescending { it.sueldoLiquido }
    for (x in liqs) println("${x.empleado.nombre} | Líquido: ${x.sueldoLiquido}")
}

fun eliminarEmpleado(repo: Repositorio) {
    val rut = leerTxt("RUT a eliminar: ")
    val ok = repo.eliminarEmpleadoPorRUT(rut)
    if (ok) println("Eliminado") else println("No existe")
}

fun pruebaCompleta(repo: Repositorio) {
    repo.empleados.clear(); repo.liquidaciones.clear()
    val a1 = repo.afps.find { it.nombre == "Habitat" } ?: AFP("Habitat", 0.112).also { repo.afps.add(it) }
    val a2 = repo.afps.find { it.nombre == "Cuprum" }  ?: AFP("Cuprum", 0.114).also { repo.afps.add(it) }
    val e1 = Empleado("12.345.678-9","Pedro",a1,Direccion("A",1,"Talca","Talca","Maule"),900000.0,10,6000.0)
    val e2 = Empleado("9.876.543-2","Juan",a2,Direccion("B",2,"Curicó","Curicó","Maule"),1200000.0)
    val e3 = Empleado("7.654.321-1","Diego",a1,Direccion("C",3,"Talca","Talca","Maule"),750000.0,5,7000.0)
    repo.agregarEmpleado(e1); repo.agregarEmpleado(e2); repo.agregarEmpleado(e3)
    val periodo = "2025-09"
    for (emp in repo.empleados) { println(repo.generarLiquidacion(emp.rut, periodo)?.resumen()); println("-".repeat(40)) }
    var total = 0.0; for (l in repo.liquidaciones) total += l.totalDescuentos
    println("TOTAL DESCUENTOS NÓMINA $periodo: $total")
}

