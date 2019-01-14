package view.screens

import model.budget.BudgetAnalysisState
import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.builder.component.LabelBuilder
import org.hexworks.zircon.api.builder.component.RadioButtonGroupBuilder
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.RadioButtonGroup
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import view.calendar.CalendarDayPanel
import java.time.LocalDate

class CalendarDayScreen(width: Int, height: Int, var component: Component, uiComponents: ApplicationUIComponents):
    BaseScreen(width, height, uiComponents){

    var position = Positions.create(0,0)
    var calendarDayPanel: CalendarDayPanel = CalendarDayPanel(((width/3)*2)-2, height-2,
            uiComponents, true, position)


    fun update(localDate: LocalDate, budgetAnalysisStates: MutableList<BudgetAnalysisState>){
        var appropriateBudgetAnalysisStates = budgetAnalysisStates.filter {
            it.date!!.equals(localDate)
        }
        calendarDayPanel.update(localDate, appropriateBudgetAnalysisStates.toMutableList())
    }

    override fun update(): BudgetState {
        var appropriateBudgetAnalysisStates =
                uiComponents.findBudgetAnalysisStateForLocalDate(uiComponents.currentLocalDate)
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .withTitle(String.format("%s - %s",TITLE,uiComponents.currentLocalDate))
                .build()
        calendarDayPanel.build()
        calendarDayPanel.panel?.let { panel!!.addComponent(it) }
        appropriateBudgetAnalysisStates?.let { update(uiComponents.currentLocalDate, it) }
        return super.update()
    }

    override fun build() {
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .withTitle(String.format("%s - %s",TITLE,uiComponents.currentLocalDate))
                .build()
        calendarDayPanel.build()
        calendarDayPanel.panel?.let { panel!!.addComponent(it) }
    }

    companion object {
        val TITLE: String = "Calendar Day View"
    }
}