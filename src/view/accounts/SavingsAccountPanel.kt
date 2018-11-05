package view.accounts

import model.ApplicationState
import model.BudgetAnalysisState
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.budgetState.BudgetStatePanel
import view.items.BaseItemsPanel

class SavingsAccountPanel(width: Int,  height: Int, component: Component, applicationState: ApplicationState) :
                                BaseItemsPanel(width, height, component, applicationState) {

    override fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(SAVINGS_ACCOUNTS_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, height))
                .withPosition(AccountsPanel.DEFAULT_OFFSET.relativeToBottomOf(component))
                .build()
        super.build()
        applicationState.savingsAccounts!!.forEach { account ->
            radioButtonGroup!!.addOption(account.name, account.toString())}
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    override fun update() {
        super.update()
        applicationState.savingsAccounts!!.forEach { account ->
            radioButtonGroup!!.addOption(account.name, account.toString())}
    }

    companion object {
        val SAVINGS_ACCOUNTS_TITLE: String = "Savings Accounts"
    }
}