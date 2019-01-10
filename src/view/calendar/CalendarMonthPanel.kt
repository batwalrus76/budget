package view.calendar

import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import java.time.LocalDate

class CalendarMonthPanel(var width: Int, var height: Int, var uiComponents: ApplicationUIComponents,
                         var showDayOfWeekLabel:Boolean = true, var position: Position = Positions.create(0,0)) {

    var panel: Panel? = Components.panel()
                            .wrapWithBox(false)
                            .wrapWithShadow(false)
                            .withSize(Sizes.create(this.width, this.height))
                            .withPosition(position)
                            .build()

    var selectedLocalDate = LocalDate.now()
    var currentMonth = selectedLocalDate.month

    var monthLabel: Label? = null

    var calendarHeight = (height/4)
    var currentMonthLocalStartDate = selectedLocalDate.minusDays((selectedLocalDate.dayOfMonth-1).toLong())

    var firstWeekOfMonthStartDate:LocalDate =
            CalendarWeekPanel.determineCurrentWeekLocalStartDate(currentMonthLocalStartDate)
    var firstWeekPanel:CalendarWeekPanel? = null

    var secondWeekOfMonthStartDate:LocalDate? = null
    var secondWeekPanel:CalendarWeekPanel? = null

    var thirdWeekOfMonthStartDate:LocalDate? = null
    var thirdWeekPanel:CalendarWeekPanel? = null

    var fourthWeekOfMonthStartDate:LocalDate? = null
    var fourthWeekPanel:CalendarWeekPanel? = null

    var fifthWeekOfMonthStartDate:LocalDate? = null
    var fifthWeekPanel:CalendarWeekPanel? = null

    fun update(selectedDate: LocalDate = LocalDate.now()){
        if(!selectedDate.equals(selectedLocalDate)) {
            panel!!.children.forEach { panel!!.removeComponent(it) }
            selectedLocalDate = selectedDate
            currentMonth = selectedLocalDate.month
            monthLabel = Components.label()
                    .withText(currentMonth.toString())
                    .withPosition(Positions.create(0,0))
                    .build()
            buildPanelComponents()
        }
    }

    fun build():CalendarMonthPanel{
        monthLabel = Components.label()
                .withText(currentMonth.toString())
                .withPosition(Positions.create(0,0))
                .build()
        buildPanelComponents()
        return this
    }

    fun buildPanelComponents(){
        panel!!.addComponent(monthLabel!!)
        firstWeekOfMonthStartDate = CalendarWeekPanel.determineCurrentWeekLocalStartDate(currentMonthLocalStartDate)
        secondWeekOfMonthStartDate = firstWeekOfMonthStartDate!!.plusDays(7L)
        thirdWeekOfMonthStartDate = secondWeekOfMonthStartDate!!.plusDays(7L)
        fourthWeekOfMonthStartDate = thirdWeekOfMonthStartDate!!.plusDays(7L)
        fifthWeekOfMonthStartDate = fourthWeekOfMonthStartDate!!.plusDays(7L)
        if(fifthWeekOfMonthStartDate!!.month.equals(currentMonth)){
            calendarHeight = height/5
        }
        var firstWeekPosition = Positions.create(0,0).relativeToBottomOf(monthLabel!!)
        firstWeekPanel =
                CalendarWeekPanel(width, calendarHeight, uiComponents, firstWeekPosition, showDayOfWeekLabel,
                        firstWeekOfMonthStartDate).build()
        firstWeekPanel!!.update(firstWeekOfMonthStartDate)
        firstWeekPanel!!.panel?.let { panel!!.addComponent(it) }

        var secondWeekPosition = Positions.create(0,0).relativeToBottomOf(firstWeekPanel!!.panel!!)
        secondWeekPanel =
                CalendarWeekPanel(width, calendarHeight, uiComponents, secondWeekPosition, showDayOfWeekLabel,
                        secondWeekOfMonthStartDate!!).build()
        secondWeekPanel!!.update(secondWeekOfMonthStartDate!!)
        secondWeekPanel!!.panel?.let { panel!!.addComponent(it) }

        var thirdWeekPosition = Positions.create(0,0).relativeToBottomOf(secondWeekPanel!!.panel!!)
        thirdWeekPanel =
                CalendarWeekPanel(width, calendarHeight, uiComponents, thirdWeekPosition, showDayOfWeekLabel,
                        thirdWeekOfMonthStartDate!!).build()
        thirdWeekPanel!!.update(thirdWeekOfMonthStartDate!!)
        thirdWeekPanel!!.panel?.let { panel!!.addComponent(it) }

        var fourthWeekPosition = Positions.create(0,0).relativeToBottomOf(thirdWeekPanel!!.panel!!)
        fourthWeekPanel =
                CalendarWeekPanel(width, calendarHeight, uiComponents, fourthWeekPosition, showDayOfWeekLabel,
                        fourthWeekOfMonthStartDate!!).build()
        fourthWeekPanel!!.update(fourthWeekOfMonthStartDate!!)
        fourthWeekPanel!!.panel?.let { panel!!.addComponent(it) }

        if(fifthWeekOfMonthStartDate!!.month.equals(currentMonth)){
            var fifthWeekPosition = Positions.create(0,0).relativeToBottomOf(fourthWeekPanel!!.panel!!)
            fifthWeekPanel =
                    CalendarWeekPanel(width, calendarHeight, uiComponents, fifthWeekPosition, showDayOfWeekLabel,
                            fifthWeekOfMonthStartDate!!).build()
            fifthWeekPanel!!.update(fifthWeekOfMonthStartDate!!)
            fifthWeekPanel!!.panel?.let { panel!!.addComponent(it) }
        }
    }
}