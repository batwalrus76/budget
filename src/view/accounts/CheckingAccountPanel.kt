package view.accounts

import model.ApplicationState
import model.BudgetAnalysisState
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.items.BaseItemsPanel

class CheckingAccountPanel(width: Int,  height: Int, component: Component, applicationState: ApplicationState) :
        BaseItemsPanel(width, height, component, applicationState) {

    fun update(budgetAnalysisState: BudgetAnalysisState){
        var balance: Double = budgetAnalysisState.checkingAccountBalance!!
        val balanceString = String.format("Balance: %.2f", balance)
        super.update()
        radioButtonGroup!!.addOption("CheckingAccount", balanceString)
    }

    override fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(CHECKING_ACCOUNT_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, height))
                .withPosition(AccountsPanel.DEFAULT_OFFSET.relativeToBottomOf(component))
                .build()
        super.build()
        radioButtonGroup!!.addOption("CheckingAccount","Balance: 0.00")
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    companion object {
        val CHECKING_ACCOUNT_TITLE: String = "Checking Account"
    }
}