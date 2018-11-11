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
import view.screens.BaseScreen

class SavingsAccountPanel(width: Int, height: Int, component: Component, inputPanel: BaseScreen,
                          applicationState: ApplicationState) :
                                BaseItemsPanel(width, height, component, inputPanel, applicationState) {

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
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel(it)
        }
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    fun update(budgetAnalysisState: BudgetAnalysisState){
        var balances = budgetAnalysisState.savingsAccountBalances!!
        super.update()
        for (balanceIndex in 0 .. balances.size-1){
            val savingsAccount: Account = applicationState.savingsAccounts!!.get(balanceIndex)
            val balance:Double = balances.get(balanceIndex)
            radioButtonGroup?.addOption(savingsAccount.name, savingsAccount.toString(balance))
        }
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel(it)
        }
    }

    override fun update() {
        super.update()
        applicationState.savingsAccounts!!.forEach { account ->
            radioButtonGroup!!.addOption(account.name, account.toString())}
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel(it)
        }
    }

    private fun updateInputPanel(selection: RadioButtonGroup.Selection) {
        var inputPanel = parent.inputPanel
        var savingsAccount: Account? = null
        applicationState.savingsAccounts!!.forEach { account ->
            if(account.name.equals(selection.key)){
                savingsAccount = account
            }
        }
        var newPanel: Panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withTitle(String.format("Savings Account: %s",savingsAccount!!.name))
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
                .withText(String.format("%.2f",savingsAccount!!.balance))
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
            savingsAccount!!.balance = balanceTextArea.text.toDouble()
            parent.update()
            parent.clearInputPanel()
        }
        newPanel.addComponent(submitButton)
        val clearButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Clear Account Items")
                .withPosition(Positions.create(0,1).relativeToBottomOf(submitButton))
                .build()
        clearButton.onMouseReleased {
            savingsAccount!!.reconciledItems.clear()
            parent.update()
            parent.clearInputPanel()
        }
        newPanel.addComponent(clearButton)
        parent.updateInputPanel(newPanel)
    }

    companion object {
        val SAVINGS_ACCOUNTS_TITLE: String = "Savings Accounts"
    }
}