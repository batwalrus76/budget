package model.view

import control.ApplicationStateBudgetAnalysis
import control.ApplicationStateManager
import control.PluginController.Companion.BUDGET_PLUGIN_AVAILABLE
import control.PluginController.Companion.TASK_PLUGIN_AVAILABLE
import control.calendar.CalendarParser
import model.calendar.CalendarEvent
import model.financial.budget.BudgetAnalysisState
import model.representation.state.ApplicationState
import model.financial.budget.BudgetState
import model.enums.view.View
import model.tasks.Task
import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.screen.Screen
import view.control.MainControlsPanel
import view.control.SupplementaryControlPanel
import view.screens.*
import view.screens.budget.BudgetScreen
import view.screens.budget.WeeklyOverviewScreen
import view.screens.budget.YearPayPeriodBalancesScreen
import view.screens.calendar.CalendarDayScreen
import view.screens.calendar.CalendarMonthScreen
import view.screens.calendar.CalendarWeekScreen
import view.screens.calendar.CalendarYearScreen
import view.screens.mixed.DailyMixedScreen
import view.screens.tasks.*
import java.time.LocalDate
import kotlin.math.max
import kotlin.math.min

class ApplicationUIComponents(var applicationStateBudgetAnalysis: ApplicationStateBudgetAnalysis,
                              var applicationState: ApplicationState){

    var weeklyOverviewScreen: WeeklyOverviewScreen? = null
    var yearPayPeriodBalancesScreen: YearPayPeriodBalancesScreen? = null
    var budgetViewScreen: BudgetScreen? = null
    var calendarDayScreen: CalendarDayScreen? = null
    var calendarWeekScreen: CalendarWeekScreen? = null
    var calendarMonthScreen: CalendarMonthScreen? = null
    var calendarYearScreen: CalendarYearScreen? = null
    var dailyTasksScreen: DailyTasksScreen? = null
    var weeklyTaskScreen: WeeklyTasksScreen? = null
    var listTaskScreen: ListTasksScreen? = null
    var projectTaskScreen: ProjectTaskScreen? = null
    var projectsTaskScreen: ProjectsTaskScreen? = null
    var dashboardTasksScreen: DashboardTaskScreen? = null
    var dailyMixedScreen: DailyMixedScreen? = null
    var currentScreen: BaseScreen? = null
    var currentView: View = View.BUDGET_WEEKLY
    var currentViewedBudgetState: BudgetState? = null
    var budgetStateIndex = 0
    var currentLocalDate = LocalDate.now()
    var currentProject = "Home"
    var currentTask: Task? = null
    private val fullScreenSize: Size = Size.create(WIDTH, HEIGHT)
    var screenHeight = fullScreenSize.height - 7
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

        buildControls()
        if(BUDGET_PLUGIN_AVAILABLE) {
            buildBudgetingScreens()
            buildCalendarScreens()
            currentMainComponent = weeklyOverviewScreen!!.panel
            weeklyOverviewScreen?.panel?.let { screen?.addComponent(it) }
        }
        if(TASK_PLUGIN_AVAILABLE) {
            buildTaskScreens()
            if(!BUDGET_PLUGIN_AVAILABLE) {
                currentMainComponent = listTaskScreen!!.panel
                listTaskScreen?.panel?.let { screen?.addComponent(it) }
            }
        }
        if(BUDGET_PLUGIN_AVAILABLE && TASK_PLUGIN_AVAILABLE) {
            buildMixedScreens()
        }

        screen!!.applyColorTheme(ColorThemes.monokaiBlue())
        screen?.display()
    }

    fun buildControls() {
        mainControlPanel = MainControlsPanel(fullScreenSize.width/2, 7, this)
        mainControlPanel?.build()
        mainControlPanel?.panel?.let { screen?.addComponent(it) }

        supplementaryControlPanelPosition = Positions.create(0,0).relativeToRightOf(mainControlPanel!!.panel!!)
        supplementaryControlPanel = SupplementaryControlPanel(fullScreenSize.width/2+1, 7,
                this, supplementaryControlPanelPosition!!)
        supplementaryControlPanel!!.build()
        supplementaryControlPanel?.panel?.let { screen?.addComponent(it) }
    }

    fun buildBudgetingScreens() {
        weeklyOverviewScreen = WeeklyOverviewScreen(fullScreenSize.width, screenHeight, mainControlPanel?.panel!!,
                this)

        budgetViewScreen = BudgetScreen(fullScreenSize.width, screenHeight, mainControlPanel?.panel!!, this)
        budgetViewScreen?.build()

        weeklyOverviewScreen?.build()
        weeklyOverviewScreen?.update()

        yearPayPeriodBalancesScreen = YearPayPeriodBalancesScreen(fullScreenSize.width, screenHeight,
                mainControlPanel?.panel!!, this, budgetAnalysis)
        yearPayPeriodBalancesScreen!!.build()
    }

    fun buildCalendarScreens() {
        calendarDayScreen = CalendarDayScreen(fullScreenSize.width, screenHeight, mainControlPanel?.panel!!,
                this)
        calendarDayScreen!!.build()

        calendarWeekScreen = CalendarWeekScreen(fullScreenSize.width - 1, screenHeight,
                mainControlPanel?.panel!!, this)
        calendarWeekScreen!!.build()

        calendarMonthScreen = CalendarMonthScreen(fullScreenSize.width, screenHeight,
                mainControlPanel?.panel!!, this)
        calendarMonthScreen!!.build()

        calendarYearScreen = CalendarYearScreen(fullScreenSize.width, screenHeight,
                mainControlPanel?.panel!!, this)
        calendarYearScreen!!.build()
    }

    fun buildTaskScreens() {
        dailyTasksScreen = DailyTasksScreen(fullScreenSize.width, screenHeight,
                mainControlPanel?.panel!!, this)
        dailyTasksScreen!!.build()
        weeklyTaskScreen = WeeklyTasksScreen(fullScreenSize.width, screenHeight,
                mainControlPanel?.panel!!, this)
        weeklyTaskScreen!!.build()
        listTaskScreen = ListTasksScreen(fullScreenSize.width, screenHeight,
                mainControlPanel?.panel!!, this)
        listTaskScreen!!.build()
        projectTaskScreen = ProjectTaskScreen(fullScreenSize.width, screenHeight,
                mainControlPanel?.panel!!, this)
        projectTaskScreen!!.build()
        projectsTaskScreen = ProjectsTaskScreen(fullScreenSize.width, screenHeight,
                mainControlPanel?.panel!!, this)
        projectsTaskScreen!!.build()
        dashboardTasksScreen = DashboardTaskScreen(fullScreenSize.width, screenHeight,
                mainControlPanel?.panel!!, this)
        dashboardTasksScreen!!.build()
    }

    fun buildMixedScreens() {
        dailyMixedScreen = DailyMixedScreen(fullScreenSize.width, screenHeight,
                mainControlPanel?.panel!!, this)
        dailyMixedScreen!!.build()
    }

    fun switchScreen(view: View): Screen? {
        currentView = view
        baseUIRefresh()
        when(view){
            View.BUDGET_WEEKLY -> {
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
            View.TASK_DAILY -> {
                currentScreen = dailyTasksScreen
            }
            View.TASK_WEEKLY -> {
                currentScreen = weeklyTaskScreen
            }
            View.TASK_LIST -> {
                currentScreen = listTaskScreen
            }
            View.TASK_PROJECT -> {
                currentScreen = projectTaskScreen
            }
            View.TASK_PROJECTS -> {
                currentScreen = projectsTaskScreen
            }
            View.TASK_DASHBOARD -> {
                currentScreen = dashboardTasksScreen
            }
            View.MIXED_DAILY -> {
                currentScreen = dailyMixedScreen
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

    open fun updateDate(newLocalDate: LocalDate, newView: View =currentView){
        this.currentLocalDate = newLocalDate
        switchScreen(newView)
    }

    open fun updateCurrentProject(projectName: String, newView: View =currentView){
        this.currentProject = projectName
        switchScreen(newView)
    }

    fun update(): BudgetState? {
        ApplicationStateManager.serializeToDefaultJsonFileLocation(applicationState)
        when(currentView) {
            View.BUDGET_WEEKLY -> {
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
            View.TASK_DAILY -> {
                dailyTasksScreen?.update()
            }
            View.TASK_WEEKLY -> {
                weeklyTaskScreen?.update()
            }
            View.TASK_LIST -> {
                listTaskScreen?.update()
            }
            View.TASK_PROJECT -> {
                projectTaskScreen?.update()
            }
            View.TASK_PROJECTS -> {
                projectsTaskScreen?.update()
            }
            View.TASK_DASHBOARD -> {
                dashboardTasksScreen?.update()
            }
            View.MIXED_DAILY -> {
                dailyMixedScreen?.update()
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

    open fun getDateCalendarEvents(localDate: LocalDate):List<CalendarEvent>?{
        return applicationState.calendar?.let { CalendarParser.retrieveDateCalendarEvents(localDate, it) }
    }

    open fun findBudgetAnalysisStateForLocalDate(localDate: LocalDate):  MutableList<BudgetAnalysisState>? {
        var budgetAnalysisStates = applicationStateBudgetAnalysis?.performBudgetAnalysis()
        var localDateBudgetState = applicationState.findBudgetStateForLocalDate(localDate)
        var budgetStateAnalysisStates =  budgetAnalysisStates[localDateBudgetState]
        var applicableAnalysisStates = ArrayList<BudgetAnalysisState>()
        budgetStateAnalysisStates?.forEach { budgetAnalysisState ->
            if(budgetAnalysisState != null && budgetAnalysisState.date != null && budgetAnalysisState.date!!.equals(localDate)){
                applicableAnalysisStates.add(budgetAnalysisState)
            }
        }
        return applicableAnalysisStates
    }


    companion object {
        val WIDTH: Int = 239
        val HEIGHT: Int = 146
    }
}