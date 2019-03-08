package model.calendar

import net.fortuna.ical4j.model.Date
import java.time.LocalDateTime

class CalendarEvent(var startDateTime: Date, var endDateTime: Date, var allDay: Boolean,
                    var description: String) {
}