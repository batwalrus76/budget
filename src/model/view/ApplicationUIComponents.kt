package model.view

import control.ApplicationStateBudgetAnalysis
import model.ApplicationState
import model.BudgetState
import model.enums.View
import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.screen.Screen
import view.control.MainControlsPanel
import view.screens.BudgetScreen
import view.screens.WeeklyOverviewScreen
import kotlin.math.max
import kotlin.math.min

class ApplicationUIComponents{

    var weeklyOverviewScreen: WeeklyOverviewScreen? = null
    var budgetViewScreen:BudgetScreen? = null
    var currentView: View = View.WEEKLY

    var applicationState: ApplicationState? = null
    var applicationStateBudgetAnalysis: ApplicationStateBudgetAnalysis? = null
    var currentViewedBudgetState: BudgetState? = null
    var budgetStateIndex = 0
    private val fullScreenSize: Size = Size.create(WIDTH, HEIGHT)
    var tileGrid = SwingApplications.startTileGrid(
            AppConfigs.newConfig()
                    .withSize(fullScreenSize)
                    .withDefaultTileset(CP437TilesetResources.rexPaint10x10())
                    .build())
    var screen: Screen? = Screens.createScreenFor(tileGrid)
    var mainControlPanel: MainControlsPanel? = null



    fun build() {
        applicationStateBudgetAnalysis = ApplicationStateBudgetAnalysis(applicationState!!)
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
        weeklyOverviewScreen?.panel?.let { screen?.addComponent(it) }
        screen!!.applyColorTheme(ColorThemes.monokaiBlue())
        screen?.display()
    }

    fun switchScreen(view: View): Screen? {
        when(view){
            View.WEEKLY -> {
                if(currentView == View.BUDGET){
                    tileGrid.clear()
                    screen = Screens.createScreenFor(tileGrid)
                    currentView = View.WEEKLY
                    budgetViewScreen?.panel?.let { screen?.removeComponent(it) }
                    weeklyOverviewScreen?.build()
                    weeklyOverviewScreen?.update()
                    mainControlPanel?.panel?.let { screen?.addComponent(it) }
                    weeklyOverviewScreen?.panel?.let { screen?.addComponent(it) }
                    screen!!.display()
                }
            }
            View.BUDGET -> {
                if(currentView == View.WEEKLY){
                    tileGrid.clear()
                    screen = Screens.createScreenFor(tileGrid)
                    currentView = View.BUDGET
                    weeklyOverviewScreen?.panel?.let { screen?.removeComponent(it) }
                    budgetViewScreen?.build()
                    budgetViewScreen?.update()
                    mainControlPanel?.panel?.let { screen?.addComponent(it) }
                    budgetViewScreen?.panel?.let { screen?.addComponent(it) }
                    screen!!.display()
                }
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
        when(currentView) {
            View.WEEKLY -> {
                weeklyOverviewScreen?.update()
            }
            View.BUDGET -> {
                budgetViewScreen?.update()
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

    companion object {
        val WIDTH: Int = 166
        val HEIGHT: Int = 100
    }
}