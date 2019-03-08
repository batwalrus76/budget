package control.calendar

import model.calendar.CalendarEvent
import net.fortuna.ical4j.data.CalendarBuilder
import net.fortuna.ical4j.filter.Filter
import java.io.FileInputStream
import net.fortuna.ical4j.filter.PeriodRule
import net.fortuna.ical4j.model.*
import net.fortuna.ical4j.model.Period
import net.fortuna.ical4j.model.property.DateProperty
import java.time.*


class CalendarParser {

    companion object {

        val SECONDS_IN_MINUTE = 60L
        val MINUTES_IN_HOUR = 60L
        val HOURS_IN_DAY = 24L
        val START_TO_END_DAY_SECONDS_OFFSET = HOURS_IN_DAY * (MINUTES_IN_HOUR-1) * SECONDS_IN_MINUTE
        val DT_START_KEY = "DTSTART"
        val DT_END_KEY = "DTEND"
        val SUMMARY_KEY = "SUMMARY"

        open fun parseCalendarFile(calendarFileLocation: String): Calendar{
            val fin = FileInputStream(calendarFileLocation)
            val builder = CalendarBuilder()
            val calendar = builder.build(fin)
            return calendar
        }

        open fun retrieveDateCalendarEvents(date: LocalDate, calendar: Calendar): List<CalendarEvent> {
            var events:MutableList<CalendarEvent> = ArrayList()
            val instantStart = date.atStartOfDay().toInstant(ZoneOffset.ofHours(-5))
            val instantEnd = instantStart.plusSeconds(START_TO_END_DAY_SECONDS_OFFSET)
            val dateStart = DateTime(instantStart.toEpochMilli())
            val dateEnd = DateTime(instantEnd.toEpochMilli())
            val period = Period(dateStart,dateEnd)
            val filter = Filter<Component>(PeriodRule(period))

            val dateEvents = filter.filter(calendar.getComponents() as Collection<Component>?)
            dateEvents.forEach { dateEvent ->
                val dateTimeStart = dateEvent.getProperty<DateProperty>(DT_START_KEY).date
                val dateTimeEnd = dateEvent.getProperty<DateProperty>(DT_END_KEY).date
                val description = dateEvent.getProperty<Property>(SUMMARY_KEY).value
                val allDay: Boolean = description.contains("all-day")
                val event: CalendarEvent = CalendarEvent(dateTimeStart, dateTimeEnd, allDay, description)
                events.add(event)
            }
            return events
        }

    }
}