package control

import model.state.ApplicationState
import model.budget.BudgetAnalysisState
import model.budget.BudgetItem
import model.budget.BudgetState
import utils.DateTimeUtils

class ApplicationStateBudgetAnalysis(var applicationState: ApplicationState) {


    fun performBudgetAnalysis(): MutableMap<BudgetState?, MutableList<BudgetAnalysisState>> {
        var budgetStatesAnalysisStatesMap: MutableMap<BudgetState?, MutableList<BudgetAnalysisState>> = HashMap()
        val currentBudgetState = applicationState.currentPayPeriodBudgetState
        var currentPayPeriodBudgetItems =
                applicationState.pastUnreconciledBudgetItems?.let {
                    reconcileApplicationBudgetState(currentBudgetState!!, it) }
        var currentBudgetAnalysisStates =
                                performAnalysisOnBudgetItems(currentPayPeriodBudgetItems)
        if(currentBudgetAnalysisStates.isNotEmpty()) {
            var lastBudgetAnalysisState = currentBudgetAnalysisStates.last()
            budgetStatesAnalysisStatesMap[currentBudgetState] = currentBudgetAnalysisStates
            applicationState.futureBudgetStates?.let {
                it.forEach { budgetState ->
                    var budgetStateAnalysisStates = analyzeBudgetState(lastBudgetAnalysisState, budgetState)
                    budgetStatesAnalysisStatesMap[budgetState] = budgetStateAnalysisStates
                    lastBudgetAnalysisState = budgetStateAnalysisStates.last()
                }
            }
        }
        return budgetStatesAnalysisStatesMap
    }

    fun reconcileApplicationBudgetState(budgetState: BudgetState, unreconciledBudgetItems: MutableMap<String, BudgetItem>):
                                MutableMap<String, BudgetItem>{
        var budgetItems = HashMap(unreconciledBudgetItems)
        val localDate = DateTimeUtils.currentDate()
        if(localDate.isAfter(budgetState?.startDate)|| localDate.isEqual(budgetState?.startDate)){
            budgetItems.putAll(retrieveApplicableBudgetItemsForState(budgetState))
        }
        return budgetItems
    }

    fun retrieveApplicableBudgetItemsForState(budgetState: BudgetState): MutableMap<String, BudgetItem> {
        var budgetItems: MutableMap<String, BudgetItem> = HashMap()
        applicationState.budgetItems!!.forEach { name, budgetItem ->
            if(budgetItem.validDueDateForBudgetState(budgetState!!)){
                budgetItems[name] = budgetItem
            }
        }
        return budgetItems
    }

    fun performAnalysisOnBudgetItems(budgetItems: MutableMap<String, BudgetItem>?): MutableList<BudgetAnalysisState> {
        var budgetAnalysisState = BudgetAnalysisState(applicationState)
        var budgetAnalysisStates = analyzeBudgetItems(budgetAnalysisState, budgetItems)
        return budgetAnalysisStates
    }

    private fun analyzeBudgetState(lastBudgetAnalysisState: BudgetAnalysisState?,
                                   budgetState: BudgetState?): MutableList<BudgetAnalysisState> {

        val budgetItemsMap: MutableMap<String, BudgetItem> = retrieveApplicableBudgetItemsForState(budgetState!!)
        return analyzeBudgetItems(lastBudgetAnalysisState, budgetItemsMap)
    }

    private fun analyzeBudgetItems(lastBudgetAnalysisState: BudgetAnalysisState?,
                                   budgetItems: MutableMap<String, BudgetItem>?): MutableList<BudgetAnalysisState> {
        var budgetAnalysisStates = ArrayList<BudgetAnalysisState>()
        var budgetAnalysisState = lastBudgetAnalysisState?.copy()
        if(lastBudgetAnalysisState == null){
            budgetAnalysisState = BudgetAnalysisState(applicationState)
        }
        var orderedBudgetItems: List<BudgetItem>? = budgetItems?.values?.sortedWith(kotlin.comparisons.compareBy({ it.due.dueDate }))
        orderedBudgetItems?.forEach { budgetItem ->
            budgetAnalysisState = budgetAnalysisState?.let { processBudgetItemBudgetAnalysisStateForAnalysis(it, budgetItem) }
            budgetAnalysisState?.let { budgetAnalysisStates.add(it) }
        }
        return (budgetAnalysisStates.sortedWith(kotlin.comparisons.compareBy({ it.date }))).toMutableList()
    }

    private fun processBudgetItemBudgetAnalysisStateForAnalysis(budgetAnalysisState: BudgetAnalysisState,
                                                                budgetItem: BudgetItem): BudgetAnalysisState {
        var newBudgetAnalysisState: BudgetAnalysisState = budgetAnalysisState.copy()
        newBudgetAnalysisState.date = budgetItem.due.dueDate
        newBudgetAnalysisState.budgetItem = budgetItem
        newBudgetAnalysisState.checkingAccountBalance =
                newBudgetAnalysisState.checkingAccountBalance?.plus(budgetItem.actualAmount)
        val transferringSavingsAccountName = budgetItem.transferredToSavingsAccountName
        if(transferringSavingsAccountName != null && !transferringSavingsAccountName.equals("null") &&
                transferringSavingsAccountName.length>0){
            newBudgetAnalysisState.savingsAccountBalances!![transferringSavingsAccountName] =
                    newBudgetAnalysisState.savingsAccountBalances!![transferringSavingsAccountName]!!.minus(budgetItem.actualAmount)
        }
        val transferredCreditAccountName = budgetItem.transferredToCreditAccountName
        if(transferredCreditAccountName != null && !transferredCreditAccountName.equals("null") && transferredCreditAccountName.length>0){
            newBudgetAnalysisState.creditAccountBalances!![transferredCreditAccountName] =
                    newBudgetAnalysisState.creditAccountBalances!![transferredCreditAccountName]!!.minus(budgetItem.actualAmount)
        }
        return newBudgetAnalysisState
    }

}