package view.temporal.event

import model.calendar.CalendarEvent
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import java.lang.Math.ceil
import kotlin.math.floor

class DayEventPanel(var width: Int, var height: Int, var uiComponents: ApplicationUIComponents,
                    val displayBox: Boolean = false, var position: Position) {

    var panel = Components.panel()
            .wrapWithBox(displayBox) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withSize(Sizes.create(width, height)) // the size must be smaller than the parent's size
            .withPosition(position)
            .build()

    var hourHeight = height/HOURS_SHOWN
    val eventWidth = width - HOUR_LABEL_WIDTH
    val hoursLabels: MutableList<Label> = ArrayList(HOURS_SHOWN)
    var calendarEventsPanels: MutableList<Panel> = ArrayList()
    var zeroHoursLabelPosition = Positions.create(0,0)
    var calendarEventsPositions: MutableList<Position> = createCalendarEventsPositions()

    private fun createCalendarEventsPositions(): MutableList<Position> {
        var currentPosition = Positions.create(HOUR_LABEL_WIDTH,zeroHoursLabelPosition.y)
        var eventsPositions: MutableList<Position> = ArrayList(HOURS_SHOWN*MAX_EVENTS_PER_HOUR)
        for( timePeriod in 0 until HOURS_SHOWN*MAX_EVENTS_PER_HOUR){
            currentPosition = Positions.create(HOUR_LABEL_WIDTH, timePeriod*(hourHeight/2))
            eventsPositions.add(currentPosition)
        }
        return eventsPositions
    }


    open fun update(events: List<CalendarEvent>){
        calendarEventsPanels.forEach { it -> panel.removeComponent(it)}
        var currentAllDayPosition = calendarEventsPositions[0]
        events.forEach { event ->
            var eventPosition = currentAllDayPosition
            var eventHeight = (hourHeight/2)
            if(!event.allDay){
                var durationInMillis = event.endDateTime.time - event.startDateTime.time
                var num_half_hours =
                        ceil(durationInMillis.toDouble()/
                                (MILLIS_IN_SECOND* SECONDS_IN_MINUTE* MINUTES_IN_HALF_HOUR)).toInt()
                var half_hour_index = (event.startDateTime.hours- FIRST_HOUR_SHOWN)*2+
                        floor(event.startDateTime.minutes.toDouble()/ MINUTES_IN_HALF_HOUR).toInt()
                eventHeight = num_half_hours*(hourHeight/2)
                eventPosition = calendarEventsPositions[half_hour_index]
            }

            var eventPanel = Components.panel()
                    .withSize(Sizes.create(eventWidth-10, eventHeight))
                    .withPosition(eventPosition)
                    .wrapWithBox(true)
                    .build()
            val eventLabel = Components.label().withText(event.description).build()
            eventPanel.addComponent(eventLabel)
            calendarEventsPanels.add(eventPanel)
            panel.addComponent(eventPanel)
        }
    }

    open fun build(){
        buildHourLabels()
    }

    fun buildHourLabels(){
        var currentLabelPosition = zeroHoursLabelPosition
        for( hour in FIRST_HOUR_SHOWN until LAST_HOUR_SHOWN){
            val hourLabel = Components.label()
                    .withText(String.format("%d:00",hour))
                    .withSize(Sizes.create(HOUR_LABEL_WIDTH,hourHeight))
                    .withPosition(currentLabelPosition)
                    .build()
            hourLabel?.let { hoursLabels.add(hourLabel)}
            hourLabel?.let { panel!!.addComponent(it) }
            currentLabelPosition = Positions.create(zeroHoursLabelPosition.x, currentLabelPosition.y+hourHeight)
        }
    }

    companion object {
        val HOUR_LABEL_WIDTH = 10
        val LAST_HOUR_SHOWN = 21
        val FIRST_HOUR_SHOWN = 5
        val HOURS_SHOWN = LAST_HOUR_SHOWN - FIRST_HOUR_SHOWN
        val MAX_EVENTS_PER_HOUR = 2
        val MILLIS_IN_SECOND = 1000L
        val SECONDS_IN_MINUTE = 60L
        val MINUTES_IN_HALF_HOUR = 30L
    }
}