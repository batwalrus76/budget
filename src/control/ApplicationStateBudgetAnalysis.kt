package control

import model.ApplicationState
import model.BudgetAnalysisState
import model.BudgetItem
import model.BudgetState
import model.BudgetState.Companion.determineNextDueLocalDateTime
import model.BudgetState.Companion.determinePreviousDueLocalDateTime
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime

class ApplicationStateBudgetAnalysis(var applicationState: ApplicationState) {


    fun performBudgetAnalysis(isComplete: Boolean): MutableList<BudgetAnalysisState> {
        var currentBudgetAnalysisStates =
                                performAnalysisOnBudgetItems(applicationState.pastUnreconciledBudgetItems)
        var lastBudgetAnalysisState: BudgetAnalysisState? = null
        if(currentBudgetAnalysisStates != null && currentBudgetAnalysisStates.isNotEmpty()){
            lastBudgetAnalysisState = currentBudgetAnalysisStates.last()
        }
        applicationState.currentPayPeriodBudgetState?.let {
            currentBudgetAnalysisStates?.addAll(performAndShowWeeklyBudgetAnalysis(lastBudgetAnalysisState,it))
        }
        if(currentBudgetAnalysisStates != null && currentBudgetAnalysisStates.isNotEmpty()){
            lastBudgetAnalysisState = currentBudgetAnalysisStates.last()
        }
        if(isComplete && applicationState.futureBudgetStates != null) {
            applicationState.futureBudgetStates?.let {
                it.forEach {
                    currentBudgetAnalysisStates?.addAll(performAndShowWeeklyBudgetAnalysis(lastBudgetAnalysisState, it))
                    lastBudgetAnalysisState = currentBudgetAnalysisStates.last()
                }
            }
        }
        currentBudgetAnalysisStates?.addAll(performAnalysisOnBudgetItems(applicationState.futureBudgetItems))
        return currentBudgetAnalysisStates
    }

    fun performBudgetAnalysis(budgetState: BudgetState): MutableList<BudgetAnalysisState>{
        return performAnalysisOnBudgetItems(budgetState.currentBudgetItems)
    }

    fun performAnalysisOnBudgetItems(budgetItems: MutableMap<String, BudgetItem>?): MutableList<BudgetAnalysisState> {
        var budgetAnalysisState = BudgetAnalysisState(applicationState)
        var budgetAnalysisStates = analyzeBudgetItems(budgetAnalysisState, budgetItems)
        return budgetAnalysisStates

    }
    fun performAndShowWeeklyBudgetAnalysis(lastBudgetAnalysisState: BudgetAnalysisState?, budgetState: BudgetState):
            MutableList<BudgetAnalysisState> {
        val budgetAnalysisStates: MutableList<BudgetAnalysisState> =
                                        analyzeBudgetItems(lastBudgetAnalysisState, budgetState.currentBudgetItems)
        var budgetStateDailyItinerary = processCurrentBudgetStatetinerary(budgetAnalysisStates)
        analyzeBudgetDailyItinerary(budgetStateDailyItinerary)
        return budgetAnalysisStates
    }

    private fun analyzeBudgetItems(lastBudgetAnalysisState: BudgetAnalysisState?,
                                   budgetItems: MutableMap<String, BudgetItem>?): MutableList<BudgetAnalysisState> {
        var budgetAnalysisStates = ArrayList<BudgetAnalysisState>()
        var budgetAnalysisState = lastBudgetAnalysisState?.copy()
        if(lastBudgetAnalysisState == null){
            budgetAnalysisState = BudgetAnalysisState(applicationState)
        }
        var orderedBudgetItems: List<BudgetItem>? = budgetItems?.values?.sortedWith(kotlin.comparisons.compareBy({ it.due }))
        orderedBudgetItems?.forEach { budgetItem ->
            budgetAnalysisState = budgetAnalysisState?.let { processBudgetItemBudgetAnalysisStateForAnalysis(it, budgetItem) }
            budgetAnalysisState?.let { budgetAnalysisStates.add(it) }
            budgetAnalysisState = budgetAnalysisState?.copy()
        }
        return (budgetAnalysisStates.sortedWith(kotlin.comparisons.compareBy({ it.date }))).toMutableList()
    }

