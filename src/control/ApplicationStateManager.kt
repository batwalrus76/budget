package control

import model.ApplicationState
import model.BudgetItem
import model.BudgetState
import control.ApplicationStateBudgetAnalysis.Companion.processFutureBudgetItemsIntoCurrentItems
import control.ApplicationStateCLIProcessor.Companion.determineNextDueLocalDateTime
import java.io.File
import java.time.LocalDateTime

class ApplicationStateManager(var applicationState: ApplicationState) {

    var minimumFutureBudgetStates: Int = 4

    fun reconcilePastCurrentFutureBudgetStates() {
        val currentDate: LocalDateTime = LocalDateTime.now()
        rectifyCurrentBudgetStateUnreconciledItems()
        if(applicationState.currentPayPeriodBudgetState!!.endDate!!.isBefore(currentDate)){
            rectifyPastAndCurrentBudgetStates()
        }
        if(applicationState.futureBudgetStates.size < minimumFutureBudgetStates){
            augmentApplicationFutureBudgetStates()
        }
    }

    private fun rectifyCurrentBudgetStateUnreconciledItems() {
        val currentDate: LocalDateTime = LocalDateTime.now()
        var currentBudgetState = applicationState.currentPayPeriodBudgetState
        var currentBudgetItems = currentBudgetState?.currentBudgetItems?.values?.toList()
        currentBudgetItems?.forEach { budgetItem ->
            if(budgetItem.due.isBefore(currentDate)){
                applicationState.pastUnreconciledBudgetItems?.put(budgetItem.name, budgetItem)
                currentBudgetState?.currentBudgetItems?.remove(budgetItem.name)
            }
        }
    }

    fun augmentApplicationFutureBudgetStates() {
        var numFutureBudgetStates: Int = applicationState.futureBudgetStates.size
        var applicationStateBudgetAnalysis = ApplicationStateBudgetAnalysis(applicationState)
        var lastBudgetState: BudgetState? = null
        if(applicationState.futureBudgetStates.size > 0) {
            lastBudgetState = applicationState.futureBudgetStates.get(numFutureBudgetStates - 1)
        } else {
            lastBudgetState = applicationState.currentPayPeriodBudgetState
        }
        for (i in numFutureBudgetStates..minimumFutureBudgetStates-1){
            var startDate = lastBudgetState?.startDate?.plusDays(7L)
            var endDate = lastBudgetState?.endDate?.plusDays(7L)
            var currentBudgetItems: MutableMap<String, BudgetItem>? = HashMap()
            startDate?.let { currentBudgetItems =
                    processFutureBudgetItemsIntoCurrentItems(applicationStateBudgetAnalysis, it)
            }
            var newFutureBudgetState = BudgetState(currentBudgetItems, startDate, endDate)
            applicationState.futureBudgetStates.add(newFutureBudgetState)
            lastBudgetState = newFutureBudgetState
        }
    }

    fun rectifyPastAndCurrentBudgetStates() {
        val pastBudgetState: BudgetState? = applicationState.currentPayPeriodBudgetState
        if(applicationState.futureBudgetStates.size > 0) {
            val currentBudgetState: BudgetState? = applicationState.futureBudgetStates.removeAt(0)
            applicationState.currentPayPeriodBudgetState = currentBudgetState
        }
        val pastUnreconciledBudgetItems = applicationState?.pastUnreconciledBudgetItems
        applicationState?.pastUnreconciledBudgetItems = pastUnreconciledBudgetItems
        pastBudgetState?.currentBudgetItems?.let { pastUnreconciledBudgetItems?.putAll(it) }
        applicationState?.pastUnreconciledBudgetItems = pastUnreconciledBudgetItems
        applicationState?.futureBudgetItems?.values?.forEach {
            budgetItem ->  reconcileFutureBudgetItemsIntoNewBudgetStates(budgetItem)
        }
    }

    fun reconcileFutureBudgetItemsIntoNewBudgetStates(budgetItem: BudgetItem) {
        applicationState.currentPayPeriodBudgetState?.let {
            reconcileBudgetItemIntoBudgetState(applicationState, budgetItem, it)
        }
        applicationState.futureBudgetStates.forEach {
            budgetState -> reconcileBudgetItemIntoBudgetState(applicationState, budgetItem, budgetState)
        }
    }

    fun reconcileBudgetItemIntoBudgetState(applicationState: ApplicationState, budgetItem: BudgetItem,
                                           budgetState: BudgetState){
        if(budgetItem.due.isAfter(budgetState?.startDate)){
            if(budgetItem.due.isBefore(budgetState?.endDate)) {
                budgetState?.currentBudgetItems?.put(budgetItem.name, budgetItem)
            } else if(budgetItem.due.isAfter(budgetState?.endDate)){
                applicationState?.futureBudgetItems?.put(budgetItem.name, budgetItem)
            }
        } else {
            applicationState?.pastUnreconciledBudgetItems?.put(budgetItem.name, budgetItem)
        }
    }

    fun updateFutureBudgetStatesWithNewBudgetItem(budgetItem: BudgetItem): BudgetItem {
        var localBudgetItem: BudgetItem = budgetItem.copy()
        applicationState.futureBudgetStates.forEach { futureBudgetState -> localBudgetItem =
                            updateBudgetStateAndFutureRecurringBudgetItem(futureBudgetState, localBudgetItem)}
        return localBudgetItem
    }

    fun updateBudgetStateAndFutureRecurringBudgetItem(budgetState: BudgetState, budgetItem: BudgetItem): BudgetItem {
        var localBudgetItem: BudgetItem = budgetItem.copy()
        if((budgetItem.due.isAfter(budgetState.startDate) || budgetItem.due.isEqual(budgetState.startDate)) &&
                (budgetItem.due.isBefore(budgetState.endDate)||budgetItem.due.isEqual(budgetState.endDate))){
            budgetState.currentBudgetItems?.put(budgetItem.name, budgetItem)
            val nextDueDate = determineNextDueLocalDateTime(budgetItem.recurrence, budgetItem.due)
            localBudgetItem.due = nextDueDate
        }
        return localBudgetItem
    }

    companion object {
        val DEFAULT_STATE_FILE_LOCATION: String = "/Users/pascact1/.budget"
        val DEFAULT_STATE_FILE: File = File(DEFAULT_STATE_FILE_LOCATION)

        fun buildApplicationStateFromDefaultFileLocation(): ApplicationState {
            var applicationState: ApplicationState?
            if (DEFAULT_STATE_FILE?.exists()) {
                applicationState = ApplicationState.deserializeJsonToApplicationState(DEFAULT_STATE_FILE)
            } else {
                applicationState = ApplicationState()
            }
            return applicationState
        }
    }
}