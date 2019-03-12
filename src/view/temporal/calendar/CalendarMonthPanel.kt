package view.temporal.calendar

import model.enums.view.View
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.kotlin.onMouseReleased
import view.screens.BaseScreen
import java.time.LocalDate
import java.util.Locale
import java.time.temporal.WeekFields



class CalendarMonthPanel(width: Int, height: Int, uiComponents: ApplicationUIComponents,
                         displayBox: Boolean = true, position: Position = Positions.create(0,0), showDayOfWeekLabel:Boolean = false,
                         selectedLocalDate:LocalDate = LocalDate.now(), baseScreen: BaseScreen):
        BaseCalendarPanel(width, height, uiComponents, displayBox, position, showDayOfWeekLabel, selectedLocalDate,
                baseScreen){

    private var currentMonth = selectedLocalDate.month

    private var monthButton: Button? = null

    private var calendarHeight = (height/4)-2
    private var currentMonthLocalStartDate = selectedLocalDate.minusDays((selectedLocalDate.dayOfMonth-1).toLong())


    private var firstWeekOfMonthStartDate:LocalDate =
            CalendarWeekPanel.determineCurrentWeekLocalStartDate(currentMonthLocalStartDate)
    private var firstWeekofMonthButton: Button? = null
    private var firstWeekPanel:CalendarWeekPanel? = null

    private var secondWeekOfMonthStartDate:LocalDate? = null
    private var secondWeekofMonthButton: Button? = null
    private var secondWeekPanel:CalendarWeekPanel? = null

    private var thirdWeekOfMonthStartDate:LocalDate? = null
    private var thirdWeekofMonthButton: Button? = null
    private var thirdWeekPanel:CalendarWeekPanel? = null

    private var fourthWeekOfMonthStartDate:LocalDate? = null
    private var fourthWeekofMonthButton: Button? = null
    private var fourthWeekPanel:CalendarWeekPanel? = null

    private var fifthWeekOfMonthStartDate:LocalDate? = null
    private var fifthWeekofMonthButton: Button? = null
    private var fifthWeekPanel:CalendarWeekPanel? = null

    fun update(selectedDate: LocalDate = LocalDate.now()){
        buildPanelComponents()
        if(selectedDate != selectedLocalDate) {
            selectedLocalDate = LocalDate.of(selectedDate.year, selectedDate.month, selectedDate.dayOfMonth)
            currentMonth = selectedLocalDate.month
            currentMonthLocalStartDate = selectedLocalDate.minusDays((selectedLocalDate.dayOfMonth-1).toLong())
            buildPanelComponents()
        }
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
                        firstWeekOfMonthStartDate, baseScreen = baseScreen)
        firstWeekPanel!!.update(firstWeekOfMonthStartDate)
        firstWeekPanel!!.panel?.let { panel!!.addComponent(it) }

        var secondWeekPosition = Positions.create(-1,0).relativeToBottomOf(firstWeekPanel!!.panel!!)
        secondWeekPanel =
                CalendarWeekPanel(width-7, calendarHeight, uiComponents, secondWeekPosition, showDayOfWeekLabel,
                        secondWeekOfMonthStartDate!!, baseScreen = baseScreen)
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
                        thirdWeekOfMonthStartDate!!, baseScreen = baseScreen)
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
                        fourthWeekOfMonthStartDate!!, baseScreen = baseScreen)
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
                            fifthWeekOfMonthStartDate!!, baseScreen = baseScreen)
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