    private fun processBudgetItemBudgetAnalysisStateForAnalysis(budgetAnalysisState: BudgetAnalysisState,
                                                                budgetItem: BudgetItem): BudgetAnalysisState {
        var newBudgetAnalysisState: BudgetAnalysisState = budgetAnalysisState.copy()
        newBudgetAnalysisState.date = budgetItem.due
        newBudgetAnalysisState.budgetItem = budgetItem
        newBudgetAnalysisState.checkingAccountBalance =
                                newBudgetAnalysisState.checkingAccountBalance?.plus(budgetItem.actualAmount)
        return newBudgetAnalysisState
    }

    private fun analyzeBudgetDailyItinerary(budgetAnalysisStateItinerary: MutableMap<DayOfWeek,
                            MutableList<BudgetAnalysisState>>) {
        //process pay period from Friday to Thursday
        var finalCheckingAccountBalance = processCurrentBudgetDay(DayOfWeek.FRIDAY, budgetAnalysisStateItinerary)
        var tempCheckingAccountBalance  = processCurrentBudgetDay(DayOfWeek.SATURDAY, budgetAnalysisStateItinerary)
        finalCheckingAccountBalance =
                if (tempCheckingAccountBalance != null) tempCheckingAccountBalance else finalCheckingAccountBalance
        tempCheckingAccountBalance  = processCurrentBudgetDay(DayOfWeek.SUNDAY, budgetAnalysisStateItinerary)
        finalCheckingAccountBalance =
                if (tempCheckingAccountBalance != null) tempCheckingAccountBalance else finalCheckingAccountBalance
        tempCheckingAccountBalance  = processCurrentBudgetDay(DayOfWeek.MONDAY, budgetAnalysisStateItinerary)
        finalCheckingAccountBalance =
                if (tempCheckingAccountBalance != null) tempCheckingAccountBalance else finalCheckingAccountBalance
        tempCheckingAccountBalance  = processCurrentBudgetDay(DayOfWeek.TUESDAY, budgetAnalysisStateItinerary)
        finalCheckingAccountBalance =
                if (tempCheckingAccountBalance != null) tempCheckingAccountBalance else finalCheckingAccountBalance
        tempCheckingAccountBalance  = processCurrentBudgetDay(DayOfWeek.WEDNESDAY, budgetAnalysisStateItinerary)
        finalCheckingAccountBalance =
                if (tempCheckingAccountBalance != null) tempCheckingAccountBalance else finalCheckingAccountBalance
        tempCheckingAccountBalance  = processCurrentBudgetDay(DayOfWeek.THURSDAY, budgetAnalysisStateItinerary)
        finalCheckingAccountBalance =
                if (tempCheckingAccountBalance != null) tempCheckingAccountBalance else finalCheckingAccountBalance
        if(finalCheckingAccountBalance == null) {
            finalCheckingAccountBalance = 0.0
        }
    }

    private fun processCurrentBudgetDay(dayOfWeek: DayOfWeek,
                                        analysisBudgetDailyItinerary: MutableMap<DayOfWeek,
                                                MutableList<BudgetAnalysisState>>): Double? {
        val currentDayofWeekBudgetItems: MutableList<BudgetAnalysisState>? = analysisBudgetDailyItinerary.get(dayOfWeek)
        var checkingAccountBalance: Double? = null
        currentDayofWeekBudgetItems?.forEach { budgetAnalysisState ->
            checkingAccountBalance = budgetAnalysisState.checkingAccountBalance!!
        }
        return checkingAccountBalance
    }

