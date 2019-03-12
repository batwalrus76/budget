package view.temporal.calendar

import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.data.Position
import view.screens.BaseScreen
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarWeekPanel(width: Int, height: Int, uiComponents: ApplicationUIComponents,
                            position: Position = Positions.create(0,0), showDayOfWeekLabel:Boolean = true,
                            selectedLocalDate:LocalDate = LocalDate.now(), displayBox: Boolean = false, baseScreen: BaseScreen):
        BaseCalendarPanel(width, height, uiComponents, displayBox, position, showDayOfWeekLabel, selectedLocalDate,
                baseScreen){

    private var seventhWidth = (width/6)
    private var currentWeekLocalStartDate = determineCurrentWeekLocalStartDate(selectedLocalDate)

    private var fridayDayPosition = Positions.create(0,0)
    private var fridayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            fridayDayPosition, showDayOfWeekLabel, currentWeekLocalStartDate, baseScreen = baseScreen)
    private var saturdaySundayDayPosition = Positions.create(0,0).relativeToRightOf(fridayDayPanel.panel)
    private var saturdaySundayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents,
            true, saturdaySundayDayPosition, showDayOfWeekLabel,
            currentWeekLocalStartDate.plusDays(1L), "Sat/Sun", baseScreen)
    private var mondayDayPosition =Positions.create(0,0).relativeToRightOf(saturdaySundayDayPanel.panel)
    private var mondayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            mondayDayPosition, showDayOfWeekLabel,
            currentWeekLocalStartDate.plusDays(3L), baseScreen = baseScreen)
    private var tuesdayDayPosition = Positions.create(0,0).relativeToRightOf(mondayDayPanel.panel)
    private var tuesdayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            tuesdayDayPosition, showDayOfWeekLabel,
            currentWeekLocalStartDate.plusDays(4L), baseScreen = baseScreen)
    private var wednesdayDayPosition = Positions.create(0,0).relativeToRightOf(tuesdayDayPanel.panel)
    private var wednesdayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            wednesdayDayPosition, showDayOfWeekLabel,
            currentWeekLocalStartDate.plusDays(5L), baseScreen = baseScreen)
    private var thursdayDayPosition = Positions.create(0,0).relativeToRightOf(wednesdayDayPanel.panel)
    private var thursdayDayPanel = CalendarDayPanel(seventhWidth, height, uiComponents, true,
            thursdayDayPosition, showDayOfWeekLabel,
            currentWeekLocalStartDate.plusDays(6L), baseScreen = baseScreen)

    override fun build() {
        if(panel.children.contains(fridayDayPanel.panel)){
            panel.removeComponent(fridayDayPanel.panel)
        }
        if(panel.children.contains(saturdaySundayDayPanel.panel)){
            panel.removeComponent(saturdaySundayDayPanel.panel)
        }
        if(panel.children.contains(mondayDayPanel.panel)){
            panel.removeComponent(mondayDayPanel.panel)
        }
        if(panel.children.contains(tuesdayDayPanel.panel)){
            panel.removeComponent(tuesdayDayPanel.panel)
        }
        if(panel.children.contains(wednesdayDayPanel.panel)){
            panel.removeComponent(wednesdayDayPanel.panel)
        }
        if(panel.children.contains(thursdayDayPanel.panel)){
            panel.removeComponent(thursdayDayPanel.panel)
        }

        fridayDayPanel.build()
        saturdaySundayDayPanel.build()
        mondayDayPanel.build()
        tuesdayDayPanel.build()
        wednesdayDayPanel.build()
        thursdayDayPanel.build()


        panel.addComponent(fridayDayPanel.panel)
        panel.addComponent(saturdaySundayDayPanel.panel)
        panel.addComponent(mondayDayPanel.panel)
        panel.addComponent(tuesdayDayPanel.panel)
        panel.addComponent(wednesdayDayPanel.panel)
        panel.addComponent(thursdayDayPanel.panel)

        super.build()
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
        dayBudgetAnalysisStates.let {
            saturdaySundayDayPanel.update(currentWeekLocalStartDate.plusDays(1L), it)
        }
        dayBudgetAnalysisStates = uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(3L))
        dayBudgetAnalysisStates?.let {
            mondayDayPanel.update(currentWeekLocalStartDate.plusDays(3L), it)
        }
        dayBudgetAnalysisStates = uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(4L))
        dayBudgetAnalysisStates?.let {
            tuesdayDayPanel.update(currentWeekLocalStartDate.plusDays(4L), it)
        }
        dayBudgetAnalysisStates = uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(5L))
        dayBudgetAnalysisStates?.let {
            wednesdayDayPanel.update(currentWeekLocalStartDate.plusDays(5L), it)
        }
        dayBudgetAnalysisStates = uiComponents.findBudgetAnalysisStateForLocalDate(currentWeekLocalStartDate.plusDays(6L))
        dayBudgetAnalysisStates?.let {
            thursdayDayPanel.update(currentWeekLocalStartDate.plusDays(6L), it)
        }
    }

    companion object {
        fun determineCurrentWeekLocalStartDate(selectedLocalDate: LocalDate):LocalDate {
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
                DayOfWeek.FRIDAY, null -> {
                    currentWeekLocalStartDate = selectedLocalDate
                }
            }
            return currentWeekLocalStartDate
        }

    }

}