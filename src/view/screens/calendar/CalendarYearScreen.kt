package view.screens.calendar

import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.calendar.CalendarYearPanel
import view.screens.BaseScreen
import java.time.LocalDate

class CalendarYearScreen (width: Int, height: Int, var component: Component, uiComponents: ApplicationUIComponents):
    BaseScreen(width, height, uiComponents){

    var calendarYearPanel: CalendarYearPanel = CalendarYearPanel(width-3, height-3, uiComponents)

    fun update(selectedDate: LocalDate){
        calendarYearPanel.update(selectedDate)
    }

    override fun update(): BudgetState {
        if(panel!!.children.contains(calendarYearPanel.panel as Component)) {
            calendarYearPanel.panel?.let { panel?.removeComponent(it) }
        } else {
            calendarYearPanel.build()
        }
        calendarYearPanel.panel?.let { panel?.addComponent(it) }
        update(uiComponents.currentLocalDate)
        return super.update()
    }

    override fun build() {
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(TITLE)
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .build()
    }


    companion object {
        val TITLE: String = "Calendar Year View"
    }
}