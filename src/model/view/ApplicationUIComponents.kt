package model.view

import control.ApplicationStateBudgetAnalysis
import model.ApplicationState
import model.BudgetAnalysisState
import model.BudgetItem
import model.BudgetState
import model.enums.View
import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.screen.Screen
import view.accounts.AccountsPanel
import view.budgetState.BudgetStatePanel
import view.input.InputPanel
import view.items.ItemsPanel
import view.screens.BaseScreen
import view.screens.BudgetScreen
import view.screens.WeeklyOverviewScreen
import kotlin.math.max
import kotlin.math.min

class ApplicationUIComponents{

    var weeklyOverviewScreen: WeeklyOverviewScreen? = null
    var budgetViewScreen:BudgetScreen? = null
    var currentScreen: BaseScreen? = null

    var applicationState: ApplicationState? = null
    var applicationStateBudgetAnalysis: ApplicationStateBudgetAnalysis? = null
    var currentViewedBudgetState: BudgetState? = null

    fun build() {
        applicationStateBudgetAnalysis = ApplicationStateBudgetAnalysis(applicationState!!)
        currentViewedBudgetState = applicationState!!.currentPayPeriodBudgetState
        val fullScreenSize: Size = Size.create(WIDTH, HEIGHT)
        val tileGrid = SwingApplications.startTileGrid(
                AppConfigs.newConfig()
                        .withSize(fullScreenSize)
                        .withDefaultTileset(CP437TilesetResources.rexPaint10x10())
                        .build())
        weeklyOverviewScreen = WeeklyOverviewScreen(fullScreenSize.width, fullScreenSize.height, tileGrid, this)
        weeklyOverviewScreen?.build()
        budgetViewScreen = BudgetScreen(fullScreenSize.width, fullScreenSize.height, tileGrid, this)
        budgetViewScreen?.build()
        currentScreen = weeklyOverviewScreen

        currentScreen!!.screen.display()
    }

    fun switchScreen(view: View): Screen? {
        when(view){
            View.WEEKLY -> currentScreen = weeklyOverviewScreen
            View.BUDGET -> currentScreen = budgetViewScreen
        }
        currentScreen?.screen?.display()
        return this.currentScreen?.screen
    }

    fun clear() {
        currentScreen?.clear()
    }

    fun update(): BudgetState? {
        return currentScreen?.update()
    }

    fun display() {
        currentScreen?.display()
    }

    companion object {
        val WIDTH: Int = 168
        val HEIGHT: Int = 100
    }
}