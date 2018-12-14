package view

import model.state.ApplicationState
import model.budget.BudgetAnalysisState
import org.hexworks.zircon.api.component.Panel

abstract class BudgetPanel(var width: Int, var height: Int, var applicationState: ApplicationState) {

    var panel: Panel? = null

    abstract fun build()

    abstract fun update(budgetAnalysisState: BudgetAnalysisState)
}