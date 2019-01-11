package control

import model.state.ApplicationState
import model.budget.BudgetAnalysisState
import model.budget.BudgetItem
import model.budget.BudgetState
import model.core.DueDate
import utils.DateTimeUtils
import java.time.LocalDate

class ApplicationStateBudgetAnalysis(var applicationState: ApplicationState) {


    fun performBudgetAnalysis(): MutableMap<BudgetState?, MutableList<BudgetAnalysisState>> {
        var budgetStatesAnalysisStatesMap: MutableMap<BudgetState?, MutableList<BudgetAnalysisState>> = HashMap()
        val currentBudgetState = applicationState.currentPayPeriodBudgetState
        var currentPayPeriodBudgetItems =
                applicationState.pastUnreconciledBudgetItems?.let {
                    reconcileApplicationBudgetState(currentBudgetState!!, it) }
        var currentBudgetAnalysisStates =
                                performAnalysisOnBudgetItems(currentPayPeriodBudgetItems, currentBudgetState)
        var lastBudgetAnalysisState = BudgetAnalysisState()
        if(currentBudgetAnalysisStates.isNotEmpty()) {
            lastBudgetAnalysisState = currentBudgetAnalysisStates.last()
        } else {
            lastBudgetAnalysisState.checkingAccountBalance = applicationState.checkingAccount?.balance
            lastBudgetAnalysisState.date = LocalDate.now()
            applicationState.creditAccounts?.map {
                lastBudgetAnalysisState.creditAccountBalances?.put(it.key, it.value.balance)
            }
            applicationState.savingsAccounts?.map {
                lastBudgetAnalysisState.savingsAccountBalances?.put(it.key, it.value.balance)
            }
        }
        budgetStatesAnalysisStatesMap[currentBudgetState] = currentBudgetAnalysisStates
        applicationState.futureBudgetStates?.let {
            it.forEach { budgetState ->
                var budgetStateAnalysisStates = analyzeBudgetState(lastBudgetAnalysisState, budgetState)
                budgetStatesAnalysisStatesMap[budgetState] = budgetStateAnalysisStates
                lastBudgetAnalysisState = budgetStateAnalysisStates.last()
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
            if(budgetItem.validForBudgetState(budgetState!!)){
                budgetItems[name] = budgetItem
            }
        }
        return budgetItems
    }

    fun performAnalysisOnBudgetItems(budgetItems: MutableMap<String, BudgetItem>?,
                                     budgetState: BudgetState?): MutableList<BudgetAnalysisState> {
        var budgetAnalysisState = BudgetAnalysisState(applicationState)
        var budgetAnalysisStates = analyzeBudgetItems(budgetAnalysisState, budgetItems, budgetState)
        return budgetAnalysisStates
    }

    private fun analyzeBudgetState(lastBudgetAnalysisState: BudgetAnalysisState?,
                                   budgetState: BudgetState?): MutableList<BudgetAnalysisState> {

        val budgetItemsMap: MutableMap<String, BudgetItem> = retrieveApplicableBudgetItemsForState(budgetState!!)
        return analyzeBudgetItems(lastBudgetAnalysisState, budgetItemsMap, budgetState)
    }

    private fun analyzeBudgetItems(lastBudgetAnalysisState: BudgetAnalysisState?,
                                   budgetItems: MutableMap<String, BudgetItem>?,
                                    budgetState: BudgetState?): MutableList<BudgetAnalysisState> {
        var budgetAnalysisStates = ArrayList<BudgetAnalysisState>()
        var budgetAnalysisState = lastBudgetAnalysisState?.copy()
        if(lastBudgetAnalysisState == null){
            budgetAnalysisState = BudgetAnalysisState(applicationState)
        }
        var orderedBudgetItems: List<BudgetItem>? = budgetItems?.values?.sortedWith(kotlin.comparisons.compareBy({ it.due.dueDate }))
        orderedBudgetItems?.forEach { budgetItem ->
            budgetAnalysisState = budgetAnalysisState?.let { processBudgetItemBudgetAnalysisStateForAnalysis(it, budgetItem, budgetState) }
            budgetAnalysisState?.let { budgetAnalysisStates.add(it) }
        }
        return (budgetAnalysisStates.sortedWith(kotlin.comparisons.compareBy({ it.date }))).toMutableList()
    }

    private fun processBudgetItemBudgetAnalysisStateForAnalysis(budgetAnalysisState: BudgetAnalysisState,
                                                                budgetItem: BudgetItem,
                                                                budgetState: BudgetState?): BudgetAnalysisState {
        var newBudgetAnalysisState: BudgetAnalysisState = budgetAnalysisState.copy()
        newBudgetAnalysisState.budgetItem = budgetItem
        var validDueDate: DueDate? = budgetItem.validDueDateForBudgetState(budgetState!!)
        newBudgetAnalysisState.date = validDueDate?.dueDate
        if (validDueDate != null) {
            newBudgetAnalysisState.checkingAccountBalance =
                    newBudgetAnalysisState.checkingAccountBalance?.plus(validDueDate.amount)
            val transferringSavingsAccountName = budgetItem.transferredToSavingsAccountName
            if(transferringSavingsAccountName != null && !transferringSavingsAccountName.equals("null") &&
                    transferringSavingsAccountName.length>0){
                newBudgetAnalysisState.savingsAccountBalances!![transferringSavingsAccountName] =
                        newBudgetAnalysisState.savingsAccountBalances!![transferringSavingsAccountName]!!.minus(validDueDate!!.amount)
            }
            val transferredCreditAccountName = budgetItem.transferredToCreditAccountName
            if(transferredCreditAccountName != null && !transferredCreditAccountName.equals("null") && transferredCreditAccountName.length>0){
                newBudgetAnalysisState.creditAccountBalances!![transferredCreditAccountName] =
                        newBudgetAnalysisState.creditAccountBalances!![transferredCreditAccountName]!!.minus(validDueDate!!.amount)
            }
        }
        return newBudgetAnalysisState
    }

}