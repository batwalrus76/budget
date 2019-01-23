package view.screens.calendar

import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.calendar.CalendarWeekPanel
import view.screens.BaseScreen
import java.time.LocalDate

class CalendarWeekScreen(width: Int, height: Int, var component: Component, uiComponents: ApplicationUIComponents):
        BaseScreen(width, height, uiComponents){

    var calendarWeekPanel = CalendarWeekPanel(width-3, height-3, uiComponents)

    fun update(selectedDate: LocalDate){
        val startOfWeekDate = CalendarWeekPanel.determineCurrentWeekLocalStartDate(selectedDate)
        val titleWithDate = String.format("%s - %s to %s", TITLE, startOfWeekDate.toString(),
                startOfWeekDate.plusDays(6L).toString())
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .withTitle(titleWithDate)
                .build()
        calendarWeekPanel.panel?.let { panel!!.removeComponent(it) }
        calendarWeekPanel.update(selectedDate)
        calendarWeekPanel.panel?.let { panel?.addComponent(it) }
    }

    override fun update():BudgetState{
        update(uiComponents.currentLocalDate)
        return super.update()
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
        calendarWeekPanel.panel?.let { panel?.addComponent(it) }
    }

    companion object {
        val TITLE: String = "Calendar Week View"
    }
}