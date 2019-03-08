package view.screens

import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.component.Panel

abstract class BaseScreen(var width: Int, var height: Int, var uiComponents: ApplicationUIComponents){

    var panel: Panel? = null

    abstract fun build()

    open fun update(): BudgetState? {
        return uiComponents.currentViewedBudgetState
    }

}