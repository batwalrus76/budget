package view.financial.accounts

import model.enums.budget.AccountType
import model.representation.state.ApplicationState
import model.financial.budget.BudgetAnalysisState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.kotlin.onSelection

class CreditAccountPanel(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents,
                            applicationState: ApplicationState) :
        BaseAccountsPanel(width, height, component, false, ADD_CREDIT_ACCOUNT_TITLE,
                uiComponents, applicationState, AccountType.Credit) {

    override fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(CREDIT_ACCOUNTS_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, height))
                .withPosition(AccountsPanel.DEFAULT_OFFSET.relativeToBottomOf(component))
                .build()
        super.build()
        applicationState.creditAccounts!!.forEach { name, account ->
            radioButtonGroup!!.addOption(name, account.toString())}
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel("Credit Account", applicationState.creditAccounts!!,false, it)
        }
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    fun update(budgetAnalysisState: BudgetAnalysisState){
        super.update()
        applicationState.creditAccounts?.forEach { name, account ->
            val budgetAnalysisCreditAccountBalance = budgetAnalysisState.creditAccountBalances!![name]
            radioButtonGroup?.addOption(name, account.toString(budgetAnalysisCreditAccountBalance!!))
        }
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel("Credit Account", applicationState.creditAccounts!!,false, it)
        }
    }

    override fun update() {
        super.update()
        applicationState.creditAccounts!!.forEach { name, account ->
            radioButtonGroup!!.addOption(name, account.toString())}
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel("Credit Account", applicationState.creditAccounts!!,false, it)
        }
    }

    companion object {
        val CREDIT_ACCOUNTS_TITLE: String = "Credit Accounts"
        val ADD_CREDIT_ACCOUNT_TITLE: String = "Add Credit Account: "
    }
}