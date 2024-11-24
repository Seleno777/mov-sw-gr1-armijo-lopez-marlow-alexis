import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.io.File

data class Event(
    var id: Int,
    var name: String,
    var date: LocalDate,
    var capacity: Int,
    var registrationFee: Double,
    var athletes: MutableList<Athlete> = mutableListOf()
)

data class Athlete(
    var id: Int,
    var name: String,
    var age: Int,
    var isRegistered: Boolean,
    var personalBest: Double
)

class EventAthleteManager {
    private val eventsFile = "events.txt"
    private val athletesFile = "athletes.txt"
    private val configFile = "config.txt"
    private var events = mutableListOf<Event>()
    private var lastEventId = 0
    private var lastAthleteId = 0
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        loadConfig()
        loadData()
    }

    private fun loadConfig() {
        try {
            if (File(configFile).exists()) {
                val config = File(configFile).readLines()
                lastEventId = config[0].toInt()
                lastAthleteId = config[1].toInt()
            }
        } catch (e: Exception) {
            println("Error al cargar configuración: ${e.message}")
            lastEventId = 0
            lastAthleteId = 0
        }
    }

    private fun saveConfig() {
        try {
            File(configFile).writeText("$lastEventId\n$lastAthleteId")
        } catch (e: Exception) {
            println("Error al guardar configuración: ${e.message}")
        }
    }

    private fun saveData() {
        try {
            // Guardar eventos
            File(eventsFile).bufferedWriter().use { writer ->
                events.forEach { event ->
                    writer.write("${event.id}|${event.name}|${event.date}|${event.capacity}|${event.registrationFee}\n")
                }
            }

            // Guardar atletas
            File(athletesFile).bufferedWriter().use { writer ->
                events.forEach { event ->
                    event.athletes.forEach { athlete ->
                        writer.write("${event.id}|${athlete.id}|${athlete.name}|${athlete.age}|${athlete.isRegistered}|${athlete.personalBest}\n")
                    }
                }
            }

            // Guardar configuración
            saveConfig()

            println("\nDatos guardados exitosamente")
        } catch (e: Exception) {
            println("\nError al guardar los datos: ${e.message}")
        }
    }

    private fun loadData() {
        events.clear()
        try {
            // Cargar eventos
            if (File(eventsFile).exists()) {
                File(eventsFile).useLines { lines ->
                    lines.forEach { line ->
                        val parts = line.split("|")
                        if (parts.size == 5) {
                            events.add(Event(
                                id = parts[0].toInt(),
                                name = parts[1],
                                date = LocalDate.parse(parts[2]),
                                capacity = parts[3].toInt(),
                                registrationFee = parts[4].toDouble()
                            ))
                        }
                    }
                }
            }

            // Cargar atletas
            if (File(athletesFile).exists()) {
                File(athletesFile).useLines { lines ->
                    lines.forEach { line ->
                        val parts = line.split("|")
                        if (parts.size == 6) {
                            val eventId = parts[0].toInt()
                            val event = events.find { it.id == eventId }
                            event?.athletes?.add(Athlete(
                                id = parts[1].toInt(),
                                name = parts[2],
                                age = parts[3].toInt(),
                                isRegistered = parts[4].toBoolean(),
                                personalBest = parts[5].toDouble()
                            ))
                        }
                    }
                }
            }

            println("Datos cargados exitosamente")
        } catch (e: Exception) {
            println("Error al cargar datos: ${e.message}")
            events.clear()
        }
    }

    private fun readDate(): LocalDate? {
        while (true) {
            try {
                print("Fecha (YYYY-MM-DD): ")
                val dateStr = readLine()!!
                return LocalDate.parse(dateStr, dateFormatter)
            } catch (e: DateTimeParseException) {
                println("Formato de fecha inválido. Use el formato YYYY-MM-DD")
                print("¿Desea intentar de nuevo? (si/no): ")
                if (readLine()?.lowercase() != "si") return null
            }
        }
    }

    private fun readPersonalBest(): Double? {
        while (true) {
            try {
                print("Mejor marca personal: ")
                val input = readLine()!!
                if (!input.matches(Regex("^\\d*\\.?\\d+$"))) {
                    throw NumberFormatException("Solo se permiten números")
                }
                return input.toDouble()
            } catch (e: NumberFormatException) {
                println("Error: Solo se permiten números.")
                print("¿Desea intentar de nuevo? (si/no): ")
                if (readLine()?.lowercase() != "si") return null
            }
        }
    }

    private fun readRegistrationStatus(): Boolean? {
        while (true) {
            print("¿Está registrado? (si/no): ")
            when (readLine()?.lowercase()) {
                "si" -> return true
                "no" -> return false
                else -> {
                    println("Por favor, ingrese 'si' o 'no'")
                    print("¿Desea intentar de nuevo? (si/no): ")
                    if (readLine()?.lowercase() != "si") return null
                }
            }
        }
    }

    private fun displayEventsList(): Int? {
        println("\nEventos disponibles:")
        if (events.isEmpty()) {
            println("No hay eventos registrados.")
            return null
        }
        events.forEach { println("${it.id}: ${it.name}") }
        print("\nSeleccione el ID del evento: ")
        return try {
            readLine()!!.toInt()
        } catch (e: NumberFormatException) {
            println("ID inválido")
            null
        }
    }

    private fun displayAthletesList(event: Event): Int? {
        println("\nAtletas en ${event.name}:")
        if (event.athletes.isEmpty()) {
            println("No hay atletas registrados en este evento.")
            return null
        }
        event.athletes.forEach { println("${it.id}: ${it.name}") }
        print("\nSeleccione el ID del atleta: ")
        return try {
            readLine()!!.toInt()
        } catch (e: NumberFormatException) {
            println("ID inválido")
            null
        }
    }

    fun createEvent(): Event? {
        println("\n=== Crear Nuevo Evento ===")
        print("Nombre del evento: ")
        val name = readLine()!!

        val date = readDate() ?: return null

        print("Capacidad máxima: ")
        val capacity = readLine()!!.toInt()
        print("Cuota de inscripción: ")
        val fee = readLine()!!.toDouble()

        val event = Event(++lastEventId, name, date, capacity, fee)
        events.add(event)
        saveData()
        println("\nEvento creado exitosamente!")
        return event
    }

    fun createAthlete() {
        println("\n=== Registrar Nuevo Atleta ===")
        val eventId = displayEventsList() ?: return

        val event = events.find { it.id == eventId }
        if (event == null) {
            println("Evento no encontrado!")
            return
        }

        print("Nombre del atleta: ")
        val name = readLine()!!
        print("Edad: ")
        val age = readLine()!!.toInt()

        val isRegistered = readRegistrationStatus() ?: return
        val personalBest = readPersonalBest() ?: return

        val athlete = Athlete(++lastAthleteId, name, age, isRegistered, personalBest)
        event.athletes.add(athlete)
        saveData()
        println("\nAtleta registrado exitosamente!")
    }

    fun listEvents() {
        println("\n=== Lista de Eventos ===")
        if (events.isEmpty()) {
            println("No hay eventos registrados.")
            return
        }
        events.forEach { event ->
            println("\nID: ${event.id}")
            println("Nombre: ${event.name}")
            println("Fecha: ${event.date.format(dateFormatter)}")
            println("Capacidad: ${event.capacity}")
            println("Cuota: $${event.registrationFee}")
            println("Atletas registrados: ${event.athletes.size}")
        }
    }

    fun listAthletes() {
        println("\n=== Lista de Atletas por Evento ===")
        val eventId = displayEventsList() ?: return

        val event = events.find { it.id == eventId }
        if (event == null) {
            println("Evento no encontrado!")
            return
        }

        println("\nAtletas en ${event.name}:")
        if (event.athletes.isEmpty()) {
            println("No hay atletas registrados en este evento.")
            return
        }

        event.athletes.forEach { athlete ->
            println("\nID: ${athlete.id}")
            println("Nombre: ${athlete.name}")
            println("Edad: ${athlete.age}")
            println("Registrado: ${if (athlete.isRegistered) "Si" else "No"}")
            println("Mejor marca(en Km): ${athlete.personalBest}")
        }
    }

    fun updateEvent() {
        println("\n=== Actualizar Evento ===")
        val eventId = displayEventsList() ?: return

        val event = events.find { it.id == eventId }
        if (event == null) {
            println("Evento no encontrado!")
            return
        }

        println("\nIngrese los nuevos datos (presione Enter para mantener el valor actual):")

        print("Nombre (${event.name}): ")
        val name = readLine()
        if (!name.isNullOrEmpty()) event.name = name

        print("Fecha (${event.date.format(dateFormatter)}): ")
        val dateStr = readLine()
        if (!dateStr.isNullOrEmpty()) {
            try {
                event.date = LocalDate.parse(dateStr, dateFormatter)
            } catch (e: DateTimeParseException) {
                println("Formato de fecha inválido. No se actualizará la fecha.")
            }
        }

        print("Capacidad (${event.capacity}): ")
        val capacity = readLine()
        if (!capacity.isNullOrEmpty()) event.capacity = capacity.toInt()

        print("Cuota (${event.registrationFee}): ")
        val fee = readLine()
        if (!fee.isNullOrEmpty()) event.registrationFee = fee.toDouble()

        saveData()
        println("\nEvento actualizado exitosamente!")
    }

    fun updateAthlete() {
        println("\n=== Actualizar Atleta ===")
        val eventId = displayEventsList() ?: return

        val event = events.find { it.id == eventId }
        if (event == null) {
            println("Evento no encontrado!")
            return
        }

        val athleteId = displayAthletesList(event) ?: return

        val athlete = event.athletes.find { it.id == athleteId }
        if (athlete == null) {
            println("Atleta no encontrado!")
            return
        }

        println("\nIngrese los nuevos datos (presione Enter para mantener el valor actual):")

        print("Nombre (${athlete.name}): ")
        val name = readLine()
        if (!name.isNullOrEmpty()) athlete.name = name

        print("Edad (${athlete.age}): ")
        val age = readLine()
        if (!age.isNullOrEmpty()) athlete.age = age.toInt()

        print("Registrado (${if (athlete.isRegistered) "si" else "no"}): ")
        val registered = readLine()
        if (!registered.isNullOrEmpty()) {
            when (registered.lowercase()) {
                "si" -> athlete.isRegistered = true
                "no" -> athlete.isRegistered = false
                else -> println("Valor inválido. No se actualizará el estado de registro.")
            }
        }

        print("Mejor marca (${athlete.personalBest}): ")
        val personalBest = readLine()
        if (!personalBest.isNullOrEmpty()) {
            try {
                if (!personalBest.matches(Regex("^\\d*\\.?\\d+$"))) {
                    println("Error: Solo se permiten números. No se actualizará la mejor marca.")
                } else {
                    athlete.personalBest = personalBest.toDouble()
                }
            } catch (e: NumberFormatException) {
                println("Error: Valor inválido. No se actualizará la mejor marca.")
            }
        }

        saveData()
        println("\nAtleta actualizado exitosamente!")
    }

    fun deleteEvent() {
        println("\n=== Eliminar Evento ===")
        val eventId = displayEventsList() ?: return

        val removed = events.removeIf { it.id == eventId }
        if (removed) {
            saveData()
            println("Evento eliminado exitosamente!")
        } else {
            println("Evento no encontrado!")
        }
    }

    fun deleteAthlete() {
        println("\n=== Eliminar Atleta ===")
        val eventId = displayEventsList() ?: return

        val event = events.find { it.id == eventId }
        if (event == null) {
            println("Evento no encontrado!")
            return
        }

        val athleteId = displayAthletesList(event) ?: return

        val removed = event.athletes.removeIf { it.id == athleteId }
        if (removed) {
            saveData()
            println("Atleta eliminado exitosamente!")
        } else {
            println("Atleta no encontrado!")
        }
    }
}

fun main() {
    val manager = EventAthleteManager()
    var option: Int

    do {
        println("\n=== SISTEMA DE GESTIÓN DE EVENTOS Y ATLETAS ===")
        println("1. Crear evento")
        println("2. Registrar atleta")
        println("3. Listar eventos")
        println("4. Listar atletas de un evento")
        println("5. Actualizar evento")
        println("6. Actualizar atleta")
        println("7. Eliminar evento")
        println("8. Eliminar atleta")
        println("0. Salir")
        print("\nSeleccione una opción: ")

        option = try {
            readLine()!!.toInt()
        } catch (e: Exception) {
            -1
        }

        when (option) {
            1 -> manager.createEvent()
            2 -> manager.createAthlete()
            3 -> manager.listEvents()
            4 -> manager.listAthletes()
            5 -> manager.updateEvent()
            6 -> manager.updateAthlete()
            7 -> manager.deleteEvent()
            8 -> manager.deleteAthlete()
            0 -> println("¡Hasta luego!")
            else -> println("Opción inválida!")
        }
    } while (option != 0)
}