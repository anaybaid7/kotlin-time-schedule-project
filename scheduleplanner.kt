import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

data class Event(val date: LocalDate, val name: String, val attendees: MutableList<String> = mutableListOf())

fun main() {
    val scanner = Scanner(System.'in')
    val events = mutableListOf<Event>()

    println("Enter your email address:")
    val username = scanner.next()

    println("Enter your email password:")
    val password = scanner.next()

    val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", "smtp.gmail.com")
        put("mail.smtp.port", "587")
    }

    val session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            return PasswordAuthentication(username, password)
        }
    })

    while (true) {
        println("Choose an option:")
        println("1. Add an event")
        println("2. View events for a day")
        println("3. Add an attendee to an event")
        println("4. Send email invitations to event attendees")
        println("5. Exit")

        when (scanner.nextInt()) {
            1 -> {
                print("Enter event name: ")
                val name = scanner.next()

                print("Enter event date (yyyy-mm-dd): ")
                val dateStr = scanner.next()
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)

                events.add(Event(date, name))
                println("Event added.")
            }
            2 -> {
                print("Enter date to view (yyyy-mm-dd): ")
                val dateStr = scanner.next()
                val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)

                val eventsForDay = events.filter { it.date == date }
                if (eventsForDay.isEmpty()) {
                    println("No events scheduled for that day.")
                } else {
                    println("Events for $date:")
                    eventsForDay.forEach { event ->
                        println("- ${event.name}")
                        if (event.attendees.isNotEmpty()) {
                            println("  Attendees:")
                            event.attendees.forEach { println("  - $it") }
                        }
                    }
                }
            }
            3 -> {
                print("Enter event name: ")
                val name = scanner.next()

                val event = events.find { it.name == name }
                if (event == null) {
                    println("Event not found.")
                } else {
                    print("Enter attendee email address: ")
                    val attendee = scanner.next()

                    if (event.attendees.contains(attendee)) {
                        println("Attendee already added.")
                    } else {
                        event.attendees.add(attendee)
                        println("Attendee added to $name.")
                    }
                }
            }
            4 -> {
                print("Enter event name: ")
                val name = scanner.next()

                val event = events.find { it.name == name }
                if (event == null) {
                    println("Event not found.")
                } else {
                        val message = MimeMessage(session).apply {
                        setFrom(InternetAddress(username))
                        setSubject("You're invited to $name")
                        setText("You're invited to $name on ${event.date}.")
                        event.attendees.forEach { addRecipient(Message.RecipientType.TO, InternetAddress(it)) }
                    }

                    Transport.send(message)

                    println("Invitations sent.")
                }
            }
            5 -> {
                println("Exiting...")
                return
            }
            else -> {
                println("Invalid option.")
            }
        }
    }
}