    private fun processCurrentBudgetStatetinerary(budgetAnalysisStates: MutableList<BudgetAnalysisState>)
            : MutableMap<DayOfWeek, MutableList<BudgetAnalysisState>> {
        var mondayBudgetStates: MutableList<BudgetAnalysisState> = ArrayList()
        var tuesdayBudgetStates: MutableList<BudgetAnalysisState> = ArrayList()
        var wednesdayBudgetStates: MutableList<BudgetAnalysisState> = ArrayList()
        var thursdayBudgetStates: MutableList<BudgetAnalysisState> = ArrayList()
        var fridayBudgetStates: MutableList<BudgetAnalysisState> = ArrayList()
        var saturdayBudgetStates: MutableList<BudgetAnalysisState> = ArrayList()
        var sundayBudgetStates: MutableList<BudgetAnalysisState> = ArrayList()
        budgetAnalysisStates.forEach { budgetAnalysisState ->
            when (budgetAnalysisState.date?.dayOfWeek) {
                DayOfWeek.MONDAY -> mondayBudgetStates.add(budgetAnalysisState)
                DayOfWeek.TUESDAY -> tuesdayBudgetStates.add(budgetAnalysisState)
                DayOfWeek.WEDNESDAY -> wednesdayBudgetStates.add(budgetAnalysisState)
                DayOfWeek.THURSDAY -> thursdayBudgetStates.add(budgetAnalysisState)
                DayOfWeek.FRIDAY -> fridayBudgetStates.add(budgetAnalysisState)
                DayOfWeek.SATURDAY -> saturdayBudgetStates.add(budgetAnalysisState)
                DayOfWeek.SUNDAY -> sundayBudgetStates.add(budgetAnalysisState)
            }
        }
        var budgetStateDailyItinerary: HashMap<DayOfWeek, MutableList<BudgetAnalysisState>> = HashMap()
        budgetStateDailyItinerary.put(DayOfWeek.MONDAY, mondayBudgetStates)
        budgetStateDailyItinerary.put(DayOfWeek.TUESDAY, tuesdayBudgetStates)
        budgetStateDailyItinerary.put(DayOfWeek.WEDNESDAY, wednesdayBudgetStates)
        budgetStateDailyItinerary.put(DayOfWeek.THURSDAY, thursdayBudgetStates)
        budgetStateDailyItinerary.put(DayOfWeek.FRIDAY, fridayBudgetStates)
        budgetStateDailyItinerary.put(DayOfWeek.SATURDAY, saturdayBudgetStates)
        budgetStateDailyItinerary.put(DayOfWeek.SUNDAY, sundayBudgetStates)
        return budgetStateDailyItinerary
    }

    private fun processCurrentWeekFutureBudgetItems(beginningOfTimePeriod: LocalDateTime,
                                                    endOfTimePeriod: LocalDateTime): MutableMap<String, BudgetItem>? {
        var newBudgetItems: MutableMap<String, BudgetItem>? = HashMap()
        if (applicationState.futureBudgetItems != null) {
            var futureBudgetItemsEntryIterator = applicationState.futureBudgetItems?.entries?.iterator()
            while (futureBudgetItemsEntryIterator?.hasNext()!!) {
                var futureBudgetItemEntry = futureBudgetItemsEntryIterator?.next()
                var futureBudgetItem = futureBudgetItemEntry?.value?.copy()
                if (futureBudgetItem != null) {
                    if (futureBudgetItem.validForWeek(beginningOfTimePeriod, endOfTimePeriod)) {
                        applicationState.currentPayPeriodBudgetState?.currentBudgetItems?.put(futureBudgetItem.name, futureBudgetItem.copy())
                        val nextDueDate: LocalDateTime = determineNextDueLocalDateTime(futureBudgetItem.recurrence,
                                futureBudgetItem.due)
                        futureBudgetItem = futureBudgetItem.copy(due = nextDueDate)
                        newBudgetItems?.put(futureBudgetItem.name, futureBudgetItem)
                    }
                }

            }
        }
        return newBudgetItems
    }

    companion object {

        fun processFutureBudgetItemsIntoCurrentItems(applicationStateBudgetAnalysis: ApplicationStateBudgetAnalysis,
                                                     currentDateTime: LocalDateTime): MutableMap<String, BudgetItem>? {
            var newBudgetItems: MutableMap<String, BudgetItem>? = HashMap()
            if (applicationStateBudgetAnalysis.applicationState.futureBudgetItems != null) {
                var beginningOfTimePeriod: LocalDateTime? =
                        determinePreviousDueLocalDateTime(currentDateTime.dayOfWeek, currentDateTime)
                if (beginningOfTimePeriod != null) {
                    beginningOfTimePeriod.with(LocalTime.of(8, 0, 0))
                    var endOfTimePeriod: LocalDateTime = beginningOfTimePeriod.plusDays(7).minusSeconds(1)
                    newBudgetItems = applicationStateBudgetAnalysis.
                            processCurrentWeekFutureBudgetItems(beginningOfTimePeriod, endOfTimePeriod)
                }
            }
            return newBudgetItems
        }
    }
}