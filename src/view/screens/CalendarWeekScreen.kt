package view.screens

import model.budget.BudgetAnalysisState
import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.graphics.BoxType
import view.calendar.CalendarDayPanel
import view.calendar.CalendarWeekPanel
import view.payPeriod.PayPeriodItemsPanel
import view.payPeriod.PayPeriodPanel
import java.time.DayOfWeek
import java.time.LocalDate

class CalendarWeekScreen(var width: Int, var height: Int, var component: Component,
                         var uiComponents: ApplicationUIComponents) {

    var panel = Components.panel()
            .wrapWithBox(true) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
            .withPosition(Positions.create(0,0).relativeToBottomOf(component))
            .withTitle(TITLE)
            .build()
    var calendarWeekPanel = CalendarWeekPanel(width-2, height-2, uiComponents).build()

    fun update(selectedDate: LocalDate){
        calendarWeekPanel.update(selectedDate)
    }

    fun build() {
        calendarWeekPanel.panel?.let { panel?.addComponent(it) }
    }

    companion object {
        val TITLE: String = "Calendar Week View"
    }
}