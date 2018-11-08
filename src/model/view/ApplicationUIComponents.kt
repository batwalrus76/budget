package model.view

import control.ApplicationStateBudgetAnalysis
import model.ApplicationState
import model.BudgetAnalysisState
import model.BudgetItem
import model.BudgetState
import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.screen.Screen
import view.accounts.AccountsPanel
import view.budgetState.BudgetStatePanel
import view.input.InputPanel
import view.items.ItemsPanel
import kotlin.math.max
import kotlin.math.min

class ApplicationUIComponents{

    var budgetStatePanel: BudgetStatePanel? = null
    var accountsPanel: AccountsPanel? = null
    var itemsPanel: ItemsPanel? = null
    var inputPanel: InputPanel? = null
    var screen: Screen? = null
    var applicationState: ApplicationState? = null
    var currentViewedBudgetState: BudgetState? = null
    var applicationStateBudgetAnalysis: ApplicationStateBudgetAnalysis? = null
    var budgetStateIndex = 0

    fun build(){
        applicationStateBudgetAnalysis = ApplicationStateBudgetAnalysis(applicationState!!)
        val fullScreenSize: Size = Size.create(160,98)
        val tileGrid = SwingApplications.startTileGrid(
                AppConfigs.newConfig()
                        .withSize(fullScreenSize)
                        .withDefaultTileset(CP437TilesetResources.rexPaint12x12())
                        .build())
        screen = Screens.createScreenFor(tileGrid)
        currentViewedBudgetState = applicationState!!.currentPayPeriodBudgetState

        val fifthScreenWidth: Int = fullScreenSize.width/5
        val sixthScreenHeight: Int = fullScreenSize.height/6+2

        inputPanel = InputPanel(fullScreenSize.width, sixthScreenHeight-4, 0,
                            fullScreenSize.height-(sixthScreenHeight-4), this, applicationState!!)
        inputPanel!!.build()

        budgetStatePanel = BudgetStatePanel(fifthScreenWidth * 3+1,
                            sixthScreenHeight * 2, this, this!!.applicationState!!)
        budgetStatePanel!!.build()

        accountsPanel = AccountsPanel(fifthScreenWidth*2 - 1, sixthScreenHeight * 2,
                                budgetStatePanel!!.panel!!, this, applicationState!!)
        accountsPanel!!.build()

        itemsPanel = ItemsPanel(fullScreenSize.width, sixthScreenHeight*3-8,
                                budgetStatePanel!!.panel!!, this, applicationState!!)
        itemsPanel!!.build()

        screen!!.addComponent(budgetStatePanel!!.panel!!)
        screen!!.addComponent(accountsPanel!!.panel!!)
        screen!!.addComponent(itemsPanel!!.panel!!)
        screen!!.addComponent(inputPanel!!.panel!!)

        // we can apply color themes to a screen
        screen!!.applyColorTheme(ColorThemes.monokaiBlue())
        // in order to see the changes you need to display your screen.
        screen!!.display()

    }

    fun clear() {
        screen?.clear()
    }

    fun display() {
        screen?.display()
    }

    fun update(): BudgetState {
        var budgetState = currentViewedBudgetState
        var currentBudgetAnalysisStates: MutableList<BudgetAnalysisState>? = null
        when(budgetStateIndex) {
            0 -> currentBudgetAnalysisStates = applicationStateBudgetAnalysis?.performBudgetAnalysis(false)
            else -> {
                currentBudgetAnalysisStates =
                        currentViewedBudgetState?.let { applicationStateBudgetAnalysis?.performBudgetAnalysis(it) }
            }
        }
        budgetStatePanel?.update(currentBudgetAnalysisStates)
        accountsPanel?.update(currentBudgetAnalysisStates?.first()!!)
        itemsPanel?.update()
        return budgetState!!
    }

    fun updateInputPanel(panel: Panel){
        inputPanel?.update(panel)
    }

    fun clearInputPanel(){
        inputPanel?.clear()
    }

    fun projectBalances(){
        var budgetItems: MutableMap<String, BudgetItem> = HashMap()
        currentViewedBudgetState?.currentBudgetItems?.let { budgetItems.putAll(it) }
        applicationState?.pastUnreconciledBudgetItems?.let { budgetItems.putAll(it) }
        var analysisStates: MutableList<BudgetAnalysisState>? = applicationStateBudgetAnalysis?.performAnalysisOnBudgetItems(budgetItems)
        var lastAnalysisState: BudgetAnalysisState = analysisStates?.sortedWith(kotlin.comparisons.compareBy({ it.date }))!!.last()
        accountsPanel?.update(lastAnalysisState)
    }

    fun currentBalances() {
        accountsPanel?.update()
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
        update()
    }
}