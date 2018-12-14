package view.accounts

import model.state.ApplicationState
import model.budget.BudgetAnalysisState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.kotlin.onSelection

class SavingsAccountPanel(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents,
                          applicationState: ApplicationState) :
                                BaseAccountsPanel(width, height, component, true, ADD_SAVINGS_ACCOUNT_TITLE,
                                        uiComponents, applicationState) {

    override fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(SAVINGS_ACCOUNTS_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, height))
                .withPosition(AccountsPanel.DEFAULT_OFFSET.relativeToBottomOf(component))
                .build()
        super.build()

        applicationState.savingsAccounts!!.forEach { name, account ->
            radioButtonGroup!!.addOption(name, account.toString())}
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel("Savings Account", applicationState.savingsAccounts!!, true, it)
        }
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    fun update(budgetAnalysisState: BudgetAnalysisState){
        super.update()
        applicationState.savingsAccounts?.forEach { name, account ->
            val budgetAnalysisCreditAccountBalance = budgetAnalysisState.savingsAccountBalances!![name]
            radioButtonGroup?.addOption(name, account.toString(budgetAnalysisCreditAccountBalance!!))
        }
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel("Savings Account", applicationState.savingsAccounts!!, true, it)
        }
    }

    override fun update() {
        super.update()
        applicationState.savingsAccounts!!.forEach { name, account ->
            radioButtonGroup!!.addOption(name, account.toString())}
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel("Savings Account", applicationState.savingsAccounts!!,true, it)
        }
    }

    companion object {
        val SAVINGS_ACCOUNTS_TITLE: String = "Savings Accounts"
        val ADD_SAVINGS_ACCOUNT_TITLE: String = "Add Savings Account: "
    }
}