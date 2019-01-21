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
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.screen.Screen
import view.control.MainControlsPanel
import view.control.SupplementaryControlPanel
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
    var currentScreen: BaseScreen? = null
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
    var supplementaryControlPanel: SupplementaryControlPanel? = null
    var currentMainComponent: Component? = null
    var budgetAnalysis = applicationStateBudgetAnalysis?.performBudgetAnalysis()
    var supplementaryControlPanelPosition: Position? = null

    fun build() {
        currentViewedBudgetState = applicationState!!.currentPayPeriodBudgetState

        mainControlPanel = MainControlsPanel(fullScreenSize.width/2, 3, this)
        mainControlPanel?.build()
        mainControlPanel?.panel?.let { screen?.addComponent(it) }

        supplementaryControlPanelPosition = Positions.create(0,0).relativeToRightOf(mainControlPanel!!.panel!!)
        supplementaryControlPanel = SupplementaryControlPanel(fullScreenSize.width/2, 3,
                this, supplementaryControlPanelPosition!!)
        supplementaryControlPanel!!.build()
        supplementaryControlPanel?.panel?.let { screen?.addComponent(it) }

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

        calendarWeekScreen = CalendarWeekScreen(fullScreenSize.width-1, fullScreenSize.height-4,
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
        currentView = view
        baseUIRefresh()
        when(view){
            View.WEEKLY -> {
                currentScreen = weeklyOverviewScreen
            }
            View.BUDGET -> {
                currentScreen = budgetViewScreen
            }
            View.YEAR -> {
                currentScreen = yearPayPeriodBalancesScreen
            }
            View.CALENDAR_DAY -> {
                currentScreen = calendarDayScreen
            }
            View.CALENDAR_WEEK -> {
                currentScreen = calendarWeekScreen
            }
            View.CALENDAR_MONTH -> {
                currentScreen = calendarMonthScreen
            }
            View.CALENDAR_YEAR -> {
                currentScreen = calendarYearScreen
            }
        }
        currentViewedBudgetState = applicationState.findBudgetStateForLocalDate(currentLocalDate)
        currentScreen?.update()
        currentMainComponent = currentScreen?.panel
        currentMainComponent?.let { screen?.addComponent(it) }
        screen!!.display()
        return this.screen
    }

    fun baseUIRefresh(){
        tileGrid.clear()
        screen = Screens.createScreenFor(tileGrid)
        currentMainComponent?.let{ screen?.removeComponent(it)}
        mainControlPanel?.panel?.let { screen?.addComponent(it) }
        supplementaryControlPanel?.panel?.let { screen?.addComponent(it) }
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
        currentLocalDate = currentViewedBudgetState?.startDate
        supplementaryControlPanel?.updateCurrentDateLabel()
        update()
    }

    fun clear() {
        screen?.clear()
    }

    open fun updateDate(newLocalDate: LocalDate, newView: View=currentView){
        this.currentLocalDate = newLocalDate
        switchScreen(newView)
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