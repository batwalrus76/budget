package view.screens.calendar

import model.financial.budget.BudgetAnalysisState
import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.temporal.calendar.CalendarDayPanel
import java.time.LocalDate

class CalendarDayScreen(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents):
    BaseCalendarScreen(width, height, component, uiComponents){

    var position = Positions.create(0,0)
    var calendarDayPanel: CalendarDayPanel = CalendarDayPanel(((width / 3) * 2) - 4, height - 2,
            uiComponents, true, position, baseScreen = this)

    override fun update(localDate: LocalDate, budgetAnalysisStates: MutableList<BudgetAnalysisState>?): BudgetState?{
        var appropriateBudgetAnalysisStates = budgetAnalysisStates?.filter {
            it.date!!.equals(localDate)
        }
        calendarDayPanel.panel?.let { panel?.removeComponent(it) }
        calendarDayPanel.update(localDate, appropriateBudgetAnalysisStates!!.toMutableList())
        calendarDayPanel.panel?.let { panel?.addComponent(it) }
        return super.update(localDate, null)
    }

    override fun update(): BudgetState? {
        var appropriateBudgetAnalysisStates =
                uiComponents.findBudgetAnalysisStateForLocalDate(uiComponents.currentLocalDate)
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .withTitle(String.format("%s - %s", TITLE,uiComponents.currentLocalDate))
                .build()
        calendarDayPanel.build()
        appropriateBudgetAnalysisStates?.let { update(uiComponents.currentLocalDate, it) }
        return super.update()
    }

    override fun build() {
        super.build()
    }

    companion object {
        val TITLE: String = "Calendar Day View"
    }
}