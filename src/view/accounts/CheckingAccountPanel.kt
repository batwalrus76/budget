package view.accounts

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

class CheckingAccountPanel(width: Int, height: Int, component: Component, parent: BaseScreen,
                           applicationState: ApplicationState) :
        BaseItemsPanel(width, height, component, parent, applicationState) {

    fun update(budgetAnalysisState: BudgetAnalysisState){
        var balance: Double = budgetAnalysisState.checkingAccountBalance!!
        update(balance)
    }

     fun update(balance: Double){
         val balanceString = String.format("Balance: %.2f", balance)
         super.update()
         radioButtonGroup!!.addOption("CheckingAccount", balanceString)
         radioButtonGroup!!.onSelection { updateCheckingAccount() }
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
        radioButtonGroup!!.onSelection { updateCheckingAccount() }
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    private fun updateCheckingAccount() {
        var inputPanel = parent.inputPanel
        var newPanel: Panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withTitle("Checking Account")
                .withSize(Sizes.create(inputPanel!!.width-4, inputPanel!!.height-4))
                .withPosition(Positions.offset1x1())
                .build()
        var checkingAccount = applicationState.checkingAccount
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
                .withText(String.format("%.2f",checkingAccount!!.balance))
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
            checkingAccount!!.balance = balanceTextArea.text.toDouble()
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
            checkingAccount!!.reconciledItems.clear()
            parent.update()
            parent.clearInputPanel()
        }
        newPanel.addComponent(clearButton)
        parent.updateInputPanel(newPanel)
    }

    companion object {
        val CHECKING_ACCOUNT_TITLE: String = "Checking Account"
    }
}