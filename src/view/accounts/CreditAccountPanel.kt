package view.accounts

import model.Account
import model.ApplicationState
import model.BudgetAnalysisState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import org.hexworks.zircon.api.kotlin.onSelection
import view.items.BaseItemsPanel

class CreditAccountPanel(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents,
                            applicationState: ApplicationState) :
        BaseItemsPanel(width, height, component, uiComponents, applicationState) {

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
            updateInputPanel(it)
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
            updateInputPanel(it)
        }
    }

    override fun update() {
        super.update()
        applicationState.creditAccounts!!.forEach { name, account ->
            radioButtonGroup!!.addOption(name, account.toString())}
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel(it)
        }
    }


    private fun updateInputPanel(selection: RadioButtonGroup.Selection) {
        var inputPanel = uiComponents.weeklyOverviewScreen!!.inputPanel
        var creditAccount: Account? = null
        applicationState.creditAccounts!!.forEach { name, account ->
            if(name.equals(selection.key)){
                creditAccount = account
            }
        }
        var newPanel: Panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withTitle(String.format("Credit Account: %s",creditAccount!!.name))
                .withSize(Sizes.create(inputPanel!!.width-4, inputPanel!!.height-4))
                .withPosition(Positions.offset1x1())
                .build()
        val balanceLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Balance:")
                .withPosition(Positions.create(0,0))
                .build()
        newPanel.addComponent(balanceLabel)
        var balanceTextArea: TextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(String.format("%.2f",creditAccount!!.balance))
                .withSize(Sizes.create(10,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(balanceLabel))
                .build()
        newPanel.addComponent(balanceTextArea)

        val submitButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Update Budget Item")
                .withPosition(Positions.create(1,0).relativeToRightOf(balanceTextArea))
                .build()
        submitButton.onMouseReleased {
            mouseAction ->
            creditAccount!!.balance = balanceTextArea.text.toDouble()
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        newPanel.addComponent(submitButton)
        val clearButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Clear Account Items")
                .withPosition(Positions.create(0,1).relativeToBottomOf(submitButton))
                .build()
        clearButton.onMouseReleased {
            creditAccount!!.reconciledItems.clear()
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        newPanel.addComponent(clearButton)
        uiComponents.updateInputScreen(newPanel)
    }


    companion object {
        val CREDIT_ACCOUNTS_TITLE: String = "Credit Accounts"
    }
}