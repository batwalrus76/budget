package view.calendar

import model.enums.View
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.kotlin.onMouseReleased
import java.time.LocalDate
import java.util.Locale
import java.time.temporal.WeekFields



class CalendarMonthPanel(var width: Int, var height: Int, var uiComponents: ApplicationUIComponents,
                         var showDayOfWeekLabel:Boolean = true, var wrapWithBox: Boolean = true,
                         var position: Position = Positions.create(0,0)) {

    var panel: Panel? = Components.panel()
                            .wrapWithBox(wrapWithBox)
                            .wrapWithShadow(false)
                            .withSize(Sizes.create(this.width, this.height))
                            .withPosition(position)
                            .build()

    var selectedLocalDate = LocalDate.now()
    var currentMonth = selectedLocalDate.month

    var monthButton: Button? = null

    var calendarHeight = (height/4)-2
    var currentMonthLocalStartDate = selectedLocalDate.minusDays((selectedLocalDate.dayOfMonth-1).toLong())


    var firstWeekOfMonthStartDate:LocalDate =
            CalendarWeekPanel.determineCurrentWeekLocalStartDate(currentMonthLocalStartDate)
    var firstWeekofMonthButton: Button? = null
    var firstWeekPanel:CalendarWeekPanel? = null

    var secondWeekOfMonthStartDate:LocalDate? = null
    var secondWeekofMonthButton: Button? = null
    var secondWeekPanel:CalendarWeekPanel? = null

    var thirdWeekOfMonthStartDate:LocalDate? = null
    var thirdWeekofMonthButton: Button? = null
    var thirdWeekPanel:CalendarWeekPanel? = null

    var fourthWeekOfMonthStartDate:LocalDate? = null
    var fourthWeekofMonthButton: Button? = null
    var fourthWeekPanel:CalendarWeekPanel? = null

    var fifthWeekOfMonthStartDate:LocalDate? = null
    var fifthWeekofMonthButton: Button? = null
    var fifthWeekPanel:CalendarWeekPanel? = null

    fun update(selectedDate: LocalDate = LocalDate.now()){
        buildPanelComponents()
        if(!selectedDate.equals(selectedLocalDate)) {
            selectedLocalDate = LocalDate.of(selectedDate.year, selectedDate.month, selectedDate.dayOfMonth)
            currentMonth = selectedLocalDate.month
            currentMonthLocalStartDate = selectedLocalDate.minusDays((selectedLocalDate.dayOfMonth-1).toLong())
            buildPanelComponents()
        }
    }

    fun build():CalendarMonthPanel{
        return this
    }

    fun buildPanelComponents(){
        panel!!.children.forEach { panel!!.removeComponent(it) }
        monthButton = Components.button()
                .withText(currentMonth.toString())
                .withPosition(Positions.create(0,0))
                .build()
        monthButton!!.onMouseReleased {
            uiComponents.updateDate(uiComponents.currentLocalDate.withMonth(currentMonth.value), View.CALENDAR_MONTH)
        }
        panel!!.addComponent(monthButton!!)

        firstWeekOfMonthStartDate = CalendarWeekPanel.determineCurrentWeekLocalStartDate(currentMonthLocalStartDate)
        secondWeekOfMonthStartDate = firstWeekOfMonthStartDate!!.plusDays(7L)
        thirdWeekOfMonthStartDate = secondWeekOfMonthStartDate!!.plusDays(7L)
        fourthWeekOfMonthStartDate = thirdWeekOfMonthStartDate!!.plusDays(7L)
        fifthWeekOfMonthStartDate = fourthWeekOfMonthStartDate!!.plusDays(7L)

        if(fifthWeekOfMonthStartDate!!.month.equals(currentMonth)){
            calendarHeight = height/5-2
        }

        var firstWeekNumber = firstWeekOfMonthStartDate.get(weekFields.weekOfWeekBasedYear())
        firstWeekofMonthButton = Components.button()
                .withText(firstWeekNumber.toString())
                .withPosition(Positions.create(-1,0).relativeToBottomOf(monthButton!!))
                .build()
        firstWeekofMonthButton!!.onMouseReleased { uiComponents.updateDate(firstWeekOfMonthStartDate, View.CALENDAR_WEEK) }
        panel!!.addComponent(firstWeekofMonthButton!!)

        var firstWeekPosition = Positions.create(0 ,-1).relativeToRightOf(firstWeekofMonthButton!!)
        firstWeekPanel =
                CalendarWeekPanel(width-7, calendarHeight, uiComponents, firstWeekPosition, showDayOfWeekLabel,
                        firstWeekOfMonthStartDate)
        firstWeekPanel!!.update(firstWeekOfMonthStartDate)
        firstWeekPanel!!.panel?.let { panel!!.addComponent(it) }

        var secondWeekPosition = Positions.create(-1,0).relativeToBottomOf(firstWeekPanel!!.panel!!)
        secondWeekPanel =
                CalendarWeekPanel(width-7, calendarHeight, uiComponents, secondWeekPosition, showDayOfWeekLabel,
                        secondWeekOfMonthStartDate!!)
        secondWeekPanel!!.update(secondWeekOfMonthStartDate!!)
        secondWeekPanel!!.panel?.let { panel!!.addComponent(it) }


        var secondWeekButtonPosition = Positions.create(-1,calendarHeight-1).relativeToBottomOf(firstWeekofMonthButton!!)
        var secondWeekNumber = secondWeekOfMonthStartDate!!.get(weekFields.weekOfWeekBasedYear())
        secondWeekofMonthButton = Components.button()
                .withText(secondWeekNumber.toString())
                .withPosition(secondWeekButtonPosition)
                .build()
        secondWeekofMonthButton!!.onMouseReleased {
            uiComponents.updateDate(firstWeekOfMonthStartDate!!.plusDays(7L), View.CALENDAR_WEEK)
        }
        panel!!.addComponent(secondWeekofMonthButton!!)

        var thirdWeekPosition = Positions.create(-1,0).relativeToBottomOf(secondWeekPanel!!.panel!!)
        thirdWeekPanel =
                CalendarWeekPanel(width-7, calendarHeight, uiComponents, thirdWeekPosition, showDayOfWeekLabel,
                        thirdWeekOfMonthStartDate!!)
        thirdWeekPanel!!.update(thirdWeekOfMonthStartDate!!)
        thirdWeekPanel!!.panel?.let { panel!!.addComponent(it) }

        var thirdWeekNumber = thirdWeekOfMonthStartDate!!.get(weekFields.weekOfWeekBasedYear())
        var thirdWeekButtonPosition = Positions.create(-1,calendarHeight-1).relativeToBottomOf(secondWeekofMonthButton!!)
        thirdWeekofMonthButton = Components.button()
                .withText(thirdWeekNumber.toString())
                .withPosition(thirdWeekButtonPosition)
                .build()
        thirdWeekofMonthButton!!.onMouseReleased {
            uiComponents.updateDate(secondWeekOfMonthStartDate!!.plusDays(7L), View.CALENDAR_WEEK)
        }
        panel!!.addComponent(thirdWeekofMonthButton!!)

        var fourthWeekPosition = Positions.create(-1,0).relativeToBottomOf(thirdWeekPanel!!.panel!!)
        fourthWeekPanel =
                CalendarWeekPanel(width-7, calendarHeight, uiComponents, fourthWeekPosition, showDayOfWeekLabel,
                        fourthWeekOfMonthStartDate!!)
        fourthWeekPanel!!.update(fourthWeekOfMonthStartDate!!)
        fourthWeekPanel!!.panel?.let { panel!!.addComponent(it) }

        var fourthWeekNumber = fourthWeekOfMonthStartDate!!.get(weekFields.weekOfWeekBasedYear())
        var fourthWeekButtonPosition = Positions.create(-1,calendarHeight-1).relativeToBottomOf(thirdWeekofMonthButton!!)
        fourthWeekofMonthButton = Components.button()
                .withText(fourthWeekNumber.toString())
                .withPosition(fourthWeekButtonPosition)
                .build()
        fourthWeekofMonthButton!!.onMouseReleased {
            uiComponents.updateDate(thirdWeekOfMonthStartDate!!.plusDays(7L), View.CALENDAR_WEEK)
        }
        panel!!.addComponent(fourthWeekofMonthButton!!)

        if(fifthWeekOfMonthStartDate!!.month.equals(currentMonth)){
            var fifthWeekPosition = Positions.create(-1,0).relativeToBottomOf(fourthWeekPanel!!.panel!!)
            fifthWeekPanel =
                    CalendarWeekPanel(width-7, calendarHeight, uiComponents, fifthWeekPosition, showDayOfWeekLabel,
                            fifthWeekOfMonthStartDate!!)
            fifthWeekPanel!!.update(fifthWeekOfMonthStartDate!!)
            fifthWeekPanel!!.panel?.let { panel!!.addComponent(it) }

            var fifthWeekNumber = fifthWeekOfMonthStartDate!!.get(weekFields.weekOfWeekBasedYear())
            var fifthWeekButtonPosition = Positions.create(-1,calendarHeight-1).relativeToBottomOf(fourthWeekofMonthButton!!)
            fifthWeekofMonthButton = Components.button()
                    .withText(fifthWeekNumber.toString())
                    .withPosition(fifthWeekButtonPosition)
                    .build()
            fifthWeekofMonthButton!!.onMouseReleased {
                uiComponents.updateDate(fourthWeekOfMonthStartDate!!.plusDays(7L), View.CALENDAR_WEEK)
            }
            panel!!.addComponent(fifthWeekofMonthButton!!)
        }
    }

    companion object {
        val weekFields = WeekFields.of(Locale.getDefault())
    }
}