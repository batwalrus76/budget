package view.items

import model.ApplicationState
import model.BudgetItem
import model.enums.Recurrence
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.RadioButtonGroup
import org.hexworks.zircon.api.component.TextArea
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.kotlin.onKeyStroke
import org.hexworks.zircon.api.kotlin.onSelection
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.ArrayList
import kotlin.math.min

class ItemConfigurationPanel(var name: String, var due: LocalDate, var isRequired: Boolean, var isAutopay: Boolean,
                            var scheduledAmount: Double, var actualAmount: Double, var currentRecurrence: Recurrence,
                            var width: Int, var height: Int, var targetSavingsAccountName: String,
                             var targetCreditAccountName: String, var applicationState: ApplicationState) {

    var panel: Panel? = null
    var scheduledAmountTextArea: TextArea? = null
    var actualAmountTextArea: TextArea? = null
    var dueTextArea: TextArea? = null

    fun build() {
        panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(width,height))
                .withPosition(Positions.create(0,0 ))
                .build()
        val nameLabel = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Name:")
                .withPosition(Positions.create(0,0))
                .build()
        panel!!.addComponent(nameLabel)
        val nameTextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(name.substring(0, min(name.length,20)))
                .withSize(Sizes.create(20,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(nameLabel))
                .build()
        nameTextArea.onKeyStroke { name = nameTextArea.text }
        panel!!.addComponent(nameTextArea)
        val dueLabel = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Due: ")
                .withPosition(Positions.create(0,1).relativeToBottomOf(nameLabel))
                .build()
        panel!!.addComponent(dueLabel)
        dueTextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(due.format(DateTimeFormatter.ISO_DATE))
                .withSize(Sizes.create(10,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(dueLabel))
                .build()
        dueTextArea!!.onKeyStroke {
            it ->
            try {
                due = LocalDate.parse(dueTextArea!!.text)
            } catch (e: Exception){

            }
        }
        panel!!.addComponent(dueTextArea!!)
        val isRequiredLabel = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Required: ")
                .withPosition(Positions.create(0,2).relativeToBottomOf(dueLabel))
                .build()
        panel!!.addComponent(isRequiredLabel)
        val isRequiredButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(0,0).relativeToRightOf(isRequiredLabel))
                .withSize(Sizes.create(20, 2))
                .build()
        if(isRequired) {
            isRequiredButtonGroup.addOption("true", "true (current)")
            isRequiredButtonGroup.addOption("false", "false")
        } else {
            isRequiredButtonGroup.addOption("true", "true")
            isRequiredButtonGroup.addOption("false", "false (current)")
        }
        isRequiredButtonGroup.onSelection {
            it ->
            isRequired = it.value.toBoolean()
        }
        panel!!.addComponent(isRequiredButtonGroup)
        val scheduledAmountLabel = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Scheduled Amount:")
                .withPosition(Positions.create(1,0).relativeToRightOf(nameTextArea))
                .build()
        panel!!.addComponent(scheduledAmountLabel)
        scheduledAmountTextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(String.format("%.2f",scheduledAmount))
                .withSize(Sizes.create(10,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(scheduledAmountLabel))
                .build()
        scheduledAmountTextArea!!.onKeyStroke {
            it -> scheduledAmount = scheduledAmountTextArea!!.text.toDoubleOrNull()!!
        }
        panel!!.addComponent(scheduledAmountTextArea!!)
        val actualAmountLabel = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Actual Amount:   ")
                .withPosition(Positions.create(0,1).relativeToBottomOf(scheduledAmountLabel))
                .build()
        panel!!.addComponent(actualAmountLabel)
        val actualAmountTextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(String.format("%.2f",actualAmount))
                .withSize(Sizes.create(10,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(actualAmountLabel))
                .build()
        actualAmountTextArea!!.onKeyStroke {
            it -> actualAmount = actualAmountTextArea!!.text.toDoubleOrNull()!!
        }
        panel!!.addComponent(actualAmountTextArea)
        val isAutopayLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Autopay: ")
                .withPosition(Positions.create(0,0).relativeToRightOf(isRequiredButtonGroup))
                .build()
        panel!!.addComponent(isAutopayLabel)
        val isAutopayButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(0,0).relativeToRightOf(isAutopayLabel))
                .withSize(Sizes.create(20, 2))
                .build()
        if(isAutopay) {
            isAutopayButtonGroup.addOption("true", "true (current)")
            isAutopayButtonGroup.addOption("false", "false")
        } else {
            isAutopayButtonGroup.addOption("true", "true")
            isAutopayButtonGroup.addOption("false", "false (current)")
        }
        isAutopayButtonGroup.onSelection {
            it ->
            isAutopay = it.value.toBoolean()
        }
        panel!!.addComponent(isAutopayButtonGroup)
        val recurrenceLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Recurrence:")
                .withPosition(Positions.create(1,0).relativeToRightOf(scheduledAmountTextArea!!))
                .build()
        panel!!.addComponent(recurrenceLabel)
        val recurrenceRadioButtonGroup: RadioButtonGroup = Components.radioButtonGroup()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(25, Recurrence.values().size))
                .withPosition(Positions.create(1,0).relativeToRightOf(recurrenceLabel))
                .build()
        Recurrence.values().forEach { recurrence ->
            if(recurrence != currentRecurrence) {
                recurrenceRadioButtonGroup.addOption(recurrence.name, recurrence.name)
            } else {
                recurrenceRadioButtonGroup.addOption(recurrence.name, "${recurrence.name} (current)")
            }
        }
        recurrenceRadioButtonGroup.onSelection { it ->
            currentRecurrence = Recurrence.valueOf(it.key)
        }
        panel!!.addComponent(recurrenceRadioButtonGroup)

        val transferLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Transfer:")
                .withPosition(Positions.create(1,0).relativeToRightOf(recurrenceRadioButtonGroup))
                .build()
        panel!!.addComponent(transferLabel)

        val transferSavingsAccountButtonGroup: RadioButtonGroup = Components.radioButtonGroup()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(30, applicationState.savingsAccounts!!.size))
                .withPosition(Positions.create(0,0).relativeToRightOf(transferLabel))
                .build()
        applicationState.savingsAccounts!!.forEach { name, savingsAccount ->
            if(name.equals(targetSavingsAccountName)) {
                transferSavingsAccountButtonGroup.addOption(name, "$name (current)")
            } else {
                transferSavingsAccountButtonGroup.addOption(name, name)
            }
        }
        transferSavingsAccountButtonGroup.onSelection { it ->
            targetSavingsAccountName = it.key
        }
        panel!!.addComponent(transferSavingsAccountButtonGroup)
        val transferCreditAccountButtonGroup: RadioButtonGroup = Components.radioButtonGroup()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(30, applicationState.creditAccounts!!.size))
                .withPosition(Positions.create(0,0).relativeToBottomOf(transferSavingsAccountButtonGroup))
                .build()
        applicationState.creditAccounts!!.forEach { name, creditAccount ->
            if(name.equals(targetCreditAccountName)) {
                transferCreditAccountButtonGroup.addOption(name, "$name (current)")
            } else {
                transferCreditAccountButtonGroup.addOption(name, name)
            }
        }
        transferCreditAccountButtonGroup.onSelection { it ->
            targetCreditAccountName = it.key
        }
        panel!!.addComponent(transferCreditAccountButtonGroup)
    }

    open fun generateItem(): BudgetItem{
        return BudgetItem(scheduledAmount, actualAmount, due, currentRecurrence, name, isAutopay, isRequired,
                targetSavingsAccountName, targetCreditAccountName, ArrayList())
    }
}