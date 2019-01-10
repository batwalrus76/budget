package model.view

import control.ApplicationStateBudgetAnalysis
import control.ApplicationStateManager
import model.budget.BudgetAnalysisState
import model.state.ApplicationState
import model.budget.BudgetState
import model.enums.View
import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.screen.Screen
import view.control.MainControlsPanel
import view.screens.*
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min

class ApplicationUIComponents(var applicationStateBudgetAnalysis: ApplicationStateBudgetAnalysis,
                              var applicationState: ApplicationState){

    var weeklyOverviewScreen: WeeklyOverviewScreen? = null
    var yearPayPeriodBalancesScreen: YearPayPeriodBalancesScreen? = null
    var budgetViewScreen:BudgetScreen? = null
    var calendarDayScreen: CalendarDayScreen? = null
    var calendarWeekScreen: CalendarWeekScreen? = null
    var calendarMonthScreen: CalendarMonthScreen? = null
    var calendarYearScreen: CalendarYearScreen? = null
    var currentView: View = View.WEEKLY
    var currentViewedBudgetState: BudgetState? = null
    var budgetStateIndex = 0
    var currentLocalDate = LocalDate.now()
    private val fullScreenSize: Size = Size.create(WIDTH, HEIGHT)
    var tileGrid = SwingApplications.startTileGrid(
            AppConfigs.newConfig()
                    .withSize(fullScreenSize)
                    .withDefaultTileset(CP437TilesetResources.rexPaint8x8())
                    .build())
    var screen: Screen? = Screens.createScreenFor(tileGrid)
    var mainControlPanel: MainControlsPanel? = null
    var currentMainComponent: Component? = null
    var budgetAnalysis = applicationStateBudgetAnalysis?.performBudgetAnalysis()


    fun build() {
        currentViewedBudgetState = applicationState!!.currentPayPeriodBudgetState

        mainControlPanel = MainControlsPanel(fullScreenSize.width, 4, this)
        mainControlPanel?.build()
        mainControlPanel?.panel?.let { screen?.addComponent(it) }

        weeklyOverviewScreen = WeeklyOverviewScreen(fullScreenSize.width, fullScreenSize.height-4,
                mainControlPanel?.panel!!,this)

        budgetViewScreen = BudgetScreen(fullScreenSize.width, fullScreenSize.height-4,
                mainControlPanel?.panel!!, this)
        budgetViewScreen?.build()

        weeklyOverviewScreen?.build()
        weeklyOverviewScreen?.update()

        yearPayPeriodBalancesScreen = YearPayPeriodBalancesScreen(fullScreenSize.width, fullScreenSize.height-4,
                mainControlPanel?.panel!!,this, budgetAnalysis)
        yearPayPeriodBalancesScreen!!.build()

        calendarDayScreen = CalendarDayScreen(fullScreenSize.width, fullScreenSize.height-4,
                                mainControlPanel?.panel!!,this)
        calendarDayScreen!!.build()

        calendarWeekScreen = CalendarWeekScreen(fullScreenSize.width, fullScreenSize.height-4,
                mainControlPanel?.panel!!,this)
        calendarWeekScreen!!.build()

        calendarMonthScreen = CalendarMonthScreen(fullScreenSize.width, fullScreenSize.height-4,
                mainControlPanel?.panel!!,this)
        calendarMonthScreen!!.build()

        calendarYearScreen = CalendarYearScreen(fullScreenSize.width, fullScreenSize.height-4,
                mainControlPanel?.panel!!,this)
        calendarYearScreen!!.build()

        currentMainComponent = weeklyOverviewScreen!!.panel
        weeklyOverviewScreen?.panel?.let { screen?.addComponent(it) }
        screen!!.applyColorTheme(ColorThemes.monokaiBlue())
        screen?.display()
    }

    fun switchScreen(view: View): Screen? {
        when(view){
            View.WEEKLY -> {
                tileGrid.clear()
                screen = Screens.createScreenFor(tileGrid)
                currentView = View.WEEKLY
                currentMainComponent?.let{ screen?.removeComponent(it)}
                weeklyOverviewScreen?.build()
                weeklyOverviewScreen?.update()
                currentMainComponent = weeklyOverviewScreen?.panel
                mainControlPanel?.panel?.let { screen?.addComponent(it) }
                weeklyOverviewScreen?.panel?.let { screen?.addComponent(it) }
                screen!!.display()
            }
            View.BUDGET -> {
                tileGrid.clear()
                screen = Screens.createScreenFor(tileGrid)
                currentView = View.BUDGET
                currentMainComponent?.let{ screen?.removeComponent(it)}
                budgetViewScreen?.build()
                budgetViewScreen?.update()
                currentMainComponent = budgetViewScreen?.panel
                mainControlPanel?.panel?.let { screen?.addComponent(it) }
                budgetViewScreen?.panel?.let { screen?.addComponent(it) }
                screen!!.display()
            }
            View.YEAR -> {
                tileGrid.clear()
                screen = Screens.createScreenFor(tileGrid)
                currentView = View.YEAR
                currentMainComponent?.let{ screen?.removeComponent(it)}
                yearPayPeriodBalancesScreen?.update()
                currentMainComponent = yearPayPeriodBalancesScreen?.panel
                mainControlPanel?.panel?.let { screen?.addComponent(it) }
                yearPayPeriodBalancesScreen?.panel?.let { screen?.addComponent(it) }
                screen!!.display()
            }
            View.CALENDAR_DAY -> {
                tileGrid.clear()
                screen = Screens.createScreenFor(tileGrid)
                currentView = View.CALENDAR_DAY
                currentMainComponent?.let{ screen?.removeComponent(it)}
                var appropriateBudgetAnalysisStates = findBudgetAnalysisStateForLocalDate(currentLocalDate)
                appropriateBudgetAnalysisStates?.let { calendarDayScreen?.update(currentLocalDate, it) }
                currentMainComponent = calendarDayScreen?.panel
                mainControlPanel?.panel?.let { screen?.addComponent(it) }
                calendarDayScreen?.panel?.let { screen?.addComponent(it) }
                screen!!.display()
            }
            View.CALENDAR_WEEK -> {
                tileGrid.clear()
                screen = Screens.createScreenFor(tileGrid)
                currentView = View.CALENDAR_WEEK
                currentMainComponent?.let{ screen?.removeComponent(it)}
                calendarWeekScreen?.update(currentLocalDate)
                currentMainComponent = calendarWeekScreen?.panel
                mainControlPanel?.panel?.let { screen?.addComponent(it) }
                calendarWeekScreen?.panel?.let { screen?.addComponent(it) }
                screen!!.display()
            }
            View.CALENDAR_MONTH -> {
                tileGrid.clear()
                screen = Screens.createScreenFor(tileGrid)
                currentView = View.CALENDAR_MONTH
                currentMainComponent?.let{ screen?.removeComponent(it)}
                calendarMonthScreen?.update(currentLocalDate)
                currentMainComponent = calendarMonthScreen?.panel
                mainControlPanel?.panel?.let { screen?.addComponent(it) }
                calendarMonthScreen?.panel?.let { screen?.addComponent(it) }
                screen!!.display()
            }
            View.CALENDAR_YEAR -> {
                tileGrid.clear()
                screen = Screens.createScreenFor(tileGrid)
                currentView = View.CALENDAR_YEAR
                currentMainComponent?.let{ screen?.removeComponent(it)}
                calendarYearScreen?.update(currentLocalDate)
                currentMainComponent = calendarYearScreen?.panel
                mainControlPanel?.panel?.let { screen?.addComponent(it) }
                calendarYearScreen?.panel?.let { screen?.addComponent(it) }
                screen!!.display()
            }
        }
        return this.screen
    }

    fun prevBudgetState() {
        budgetStateIndex = max(0,budgetStateIndex-1)
        updateBudgetState()
    }

    fun nextBudgetState() {
        budgetStateIndex = min(applicationState?.futureBudgetStates?.size!! +1,budgetStateIndex+1)
        updateBudgetState()
    }

    private fun updateBudgetState() {
        ApplicationStateManager.serializeToDefaultJsonFileLocation(applicationState)
        when(budgetStateIndex){
            0 -> currentViewedBudgetState = applicationState?.currentPayPeriodBudgetState
            else -> {
                currentViewedBudgetState = applicationState?.futureBudgetStates?.get(budgetStateIndex-1)
            }
        }
        currentViewedBudgetState = currentViewedBudgetState
        update()
    }

    fun clear() {
        screen?.clear()
    }

    fun update(): BudgetState? {
        ApplicationStateManager.serializeToDefaultJsonFileLocation(applicationState)
        when(currentView) {
            View.WEEKLY -> {
                weeklyOverviewScreen?.update()
            }
            View.BUDGET -> {
                budgetViewScreen?.update()
            }
            View.YEAR -> {
                yearPayPeriodBalancesScreen?.update()
            }
            View.CALENDAR_DAY -> {
                budgetAnalysis[currentViewedBudgetState]?.let { calendarDayScreen?.update(currentLocalDate, it) }
            }
            View.CALENDAR_WEEK -> {
                calendarWeekScreen?.update(currentLocalDate)
            }
            View.CALENDAR_MONTH -> {
                calendarMonthScreen?.update(currentLocalDate)
            }
            View.CALENDAR_YEAR -> {
                calendarYearScreen?.update(currentLocalDate)
            }
        }
        return applicationState?.currentPayPeriodBudgetState
    }

    fun clearInputScreen() {
        weeklyOverviewScreen!!.clearInputPanel()
    }

    fun updateInputScreen(panel: Panel) {
        weeklyOverviewScreen!!.updateInputPanel(panel)
    }

    fun display() {
        screen?.display()
    }

    open fun findBudgetAnalysisStateForLocalDate(localDate: LocalDate):  MutableList<BudgetAnalysisState>? {
        var budgetAnalysisStates = applicationStateBudgetAnalysis?.performBudgetAnalysis()
        var localDateBudgetState = applicationState.findBudgetStateForLocalDate(localDate)
        var budgetStateAnalysisStates =  budgetAnalysisStates[localDateBudgetState]
        var applicableAnalysisStates = ArrayList<BudgetAnalysisState>()
        budgetStateAnalysisStates?.forEach { budgetAnalysisState ->
            if(budgetAnalysisState.date!!.equals(localDate)){
                applicableAnalysisStates.add(budgetAnalysisState)
            }
        }
        return applicableAnalysisStates
    }


    companion object {
        val WIDTH: Int = 210
        val HEIGHT: Int = 124
    }
}