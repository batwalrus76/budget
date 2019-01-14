package view.screens

import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.calendar.CalendarMonthPanel
import java.time.LocalDate

class CalendarMonthScreen (width: Int, height: Int, var component: Component, uiComponents: ApplicationUIComponents):
    BaseScreen(width, height, uiComponents){

    var calendarMonthPanel:CalendarMonthPanel = CalendarMonthPanel(width-4, height-4, uiComponents)

    fun update(selectedDate: LocalDate){
        calendarMonthPanel.update(selectedDate)
    }

    override fun build() {
        panel= Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(TITLE)
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .build()
        calendarMonthPanel.build()
        calendarMonthPanel.panel?.let { panel?.addComponent(it) }
    }

    companion object {
        val TITLE: String = "Calendar Month View"
    }
}