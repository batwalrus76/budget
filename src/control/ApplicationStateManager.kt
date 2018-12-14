package control

import model.state.ApplicationState
import model.budget.BudgetItem
import model.budget.BudgetState
import utils.DateTimeUtils
import java.io.File

class ApplicationStateManager(var applicationState: ApplicationState,
                              var applicationStateBudgetAnalysis: ApplicationStateBudgetAnalysis) {

    fun augmentApplicationFutureBudgetStates() {
        val todaysDate = DateTimeUtils.currentDate()
        val endOfOneYearPeriod = todaysDate.plusYears(1).minusDays(1)
        var lastFutureBudgetState: BudgetState? = null
        if(applicationState.futureBudgetStates.isNotEmpty()) {
            lastFutureBudgetState = applicationState.futureBudgetStates.last()
        } else {
            lastFutureBudgetState = applicationState.currentPayPeriodBudgetState
        }
        while(lastFutureBudgetState?.endDate!!.isBefore(endOfOneYearPeriod)){
            var newFutureBudgetState = BudgetState(lastFutureBudgetState?.endDate!!.plusDays(1),
                    lastFutureBudgetState?.endDate!!.plusDays(7))
            applicationState.futureBudgetStates.add(newFutureBudgetState)
            lastFutureBudgetState = newFutureBudgetState
        }
    }

    fun reconcileApplicationStateToTodaysDate() {
        val todaysDate = DateTimeUtils.currentDate()
        if(todaysDate.isAfter(applicationState.currentPayPeriodBudgetState?.endDate)){
            this.augmentUnreconciledItemsFromPastBudgetState()
            applicationState.currentPayPeriodBudgetState = applicationState.futureBudgetStates.removeAt(0)
            while(todaysDate.isAfter(applicationState.currentPayPeriodBudgetState!!.endDate)){
                this.augmentUnreconciledItemsFromPastBudgetState()
                applicationState.currentPayPeriodBudgetState = applicationState.futureBudgetStates.removeAt(0)
            }
        }
        augmentApplicationFutureBudgetStates()
    }

    private fun augmentUnreconciledItemsFromPastBudgetState() {
        var newUnreconciledBudgetItems: MutableMap<String, BudgetItem> =
                applicationStateBudgetAnalysis.
                        retrieveApplicableBudgetItemsForState(applicationState.currentPayPeriodBudgetState!!)
        applicationState.pastUnreconciledBudgetItems!!.putAll(newUnreconciledBudgetItems)
    }


    companion object {
        val DEFAULT_STATE_FILE_LOCATION: String = "/Users/pascact1/budget.json"
        val DEFAULT_STATE_FILE: File = File(DEFAULT_STATE_FILE_LOCATION)

        fun buildApplicationStateFromDefaultFileLocation(): ApplicationState? {
            var applicationState: ApplicationState? = null
            if (DEFAULT_STATE_FILE?.exists()) {
                applicationState = ApplicationState.deserializeJsonToApplicationState(DEFAULT_STATE_FILE)
            } else {
                applicationState = ApplicationState()
                serializeToDefaultJsonFileLocation(applicationState)
            }
            return applicationState
        }

        fun serializeToDefaultJsonFileLocation(applicationState: ApplicationState){
            var applicationStateJson = applicationState?.serializeApplicationStateToJson()
            DEFAULT_STATE_FILE.bufferedWriter().use { out -> out.write(applicationStateJson) }
        }
    }
}