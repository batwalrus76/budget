package view.screens.calendar

import model.financial.budget.BudgetState
import model.enums.budget.Recurrence
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.temporal.calendar.CalendarWeekPanel
import view.financial.items.ItemConfigurationPanel
import java.time.LocalDate

class CalendarWeekScreen(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents):
        BaseCalendarScreen(width, height, component, uiComponents, width/5){

    var calendarWeekPanel = CalendarWeekPanel(((4*width)/5)-2, height-3, uiComponents, baseScreen = this)

    fun update(selectedDate: LocalDate): BudgetState? {
        if(panel!!.children.contains(calendarWeekPanel.panel as Component)) {
            calendarWeekPanel.panel?.let { panel!!.removeComponent(it) }
        }
        calendarWeekPanel.update(selectedDate)
        calendarWeekPanel.panel?.let { panel?.addComponent(it) }
        return super.update(selectedDate, null)
    }

    override fun update():BudgetState? {
        return update(uiComponents.currentLocalDate)
    }

    override fun build() {
        val startOfWeekDate = CalendarWeekPanel.determineCurrentWeekLocalStartDate(uiComponents.currentLocalDate)
        val titleWithDate = String.format("%s - %s to %s", TITLE, startOfWeekDate.toString(),
                startOfWeekDate.plusDays(6L).toString())
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .withTitle(titleWithDate)
                .build()
        calendarWeekPanel.build()
    }

    companion object {
        val TITLE: String = "Calendar Week View"
    }
}