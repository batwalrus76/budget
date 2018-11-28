package view.accounts

import model.Account
import model.ApplicationState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased

abstract class BaseAccountsPanel(var width: Int, var height: Int, val component: Component, var isSavings: Boolean,
                                 var title: String, var uiComponents: ApplicationUIComponents,
                                 var applicationState: ApplicationState) {

    var radioButtonGroup: RadioButtonGroup? = null
    var panel: Panel? = null
    var addAccountButton: Button = Components.button()
            .withText("Add")
            .withBoxType(BoxType.LEFT_RIGHT_DOUBLE)
            .wrapSides(true)
            .withPosition(Position.create(width-10,0))
            .build()

    open fun addAccount() {
        var inputPanel = uiComponents.weeklyOverviewScreen!!.inputPanel
        var newPanel: Panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withTitle(title)
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
                .withText(String.format("%.2f",0.0))
                .withSize(Sizes.create(10,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(balanceLabel))
                .build()
        newPanel.addComponent(balanceTextArea)

        val nameLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Name:")
                .withPosition(Positions.create(10,0).relativeToRightOf(balanceTextArea))
                .build()
        newPanel.addComponent(nameLabel)
        var nameTextArea: TextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(20,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(nameLabel))
                .build()
        newPanel.addComponent(nameTextArea)

        val submitButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Create Account")
                .withPosition(Positions.create(1,0).relativeToRightOf(nameTextArea))
                .build()
        submitButton.onMouseReleased {
            mouseAction ->
            val balance = balanceTextArea.text.toDouble()
            val name = nameTextArea.text
            val account = Account(name, balance)
            if(isSavings){
                applicationState.addSavingsAccount(account)
            } else {
                applicationState.addCreditAccount(account)
            }
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        newPanel.addComponent(submitButton)
        uiComponents.updateInputScreen(newPanel)
    }


    open fun build() {
        this.panel!!.addComponent(addAccountButton)
        addAccountButton.onMouseReleased{addAccount()}
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(0, 1))
                .withSize(Sizes.create(this.width - 2, this.height - 3))
                .build()
    }

    open fun update() {
        this.panel!!.removeComponent(this!!.radioButtonGroup!!)
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(0, 1))
                .withSize(Sizes.create(this.width - 2, this.height - 3))
                .build()
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    fun updateInputPanel(title: String, accounts: MutableMap<String, Account>,
                                 isSavings: Boolean, selection: RadioButtonGroup.Selection) {
        var inputPanel = uiComponents.weeklyOverviewScreen!!.inputPanel
        var targetAccount: Account? = null
        accounts.forEach { name, account ->
            if(name.equals(selection.key)){
                targetAccount = account
            }
        }
        var newPanel: Panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withTitle(String.format("%s: %s", title, targetAccount!!.name))
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
                .withText(String.format("%.2f",targetAccount!!.balance))
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
            targetAccount!!.balance = balanceTextArea.text.toDouble()
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        newPanel.addComponent(submitButton)
        val deleteButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Delete Account")
                .withPosition(Positions.create(0,1).relativeToBottomOf(submitButton))
                .build()
        deleteButton.onMouseReleased {
            it ->
            if(isSavings) {
                applicationState.deleteSavingsAccount(targetAccount!!)
            } else {
                applicationState.deleteCreditAccount(targetAccount!!)
            }
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        newPanel.addComponent(deleteButton)
        val clearButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Clear Account Items")
                .withPosition(Positions.create(0,1).relativeToBottomOf(deleteButton))
                .build()
        clearButton.onMouseReleased {
            targetAccount!!.reconciledItems.clear()
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        newPanel.addComponent(clearButton)
        uiComponents.updateInputScreen(newPanel)
    }
}