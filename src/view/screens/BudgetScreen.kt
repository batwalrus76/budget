package view.screens

import model.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.grid.TileGrid
import view.budget.MonthlyBudgetPanel
import view.budget.YearlyBudgetPanel
import view.control.MainControlsPanel

class BudgetScreen(var width: Int, var height: Int, var component: Component, var uiComponents: ApplicationUIComponents){

    var panel: Panel? = Components.panel()
            .wrapWithBox(false) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
            .withPosition(Positions.create(0,0).relativeToBottomOf(component))
            .build()
    var monthlyBudgetPanel: MonthlyBudgetPanel? = null
    var yearlyBudgetPanel: YearlyBudgetPanel? = null

    fun update(): BudgetState {
        return uiComponents.currentViewedBudgetState!!
    }

    fun build() {
        panel!!.children.forEach { child -> panel!!.removeComponent(child) }
        monthlyBudgetPanel = MonthlyBudgetPanel(width, (2*height/5+5), uiComponents.applicationState!!)
        monthlyBudgetPanel!!.build()
        monthlyBudgetPanel!!.panel?.let { panel?.addComponent(it) }
        yearlyBudgetPanel = YearlyBudgetPanel(width, (3*height/5-5), monthlyBudgetPanel!!.panel!!, uiComponents.applicationState!!)
        yearlyBudgetPanel!!.build()
        yearlyBudgetPanel!!.panel?.let { panel?.addComponent(it) }
    }

}