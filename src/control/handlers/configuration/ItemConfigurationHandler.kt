package control.handlers.configuration

import model.financial.budget.BudgetItem

interface ItemConfigurationHandler: BaseConfigurationHandler {

    fun handleConfigurationItem(item: BudgetItem)

}