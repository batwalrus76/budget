package view.screens

import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.grid.TileGrid
import view.input.InputPanel

abstract class BaseScreen(var width: Int, var height: Int, var uiComponents: ApplicationUIComponents){

    var panel: Panel? = null

    abstract fun build()

    open fun update(): BudgetState {
        return uiComponents.currentViewedBudgetState!!
    }

}