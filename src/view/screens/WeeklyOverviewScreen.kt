package view.screens

import model.budget.BudgetAnalysisState
import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import view.accounts.AccountsPanel
import view.budgetState.BudgetStatePanel
import view.input.InputPanel
import view.items.ItemsPanel

class WeeklyOverviewScreen(width: Int, height: Int, var component: Component, uiComponents: ApplicationUIComponents):
    BaseScreen(width, height, uiComponents){

    var budgetStatePanel: BudgetStatePanel? = null
    var accountsPanel: AccountsPanel? = null
    var itemsPanel: ItemsPanel? = null
    var currentViewedBudgetState: BudgetState? = null
    var inputPanel:InputPanel? = null
    private var budgetAnalysis: MutableMap<BudgetState?, MutableList<BudgetAnalysisState>>? = null

    override fun build() {
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .build()
        budgetAnalysis = uiComponents.applicationStateBudgetAnalysis?.performBudgetAnalysis()
        panel!!.children.forEach { child -> panel!!.removeComponent(child) }
        currentViewedBudgetState = uiComponents.applicationState!!.currentPayPeriodBudgetState

        val fifthScreenWidth: Int = width/5
        val fifthScreenHeight: Int = (height/5)

        inputPanel = InputPanel(width-2, fifthScreenHeight, 0,
                height-(fifthScreenHeight+2),uiComponents, uiComponents.applicationState!!)
        inputPanel!!.build()

        budgetStatePanel = BudgetStatePanel(fifthScreenWidth * 3+1,
                fifthScreenHeight * 2 - 6, uiComponents, uiComponents!!.applicationState!!)
        budgetStatePanel!!.build()

        accountsPanel = AccountsPanel((fifthScreenWidth * 2) + 1, fifthScreenHeight * 2 - 6,
                budgetStatePanel!!.panel!!, uiComponents, uiComponents.applicationState!!)
        accountsPanel!!.build()

        itemsPanel = ItemsPanel(width-2, fifthScreenHeight*2+3,
                budgetStatePanel!!.panel!!, uiComponents, uiComponents.applicationState!!)
        itemsPanel!!.build()

        panel!!.addComponent(budgetStatePanel!!.panel!!)
        panel!!.addComponent(accountsPanel!!.panel!!)
        panel!!.addComponent(itemsPanel!!.panel!!)
        panel!!.addComponent(inputPanel!!.panel!!)
    }

    override fun update(): BudgetState {
        currentViewedBudgetState = uiComponents.currentViewedBudgetState
        //budgetAnalysis = uiComponents.applicationStateBudgetAnalysis?.performBudgetAnalysis()
        val currentBudgetAnalysisStates = budgetAnalysis?.get(currentViewedBudgetState)
        budgetStatePanel?.update(currentBudgetAnalysisStates)
        if(currentBudgetAnalysisStates != null && currentBudgetAnalysisStates!!.isNotEmpty()) {
            accountsPanel?.update(currentBudgetAnalysisStates?.first()!!)
        }
        itemsPanel?.update()
        return currentViewedBudgetState!!
    }

    fun updateInputPanel(panel: Panel){
        inputPanel?.update(panel)
    }

    fun clearInputPanel(){
        inputPanel?.clear()
    }


    fun projectBalances(){
        val currentBudgetAnalysisStates = budgetAnalysis?.get(currentViewedBudgetState)
        var lastAnalysisState: BudgetAnalysisState =
                currentBudgetAnalysisStates?.sortedWith(kotlin.comparisons.compareBy({ it.date }))!!.last()
        accountsPanel?.update(lastAnalysisState)
    }

    fun currentBalances() {
        accountsPanel?.update()
    }

    companion object {
        const val TITLE = "Weekly Budget View"
    }

}