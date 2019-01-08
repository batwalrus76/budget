package view.calendar

import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarWeekPanel(var width: Int, var height: Int, var uiComponents: ApplicationUIComponents,
                        var position: Position = Positions.create(0,0),
                        var selectedLocalDate:LocalDate = LocalDate.now()) {

    var panel: Panel? = Components.panel()
                            .wrapWithBox(false)
                            .wrapWithShadow(false)
                            .withSize(Sizes.create(this.width, this.height))
                            .withPosition(position).build()

    var seventhWidth = (width/6)
    var currentWeekLocalStartDate = determineCurrentWeekLocalStartDate(selectedLocalDate)

    var fridayDayPosition = Positions.create(0,0)
    var fridayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            fridayDayPosition, true, currentWeekLocalStartDate).build()
    var saturdayDayPosition = Positions.create(0,0).relativeToRightOf(fridayDayPanel.panel!!)
    var saturdayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents,
                true, saturdayDayPosition, true, currentWeekLocalStartDate.plusDays(1L)).build()
    var mondayDayPosition =Positions.create(0,0).relativeToRightOf(saturdayDayPanel.panel!!)
    var mondayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            mondayDayPosition, true, currentWeekLocalStartDate.plusDays(3L)).build()
    var tuesdayDayPosition = Positions.create(0,0).relativeToRightOf(mondayDayPanel.panel!!)
    var tuesdayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            tuesdayDayPosition, true, currentWeekLocalStartDate.plusDays(4L)).build()
    var wednesdayDayPosition = Positions.create(0,0).relativeToRightOf(tuesdayDayPanel.panel!!)
    var wednesdayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            wednesdayDayPosition, true, currentWeekLocalStartDate.plusDays(5L)).build()
    var thursdayDayPosition = Positions.create(0,0).relativeToRightOf(wednesdayDayPanel.panel!!)
    var thursdayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            thursdayDayPosition, true, currentWeekLocalStartDate.plusDays(6L)).build()

    fun build():CalendarWeekPanel {
        panel?.addComponent(fridayDayPanel.panel!!)
        panel?.addComponent(saturdayDayPanel.panel!!)
        panel?.addComponent(mondayDayPanel.panel!!)
        panel?.addComponent(tuesdayDayPanel.panel!!)
        panel?.addComponent(wednesdayDayPanel.panel!!)
        panel?.addComponent(thursdayDayPanel.panel!!)
        return this
    }

    fun update(selectedDate: LocalDate){
        selectedLocalDate = selectedDate
        currentWeekLocalStartDate = determineCurrentWeekLocalStartDate(selectedLocalDate)
        uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate)?.let {
            fridayDayPanel.update(currentWeekLocalStartDate, it)
        }
        uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(1L))?.let {
            saturdayDayPanel?.update(currentWeekLocalStartDate.plusDays(1L), it)
        }
        uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(3L))?.let {
            mondayDayPanel?.update(currentWeekLocalStartDate.plusDays(3L), it)
        }
        uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(4L))?.let {
            tuesdayDayPanel?.update(currentWeekLocalStartDate.plusDays(4L), it)
        }
        uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(5L))?.let {
            wednesdayDayPanel?.update(currentWeekLocalStartDate.plusDays(5L), it)
        }
        uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(6L))?.let {
            thursdayDayPanel?.update(currentWeekLocalStartDate.plusDays(6L), it)
        }
    }

    companion object {
        open fun determineCurrentWeekLocalStartDate(selectedLocalDate: LocalDate):LocalDate {
            var currentWeekLocalStartDate = selectedLocalDate
            when(selectedLocalDate.dayOfWeek){ //If it is Friday don't worry about changing date
                DayOfWeek.SATURDAY -> {
                    currentWeekLocalStartDate = selectedLocalDate.minusDays(1L)
                }
                DayOfWeek.SUNDAY -> {
                    currentWeekLocalStartDate = selectedLocalDate.minusDays(2L)
                }
                DayOfWeek.MONDAY -> {
                    currentWeekLocalStartDate = selectedLocalDate.minusDays(3L)
                }
                DayOfWeek.TUESDAY -> {
                    currentWeekLocalStartDate = selectedLocalDate.minusDays(4L)
                }
                DayOfWeek.WEDNESDAY -> {
                    currentWeekLocalStartDate = selectedLocalDate.minusDays(5L)
                }
                DayOfWeek.THURSDAY -> {
                    currentWeekLocalStartDate = selectedLocalDate.minusDays(6L)
                }
            }
            return currentWeekLocalStartDate
        }
    }

}