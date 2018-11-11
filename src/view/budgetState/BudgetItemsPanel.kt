package view.budgetState

import model.*
import model.enums.Recurrence
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
import view.screens.WeeklyOverviewScreen
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.min

class BudgetItemsPanel(width: Int, height: Int, component: Component, parent: BaseScreen,
                       applicationState: ApplicationState) :
                                BaseItemsPanel(width, height, component, parent, applicationState){

    var currentBudgetState: BudgetState? = null
    var currentBudgetAnalysisStates: MutableList<BudgetAnalysisState>? = null

    override fun build() {
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(BudgetStatePanel.ITINERARY) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(this.width, this.height))
                .withPosition(BudgetStatePanel.DEFAULT_OFFSET.relativeToBottomOf(component))
                .build()
        super.build()
        this.panel!!.addComponent(radioButtonGroup!!)
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel(it)
        }
    }

    fun update(budgetState: BudgetState) {
        currentBudgetState = budgetState
        var budgetStateCurrentItems = budgetState.currentBudgetItems!!.values.sortedWith(compareBy({ it.due as Comparable<*>? }))
        super.update()
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel(it)
        }
        budgetStateCurrentItems?.forEach { u ->
            var budgetItemCheckingAccountBalance: Double? = 0.0
            currentBudgetAnalysisStates?.forEach { associatedBudgetAnalysisState ->
                if(associatedBudgetAnalysisState.budgetItem?.name.equals(u.name)){
                    budgetItemCheckingAccountBalance = associatedBudgetAnalysisState.checkingAccountBalance
                }
            }
            var budgetItemText = u.toNarrowString() + String.format("Balance:%.2f", budgetItemCheckingAccountBalance)
            radioButtonGroup!!.addOption(u.name,budgetItemText)
        }
    }

    fun updateBudgetItem(originalName: String, updatedBudgetItem: BudgetItem){
        currentBudgetState!!.currentBudgetItems!!.remove(originalName)
        currentBudgetState!!.currentBudgetItems!!.put(updatedBudgetItem.name, updatedBudgetItem)
        update(currentBudgetState!!)
    }

    private fun updateInputPanel(selection: RadioButtonGroup.Selection) {
        var inputPanel = parent.inputPanel
        var newPanel:Panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(inputPanel!!.width-4, inputPanel!!.height-4))
                .withPosition(Positions.offset1x1())
                .build()
        val budgetItem =
                parent.applicationUIComponents.currentViewedBudgetState!!.currentBudgetItems!!.get(selection.key)
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
                    currentRecurrence, nameTextArea.text, targetSavingsAccountName, targetCreditAccountName)
            updateBudgetItem(name, updatedBudgetItem)
            parent.update()
            parent.clearInputPanel()
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
        val reconciledBudgetItem = currentBudgetState!!.currentBudgetItems!!.remove(name)
        if(reconciledBudgetItem != null) {
            applicationState.checkingAccount!!.reconciledItems.add(AccountItem(reconciledBudgetItem?.due!!, reconciledBudgetItem?.name!!, reconciledBudgetItem?.actualAmount!!))
            applicationState.checkingAccount!!.balance = applicationState.checkingAccount!!.balance + reconciledBudgetItem.actualAmount
            update(currentBudgetState!!)
        }
        parent.update()
        parent.clearInputPanel()
    }

    private fun deleteBudgetItem(name: String) {
        currentBudgetState!!.currentBudgetItems!!.remove(name)
        update(currentBudgetState!!)
        parent.update()
        parent.clearInputPanel()
    }

}