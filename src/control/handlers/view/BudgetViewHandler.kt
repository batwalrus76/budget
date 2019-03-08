package control.handlers.view

import model.financial.account.Account
import model.financial.budget.BudgetItem

interface BudgetViewHandler: BaseViewHandler {

    fun handleNewBudgetItem(budgetItem: BudgetItem, hypothetical: Boolean = false)
    fun handleModifiedBudgetItem(budgetItem: BudgetItem)
    fun handleNewAccount(account: Account)

}