package view.accounts

import model.state.ApplicationState
import model.budget.BudgetAnalysisState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import org.hexworks.zircon.api.kotlin.onSelection

class CheckingAccountPanel(var width: Int, var height: Int, var component: Component,
                           var uiComponents: ApplicationUIComponents, var applicationState: ApplicationState) {

    var radioButtonGroup: RadioButtonGroup? = null
    var panel: Panel? = null

    fun update(budgetAnalysisState: BudgetAnalysisState){
        var balance: Double = budgetAnalysisState.checkingAccountBalance!!
        update(balance)
    }

     fun update(balance: Double){
         val balanceString = String.format("                                Balance: %.2f", balance)
         this.panel!!.removeComponent(this!!.radioButtonGroup!!)
         radioButtonGroup = Components.radioButtonGroup()
                 .withPosition(Position.create(0,0))
                 .withSize(Sizes.create(this.width-2, 1))
                 .build()
         this.panel!!.addComponent(radioButtonGroup!!)
         radioButtonGroup!!.addOption("CheckingAccount", balanceString)
         radioButtonGroup!!.onSelection { updateCheckingAccount() }
     }

    fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(CHECKING_ACCOUNT_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, 3))
                .withPosition(AccountsPanel.DEFAULT_OFFSET.relativeToBottomOf(component))
                .build()
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(0,0))
                .withSize(Sizes.create(this.width-4, 1))
                .build()
        radioButtonGroup!!.addOption("CheckingAccount","                                Balance: 0.00")
        radioButtonGroup!!.onSelection { updateCheckingAccount() }
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    private fun updateCheckingAccount() {
        var inputPanel = uiComponents.weeklyOverviewScreen!!.inputPanel
        var newPanel: Panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withTitle("Checking Account")
                .withSize(Sizes.create(inputPanel!!.width-4, 3))
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
            checkingAccount!!.reconciledItems.clear()
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        newPanel.addComponent(clearButton)
        uiComponents.updateInputScreen(newPanel)
    }

    companion object {
        val CHECKING_ACCOUNT_TITLE: String = "Checking Account"
    }
}