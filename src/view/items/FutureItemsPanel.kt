package view.items

import model.AccountItem
import model.ApplicationState
import model.BudgetItem
import model.enums.Recurrence
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import org.hexworks.zircon.api.kotlin.onSelection
import view.screens.BaseScreen
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.min

class FutureItemsPanel (width: Int, height: Int, component: Component, parent: BaseScreen,
                                        applicationState: ApplicationState) :
        BaseItemsPanel(width, height, component, parent, applicationState){

    override fun build() {
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(FUTURE_ITEM_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, height))
                .withPosition(Positions.create(0,0).relativeToRightOf(this!!.component!!))
                .build()
        super.build()
        radioButtonGroup?.let { this.panel!!.addComponent(it) }
    }

    override fun update(){
        var futureBudgetItems = applicationState.futureBudgetItems!!.values.sortedWith(kotlin.comparisons.compareBy({ it.due }))
        super.update()
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel(it)
        }
        futureBudgetItems?.forEach { futureBudgetItem ->
            radioButtonGroup!!.addOption(futureBudgetItem.name, futureBudgetItem.toNarrowString())
        }
    }

    companion object {
        val FUTURE_ITEM_TITLE: String = "Future Items"
    }

    private fun updateInputPanel(selection: RadioButtonGroup.Selection) {
        var inputPanel = parent.inputPanel
        var newPanel: Panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(inputPanel!!.width-4, inputPanel!!.height-4))
                .withPosition(Positions.offset1x1())
                .build()
        val budgetItem = applicationState.futureBudgetItems!!.get(selection.key)
        val name: String = budgetItem!!.name
        val nameLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Name:")
                .withPosition(Positions.create(0,0))
                .build()
        newPanel.addComponent(nameLabel)
        var nameTextArea: TextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(name.substring(0, min(name.length,20)))
                .withSize(Sizes.create(20,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(nameLabel))
                .build()
        newPanel.addComponent(nameTextArea)
        val due: LocalDateTime = budgetItem!!.due
        val dueLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Due: ")
                .withPosition(Positions.create(0,1).relativeToBottomOf(nameLabel))
                .build()
        newPanel.addComponent(dueLabel)
        var dueTextArea: TextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(due.format(DateTimeFormatter.ISO_DATE))
                .withSize(Sizes.create(10,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(dueLabel))
                .build()
        newPanel.addComponent(dueTextArea)
        val scheduledAmount = budgetItem!!.scheduledAmount
        val scheduledAmountLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Scheduled Amount:")
                .withPosition(Positions.create(1,0).relativeToRightOf(nameTextArea))
                .build()
        newPanel.addComponent(scheduledAmountLabel)
        var scheduledAmountTextArea: TextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(String.format("%.2f",scheduledAmount))
                .withSize(Sizes.create(10,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(scheduledAmountLabel))
                .build()
        newPanel.addComponent(scheduledAmountTextArea)
        val actualAmountLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Actual Amount:   ")
                .withPosition(Positions.create(0,1).relativeToBottomOf(scheduledAmountLabel))
                .build()
        newPanel.addComponent(actualAmountLabel)
        val actualAmount = budgetItem!!.actualAmount
        var actualAmountTextArea: TextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(String.format("%.2f",actualAmount))
                .withSize(Sizes.create(10,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(actualAmountLabel))
                .build()
        newPanel.addComponent(actualAmountTextArea)
        val recurrenceLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Recurrence:")
                .withPosition(Positions.create(1,0).relativeToRightOf(scheduledAmountTextArea))
                .build()
        newPanel.addComponent(recurrenceLabel)
        var currentRecurrence = budgetItem!!.recurrence
        val recurrenceRadioButtonGroup: RadioButtonGroup = Components.radioButtonGroup()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(20, 10))
                .withPosition(Positions.create(1,0).relativeToRightOf(recurrenceLabel))
                .build()
        Recurrence.values().forEach { recurrence ->
            if(recurrence != currentRecurrence) {
                recurrenceRadioButtonGroup.addOption(recurrence.name, recurrence.name)
            } else {
                recurrenceRadioButtonGroup.addOption(recurrence.name, recurrence.name + " (current)")
            }
        }
        recurrenceRadioButtonGroup.onSelection { it ->
            currentRecurrence = Recurrence.valueOf(it.key)
        }
        newPanel.addComponent(recurrenceRadioButtonGroup)
        val transferLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Transfer:")
                .withPosition(Positions.create(1,0).relativeToRightOf(recurrenceRadioButtonGroup))
                .build()
        newPanel.addComponent(transferLabel)
        var targetSavingsAccountName = budgetItem!!.transferredToSavingsAccountName
        val transferSavingsAccountButtonGroup: RadioButtonGroup = Components.radioButtonGroup()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(35, 5))
                .withPosition(Positions.create(1,0).relativeToRightOf(transferLabel))
                .build()
        applicationState.savingsAccounts!!.forEach { savingsAccount ->
            if(savingsAccount.name.equals(targetSavingsAccountName)) {
                transferSavingsAccountButtonGroup.addOption(savingsAccount.name, savingsAccount.name + " (current)")
            } else {
                transferSavingsAccountButtonGroup.addOption(savingsAccount.name, savingsAccount.name)
            }
        }
        transferSavingsAccountButtonGroup.onSelection { it ->
            targetSavingsAccountName = it.key
        }
        newPanel.addComponent(transferSavingsAccountButtonGroup)

        var targetCreditAccountName = budgetItem!!.transferredToCreditAccountName
        val transferCreditAccountButtonGroup: RadioButtonGroup = Components.radioButtonGroup()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(35, 5))
                .withPosition(Positions.create(0,0).relativeToBottomOf(transferSavingsAccountButtonGroup))
                .build()
        applicationState.creditAccounts!!.forEach { creditAccount ->
            if(creditAccount.name.equals(targetCreditAccountName)) {
                transferCreditAccountButtonGroup.addOption(creditAccount.name, creditAccount.name + " (current)")
            } else {
                transferCreditAccountButtonGroup.addOption(creditAccount.name, creditAccount.name)
            }
        }
        transferCreditAccountButtonGroup.onSelection { it ->
            targetCreditAccountName = it.key
        }
        newPanel.addComponent(transferCreditAccountButtonGroup)

        val submitButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Update")
                .withPosition(Positions.create(1,0).relativeToRightOf(transferSavingsAccountButtonGroup))
                .build()

        submitButton.onMouseReleased {
            mouseAction ->
            var updatedBudgetItem = BudgetItem(0, scheduledAmountTextArea.text.toDoubleOrNull()!!,
                    actualAmountTextArea.text.toDoubleOrNull()!!,
                    LocalDateTime.of(LocalDate.parse(dueTextArea.text), LocalTime.of(8,0)),
                    currentRecurrence, nameTextArea.text,
                    targetSavingsAccountName, targetCreditAccountName)
            updateBudgetItem(name, updatedBudgetItem)
        }
        newPanel.addComponent(submitButton)
        val reconcileButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Reconcile")
                .withPosition(Positions.create(0,1).relativeToBottomOf(submitButton))
                .build()
        reconcileButton.onMouseReleased {
            reconcileBudgetItem(name)
        }
        newPanel.addComponent(reconcileButton)
        val deleteButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Delete")
                .withPosition(Positions.create(0,1).relativeToBottomOf(reconcileButton))
                .build()
        deleteButton.onMouseReleased {
            deleteBudgetItem(name)
        }
        newPanel.addComponent(deleteButton)
        parent.updateInputPanel(newPanel)
    }

    private fun reconcileBudgetItem(name: String) {
        val reconciledBudgetItem = applicationState.futureBudgetItems!!.remove(name)
        if(reconciledBudgetItem != null) {
            applicationState.checkingAccount!!.reconciledItems.add(AccountItem(reconciledBudgetItem?.due!!, reconciledBudgetItem?.name!!, reconciledBudgetItem?.actualAmount!!))
            applicationState.checkingAccount!!.balance = applicationState.checkingAccount!!.balance + reconciledBudgetItem.actualAmount
        }
        budgetItemChange()
    }

    private fun deleteBudgetItem(name: String) {
        applicationState.futureBudgetItems!!.remove(name)
        budgetItemChange()
    }

    fun updateBudgetItem(originalName: String, updatedBudgetItem: BudgetItem){
        applicationState.futureBudgetItems!!.remove(originalName)
        applicationState.futureBudgetItems!!.put(updatedBudgetItem.name, updatedBudgetItem)
        budgetItemChange()
    }

    fun budgetItemChange(){
        update()
        parent.update()
        parent.clearInputPanel()
    }
}