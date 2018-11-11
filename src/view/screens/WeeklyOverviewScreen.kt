package view.screens

import model.BudgetAnalysisState
import model.BudgetItem
import model.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.Screens
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.grid.TileGrid
import view.accounts.AccountsPanel
import view.budgetState.BudgetStatePanel
import view.control.MainControlsPanel
import view.input.InputPanel
import view.items.ItemsPanel
import kotlin.math.max
import kotlin.math.min

class WeeklyOverviewScreen(width: Int, height: Int, tileGrid: TileGrid, var uiComponents: ApplicationUIComponents) :
                        BaseScreen(width, height, tileGrid, uiComponents){

    var mainControlPanel: MainControlsPanel? = null
    var budgetStatePanel: BudgetStatePanel? = null
    var accountsPanel: AccountsPanel? = null
    var itemsPanel: ItemsPanel? = null
    var budgetStateIndex = 0
    var currentViewedBudgetState: BudgetState? = null

    override fun build() {
        screen = Screens.createScreenFor(tileGrid)
        currentViewedBudgetState = uiComponents.applicationState!!.currentPayPeriodBudgetState

        val fifthScreenWidth: Int = width/5
        val sixthScreenHeight: Int = height/6+2

        mainControlPanel = MainControlsPanel(width-2, 6, uiComponents)
        mainControlPanel!!.build()
        inputPanel = InputPanel(width-2, sixthScreenHeight-4, 1,
                height-(sixthScreenHeight-4),this, uiComponents.applicationState!!)
        inputPanel!!.build()

        budgetStatePanel = BudgetStatePanel(fifthScreenWidth * 3+1,
                sixthScreenHeight * 2, this, mainControlPanel!!.panel!!, uiComponents!!.applicationState!!)
        budgetStatePanel!!.build()

        accountsPanel = AccountsPanel(fifthScreenWidth * 2, sixthScreenHeight * 2,
                budgetStatePanel!!.panel!!, this, uiComponents.applicationState!!)
        accountsPanel!!.build()

        itemsPanel = ItemsPanel(width-2, sixthScreenHeight*3-12,
                budgetStatePanel!!.panel!!, this, uiComponents.applicationState!!)
        itemsPanel!!.build()

        screen!!.addComponent(mainControlPanel!!.panel!!)
        screen!!.addComponent(budgetStatePanel!!.panel!!)
        screen!!.addComponent(accountsPanel!!.panel!!)
        screen!!.addComponent(itemsPanel!!.panel!!)
        screen!!.addComponent(inputPanel!!.panel!!)
        screen!!.applyColorTheme(ColorThemes.monokaiBlue())
    }


    override fun clear() {
        screen?.clear()
    }

    override fun display() {
        screen?.display()
    }

    override fun update(): BudgetState {
        var budgetState = currentViewedBudgetState
        var currentBudgetAnalysisStates: MutableList<BudgetAnalysisState>? = null
        when(budgetStateIndex) {
            0 -> currentBudgetAnalysisStates =
                    uiComponents.applicationStateBudgetAnalysis?.performBudgetAnalysis(false)
            else -> {
                currentBudgetAnalysisStates =
                        currentViewedBudgetState?.let {
                            uiComponents.applicationStateBudgetAnalysis?.performBudgetAnalysis(it)
                        }
            }
        }
        budgetStatePanel?.update(currentBudgetAnalysisStates)
        accountsPanel?.update(currentBudgetAnalysisStates?.first()!!)
        itemsPanel?.update()
        return budgetState!!
    }

    override fun updateInputPanel(panel: Panel){
        inputPanel?.update(panel)
    }

    override fun clearInputPanel(){
        inputPanel?.clear()
    }

    override fun projectBalances(){
        var budgetItems: MutableMap<String, BudgetItem> = HashMap()
        currentViewedBudgetState?.currentBudgetItems?.let { budgetItems.putAll(it) }
        uiComponents.applicationState?.pastUnreconciledBudgetItems?.let { budgetItems.putAll(it) }
        var analysisStates: MutableList<BudgetAnalysisState>? =
                uiComponents.applicationStateBudgetAnalysis?.performAnalysisOnBudgetItems(budgetItems)
        var lastAnalysisState: BudgetAnalysisState =
                analysisStates?.sortedWith(kotlin.comparisons.compareBy({ it.date }))!!.last()
        accountsPanel?.update(lastAnalysisState)
    }

    override fun currentBalances() {
        accountsPanel?.update()
    }

    override fun prevBudgetState() {
        budgetStateIndex = max(0,budgetStateIndex-1)
        updateBudgetState()
    }

    override fun nextBudgetState() {
        budgetStateIndex = min(uiComponents.applicationState?.futureBudgetStates?.size!! +1,budgetStateIndex+1)
        updateBudgetState()
    }

    private fun updateBudgetState() {
        when(budgetStateIndex){
            0 -> currentViewedBudgetState = uiComponents.applicationState?.currentPayPeriodBudgetState
            else -> {
                currentViewedBudgetState = uiComponents.applicationState?.futureBudgetStates?.get(budgetStateIndex-1)
            }
        }
        update()
    }

}