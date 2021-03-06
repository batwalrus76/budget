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
                        var position: Position = Positions.create(0,0), var showDayOfWeekLabel:Boolean = true,
                        var selectedLocalDate:LocalDate = LocalDate.now()) {

    var panel: Panel? = null

    var seventhWidth = (width/6)
    var currentWeekLocalStartDate = determineCurrentWeekLocalStartDate(selectedLocalDate)

    var fridayDayPosition = Positions.create(0,0)
    var fridayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            fridayDayPosition, showDayOfWeekLabel, currentWeekLocalStartDate).build()
    var saturdaySundayDayPosition = Positions.create(0,0).relativeToRightOf(fridayDayPanel.panel!!)
    var saturdaySundayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents,
                true, saturdaySundayDayPosition, showDayOfWeekLabel,
            currentWeekLocalStartDate.plusDays(1L),"Sat/Sun").build()
    var mondayDayPosition =Positions.create(0,0).relativeToRightOf(saturdaySundayDayPanel.panel!!)
    var mondayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            mondayDayPosition, showDayOfWeekLabel,
            currentWeekLocalStartDate.plusDays(3L)).build()
    var tuesdayDayPosition = Positions.create(0,0).relativeToRightOf(mondayDayPanel.panel!!)
    var tuesdayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            tuesdayDayPosition, showDayOfWeekLabel,
            currentWeekLocalStartDate.plusDays(4L)).build()
    var wednesdayDayPosition = Positions.create(0,0).relativeToRightOf(tuesdayDayPanel.panel!!)
    var wednesdayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            wednesdayDayPosition, showDayOfWeekLabel,
            currentWeekLocalStartDate.plusDays(5L)).build()
    var thursdayDayPosition = Positions.create(0,0).relativeToRightOf(wednesdayDayPanel.panel!!)
    var thursdayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            thursdayDayPosition, showDayOfWeekLabel,
            currentWeekLocalStartDate.plusDays(6L)).build()

    fun build() {
        panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(this.width, this.height))
                .withPosition(position).build()
    }

    fun update(selectedDate: LocalDate){
        build()
        selectedLocalDate = LocalDate.of(selectedDate.year, selectedDate.month, selectedDate.dayOfMonth)
        currentWeekLocalStartDate = determineCurrentWeekLocalStartDate(selectedLocalDate)
        var dayBudgetAnalysisStates = uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate)
        dayBudgetAnalysisStates?.let {
            fridayDayPanel.update(currentWeekLocalStartDate, it)
        }
        dayBudgetAnalysisStates =
                uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(1L))
        dayBudgetAnalysisStates!!.addAll(uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(2L))!!)
        dayBudgetAnalysisStates?.let {
            saturdaySundayDayPanel?.update(currentWeekLocalStartDate.plusDays(1L), it)
        }
        dayBudgetAnalysisStates = uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(3L))
        dayBudgetAnalysisStates?.let {
            mondayDayPanel?.update(currentWeekLocalStartDate.plusDays(3L), it)
        }
        dayBudgetAnalysisStates = uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(4L))
        dayBudgetAnalysisStates?.let {
            tuesdayDayPanel?.update(currentWeekLocalStartDate.plusDays(4L), it)
        }
        dayBudgetAnalysisStates = uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(5L))
        dayBudgetAnalysisStates?.let {
            wednesdayDayPanel?.update(currentWeekLocalStartDate.plusDays(5L), it)
        }
        dayBudgetAnalysisStates = uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(6L))
        dayBudgetAnalysisStates?.let {
            thursdayDayPanel?.update(currentWeekLocalStartDate.plusDays(6L), it)
        }
        panel?.addComponent(fridayDayPanel.panel!!)
        panel?.addComponent(saturdaySundayDayPanel.panel!!)
        panel?.addComponent(mondayDayPanel.panel!!)
        panel?.addComponent(tuesdayDayPanel.panel!!)
        panel?.addComponent(wednesdayDayPanel.panel!!)
        panel?.addComponent(thursdayDayPanel.panel!!)
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