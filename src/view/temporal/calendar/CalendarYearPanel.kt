package view.temporal.calendar

import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import java.time.LocalDate

class CalendarYearPanel (width: Int, height: Int, uiComponents: ApplicationUIComponents,
                         displayBox: Boolean = false, position: Position, showDayOfWeekLabel:Boolean = true,
                         selectedLocalDate:LocalDate = LocalDate.now()):
        BaseCalendarPanel(width, height, uiComponents, displayBox, position, showDayOfWeekLabel){

    var currentYear = selectedLocalDate.year

    var calendarMonthWidth = (width/4)
    var calendarMonthHeight = ((height-3)/3)
    var currentYearLocalStartDate = selectedLocalDate.minusDays((selectedLocalDate.dayOfYear-1).toLong())

    var yearLabel: Label? = null
    var januaryMonthPanel:CalendarMonthPanel? = null
    var februaryMonthPanel:CalendarMonthPanel? = null
    var marchMonthPanel:CalendarMonthPanel? = null
    var aprilMonthPanel:CalendarMonthPanel? = null
    var mayMonthPanel:CalendarMonthPanel? = null
    var juneMonthPanel:CalendarMonthPanel? = null
    var julyMonthPanel:CalendarMonthPanel? = null
    var augustMonthPanel:CalendarMonthPanel? = null
    var septemberMonthPanel:CalendarMonthPanel? = null
    var octoberMonthPanel:CalendarMonthPanel? = null
    var novemberMonthPanel:CalendarMonthPanel? = null
    var decemberMonthPanel:CalendarMonthPanel? = null

    fun update(selectedDate: LocalDate = LocalDate.now()){
        if(!selectedDate.year.equals(selectedLocalDate.year)) {
            panel!!.children.forEach { panel!!.removeComponent(it) }
            if(!selectedDate.year.equals(currentYear)) {
                currentYear = selectedLocalDate.year
                yearLabel = Components.label()
                        .withText(currentYear.toString())
                        .withPosition(Positions.create(0,0))
                        .build()
            }
            buildPanelComponents()
        }
        selectedLocalDate = LocalDate.of(selectedDate.year, selectedDate.month, selectedDate.dayOfMonth)
    }

    override fun build(){
        super.build()
        yearLabel = Components.label()
                .withText(currentYear.toString())
                .withPosition(Positions.create(0,0))
                .build()
        buildPanelComponents()
    }

    fun buildPanelComponents() {
        panel!!.children.forEach { panel!!.removeComponent(it) }
        panel!!.addComponent(yearLabel!!)

        //First Row of Months
        var januaryPosition = Positions.create(0,0).relativeToBottomOf(yearLabel!!)
        januaryMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                januaryPosition)
        januaryMonthPanel!!.build()
        januaryMonthPanel!!.update(currentYearLocalStartDate)
        januaryMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        var februaryPosition = Positions.create(0,0).relativeToRightOf(januaryMonthPanel!!.panel!!)
        februaryMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                februaryPosition, true)
        februaryMonthPanel!!.build()
        februaryMonthPanel!!.update(currentYearLocalStartDate.plusMonths(1L))
        februaryMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        var marchPosition = Positions.create(0,0).relativeToRightOf(februaryMonthPanel!!.panel!!)
        marchMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                marchPosition, true)
        marchMonthPanel!!.build()
        marchMonthPanel!!.update(currentYearLocalStartDate.plusMonths(2L))
        marchMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        var aprilPosition = Positions.create(0,0).relativeToRightOf(marchMonthPanel!!.panel!!)
        aprilMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                aprilPosition, true)
        aprilMonthPanel!!.build()
        aprilMonthPanel!!.update(currentYearLocalStartDate.plusMonths(3L))
        aprilMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        //Second Row of Months
        var mayPosition = Positions.create(0,0).relativeToBottomOf(januaryMonthPanel!!.panel!!)
        mayMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                mayPosition, true)
        mayMonthPanel!!.build()
        mayMonthPanel!!.update(currentYearLocalStartDate.plusMonths(4L))
        mayMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        var junePosition = Positions.create(0,0).relativeToRightOf(mayMonthPanel!!.panel!!)
        juneMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                junePosition, true)
        juneMonthPanel!!.build()
        juneMonthPanel!!.update(currentYearLocalStartDate.plusMonths(5L))
        juneMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        var julyPosition = Positions.create(0,0).relativeToRightOf(juneMonthPanel!!.panel!!)
        julyMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                julyPosition, true)
        julyMonthPanel!!.build()
        julyMonthPanel!!.update(currentYearLocalStartDate.plusMonths(6L))
        julyMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        var augustPosition = Positions.create(0,0).relativeToRightOf(julyMonthPanel!!.panel!!)
        augustMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                augustPosition, true)
        augustMonthPanel!!.build()
        augustMonthPanel!!.update(currentYearLocalStartDate.plusMonths(7L))
        augustMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        //Third Row of Months
        var septemberPosition = Positions.create(0,0).relativeToBottomOf(mayMonthPanel!!.panel!!)
        septemberMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                septemberPosition, true)
        septemberMonthPanel!!.build()
        septemberMonthPanel!!.update(currentYearLocalStartDate.plusMonths(4L))
        septemberMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        var octoberPosition = Positions.create(0,0).relativeToRightOf(septemberMonthPanel!!.panel!!)
        octoberMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                octoberPosition, true)
        octoberMonthPanel!!.build()
        octoberMonthPanel!!.update(currentYearLocalStartDate.plusMonths(5L))
        octoberMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        var novemberPosition = Positions.create(0,0).relativeToRightOf(octoberMonthPanel!!.panel!!)
        novemberMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                novemberPosition, true)
        novemberMonthPanel!!.build()
        novemberMonthPanel!!.update(currentYearLocalStartDate.plusMonths(6L))
        novemberMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        var decemberPosition = Positions.create(0,0).relativeToRightOf(novemberMonthPanel!!.panel!!)
        decemberMonthPanel = CalendarMonthPanel(calendarMonthWidth, calendarMonthHeight, uiComponents, false,
                decemberPosition, true)
        decemberMonthPanel!!.build()
        decemberMonthPanel!!.update(currentYearLocalStartDate.plusMonths(7L))
        decemberMonthPanel!!.panel?.let { panel!!.addComponent(it) }
    }
}