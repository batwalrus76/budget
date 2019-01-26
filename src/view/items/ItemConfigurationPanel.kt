package view.items

import model.state.ApplicationState
import model.budget.BudgetItem
import model.core.DueDate
import model.enums.Recurrence
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.builder.component.LabelBuilder
import org.hexworks.zircon.api.builder.component.RadioButtonGroupBuilder
import org.hexworks.zircon.api.builder.component.TextAreaBuilder
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
                             var targetCreditAccountName: String, var applicationState: ApplicationState,
                             var useHorizontalLayout: Boolean = true, var position: Position = Positions.create(0,0)) {

    var panel: Panel? = null
    var scheduledAmountText: String? = scheduledAmount.toString()
    var actualAmountText: String? = actualAmount.toString()

    var nameLabel: Label? = null
    var nameTextArea: TextArea? = null
    var dueLabel: Label? = null
    var dueTextArea: TextArea? = null
    var isRequiredLabel: Label? = null
    var isRequiredButtonGroup: RadioButtonGroup? = null
    var scheduledAmountLabel: Label? = null
    var scheduledAmountTextArea: TextArea? = null
    var actualAmountLabel: Label? = null
    var actualAmountTextArea: TextArea? = null
    var isAutopayLabel: Label? = null
    var isAutopayButtonGroup: RadioButtonGroup? = null
    var recurrenceLabel: Label? = null
    var recurrenceRadioButtonGroup: RadioButtonGroup? = null
    var transferLabel: Label? = null
    var transferSavingsAccountButtonGroup: RadioButtonGroup? = null
    var transferCreditAccountButtonGroup: RadioButtonGroup? = null

    fun build() {
        panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(width,height))
                .withPosition(position)
                .build()
        processPanelComponents()
        updatePanelWithComponents()
    }

    private fun updatePanelWithComponents(){
        panel?.children?.forEach { panel?.removeComponent(it) }

        nameLabel?.let { panel!!.addComponent(it) }
        nameTextArea?.let { panel!!.addComponent(it) }
        dueLabel?.let { panel!!.addComponent(it) }
        dueTextArea?.let { panel!!.addComponent(it)}
        isRequiredLabel?.let { panel!!.addComponent(it)}
        isRequiredButtonGroup?.let { panel!!.addComponent(it)}
        scheduledAmountLabel?.let { panel!!.addComponent(it)}
        scheduledAmountTextArea?.let { panel!!.addComponent(it)}
        actualAmountLabel?.let { panel!!.addComponent(it)}
        actualAmountTextArea?.let { panel!!.addComponent(it)}
//        isAutopayLabel?.let { panel!!.addComponent(it)}
//        isAutopayButtonGroup?.let { panel!!.addComponent(it)}
//        recurrenceLabel?.let { panel!!.addComponent(it)}
//        recurrenceRadioButtonGroup?.let { panel!!.addComponent(it)}
//        transferLabel?.let { panel!!.addComponent(it)}
//        transferSavingsAccountButtonGroup?.let { panel!!.addComponent(it)}
//        transferCreditAccountButtonGroup?.let { panel!!.addComponent(it)}
    }

    private fun addComponentBehaviours(){
        dueTextArea!!.onKeyStroke {
            it ->
            try {
                due = LocalDate.parse(dueTextArea!!.text)
            } catch (e: Exception){

            }
        }
        nameTextArea?.onKeyStroke { name = nameTextArea!!.text }
        if(isRequired) {
            isRequiredButtonGroup?.addOption("true", "true (current)")
            isRequiredButtonGroup?.addOption("false", "false")
        } else {
            isRequiredButtonGroup?.addOption("true", "true")
            isRequiredButtonGroup?.addOption("false", "false (current)")
        }
        isRequiredButtonGroup?.onSelection {
            it ->
            isRequired = it.value.toBoolean()
        }
        scheduledAmountTextArea!!.onKeyStroke {
            it -> scheduledAmountText = scheduledAmountTextArea!!.text
        }
        actualAmountTextArea!!.onKeyStroke {
            it -> actualAmountText = actualAmountTextArea!!.text
        }
        if(isAutopay) {
            isAutopayButtonGroup?.addOption("true", "true (current)")
            isAutopayButtonGroup?.addOption("false", "false")
        } else {
            isAutopayButtonGroup?.addOption("true", "true")
            isAutopayButtonGroup?.addOption("false", "false (current)")
        }
        isAutopayButtonGroup?.onSelection {
            it ->
            isAutopay = it.value.toBoolean()
        }
        Recurrence.values().forEach { recurrence ->
            if(recurrence != currentRecurrence) {
                recurrenceRadioButtonGroup?.addOption(recurrence.name, recurrence.name)
            } else {
                recurrenceRadioButtonGroup?.addOption(recurrence.name, "${recurrence.name} (current)")
            }
        }
        recurrenceRadioButtonGroup?.onSelection { it ->
            currentRecurrence = Recurrence.valueOf(it.key)
        }
        applicationState.savingsAccounts!!.forEach { name, savingsAccount ->
            if(name.equals(targetSavingsAccountName)) {
                transferSavingsAccountButtonGroup?.addOption(name, "$name (current)")
            } else {
                transferSavingsAccountButtonGroup?.addOption(name, name)
            }
        }
        transferSavingsAccountButtonGroup?.onSelection { it ->
            targetSavingsAccountName = it.key
        }
        applicationState.creditAccounts!!.forEach { name, creditAccount ->
            if(name.equals(targetCreditAccountName)) {
                transferCreditAccountButtonGroup?.addOption(name, "$name (current)")
            } else {
                transferCreditAccountButtonGroup?.addOption(name, name)
            }
        }
        transferCreditAccountButtonGroup?.onSelection { it ->
            targetCreditAccountName = it.key
        }
    }

    private fun processPanelComponents(){
        val nameLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Name:")
        val nameTextAreaBuilder = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(name.substring(0, min(name.length,20)))
                .withSize(Sizes.create(20,2))
        val dueLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Due: ")
        val dueTextAreaBuilder = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(due.format(DateTimeFormatter.ISO_DATE))
                .withSize(Sizes.create(10,2))
        val isRequiredLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Required: ")
        val isRequiredButtonGroupBuilder = Components.radioButtonGroup()
                .withSize(Sizes.create(20, 2))
        val scheduledAmountLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Scheduled Amount:")
        val scheduledAmountTextAreaBuilder = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(String.format("%.2f",scheduledAmount))
                .withSize(Sizes.create(10,2))
        val actualAmountLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Actual Amount:   ")
        val actualAmountTextAreaBuilder = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(String.format("%.2f",actualAmount))
                .withSize(Sizes.create(10,2))
        val isAutopayLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Autopay: ")
        val isAutopayButtonGroupBuilder = Components.radioButtonGroup()
                .withSize(Sizes.create(20, 2))
        val recurrenceLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Recurrence:")
        val recurrenceRadioButtonGroupBuilder = Components.radioButtonGroup()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(25, Recurrence.values().size))
        val transferLabelBuilder = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Transfer:")
        val transferSavingsAccountButtonGroupBuilder = Components.radioButtonGroup()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(30, applicationState.savingsAccounts!!.size))
        val transferCreditAccountButtonGroupBuilder = Components.radioButtonGroup()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(30, applicationState.creditAccounts!!.size))

        when(useHorizontalLayout) {
            true -> createHorizontalLayoutPanel(nameLabelBuilder, nameTextAreaBuilder, dueLabelBuilder,
                    dueTextAreaBuilder, isRequiredLabelBuilder, isRequiredButtonGroupBuilder,
                    scheduledAmountLabelBuilder, scheduledAmountTextAreaBuilder, actualAmountLabelBuilder,
                    actualAmountTextAreaBuilder, isAutopayLabelBuilder, isAutopayButtonGroupBuilder,
                    recurrenceLabelBuilder, recurrenceRadioButtonGroupBuilder, transferLabelBuilder,
                    transferSavingsAccountButtonGroupBuilder, transferCreditAccountButtonGroupBuilder)
            false -> createVerticalLayoutPanel(nameLabelBuilder, nameTextAreaBuilder, dueLabelBuilder,
                    dueTextAreaBuilder, isRequiredLabelBuilder, isRequiredButtonGroupBuilder,
                    scheduledAmountLabelBuilder, scheduledAmountTextAreaBuilder, actualAmountLabelBuilder,
                    actualAmountTextAreaBuilder, isAutopayLabelBuilder, isAutopayButtonGroupBuilder,
                    recurrenceLabelBuilder, recurrenceRadioButtonGroupBuilder, transferLabelBuilder,
                    transferSavingsAccountButtonGroupBuilder, transferCreditAccountButtonGroupBuilder)
        }
    }

    private fun createVerticalLayoutPanel(nameLabelBuilder: LabelBuilder, nameTextAreaBuilder: TextAreaBuilder,
                                          dueLabelBuilder: LabelBuilder, dueTextAreaBuilder: TextAreaBuilder,
                                          isRequiredLabelBuilder: LabelBuilder,
                                          isRequiredButtonGroupBuilder: RadioButtonGroupBuilder,
                                          scheduledAmountLabelBuilder: LabelBuilder,
                                          scheduledAmountTextAreaBuilder: TextAreaBuilder,
                                          actualAmountLabelBuilder: LabelBuilder,
                                          actualAmountTextAreaBuilder: TextAreaBuilder,
                                          isAutopayLabelBuilder: LabelBuilder,
                                          isAutopayButtonGroupBuilder: RadioButtonGroupBuilder,
                                          recurrenceLabelBuilder: LabelBuilder,
                                          recurrenceRadioButtonGroupBuilder: RadioButtonGroupBuilder,
                                          transferLabelBuilder: LabelBuilder,
                                          transferSavingsAccountButtonGroupBuilder: RadioButtonGroupBuilder,
                                          transferCreditAccountButtonGroupBuilder: RadioButtonGroupBuilder){

        nameLabel = nameLabelBuilder.withPosition(Positions.create(0,0))
                .build()
        nameTextArea = nameTextAreaBuilder.withPosition(Positions.create(1,0).relativeToRightOf(nameLabel!!))
                .build()
        dueLabel = dueLabelBuilder.withPosition(Positions.create(0,1).relativeToBottomOf(nameLabel!!))
                .build()
        dueTextArea = dueTextAreaBuilder.withPosition(Positions.create(1,0).relativeToRightOf(dueLabel!!))
                .build()
        isRequiredLabel = isRequiredLabelBuilder.withPosition(Positions.create(0,2).relativeToBottomOf(dueLabel!!))
                .build()
        isRequiredButtonGroup = isRequiredButtonGroupBuilder
                .withPosition(Position.create(0,0).relativeToRightOf(isRequiredLabel!!))
                .build()
        scheduledAmountLabel = scheduledAmountLabelBuilder
                .withPosition(Positions.create(0,20).relativeToBottomOf(isRequiredButtonGroup!!))
                .build()
        scheduledAmountTextArea = scheduledAmountTextAreaBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(scheduledAmountLabel!!))
                .build()
        actualAmountLabel = actualAmountLabelBuilder
                .withPosition(Positions.create(0,1).relativeToBottomOf(scheduledAmountLabel!!))
                .build()
        actualAmountTextArea = actualAmountTextAreaBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(actualAmountLabel!!))
                .build()
        isAutopayLabel = isAutopayLabelBuilder
                .withPosition(Positions.create(0,0).relativeToBottomOf(actualAmountLabel!!))
                .build()
        isAutopayButtonGroup = isAutopayButtonGroupBuilder
                .withPosition(Position.create(0,0).relativeToRightOf(isAutopayLabel!!))
                .build()
        recurrenceLabel = recurrenceLabelBuilder
                .withPosition(Positions.create(1,0).relativeToBottomOf(isAutopayLabel!!))
                .build()
        recurrenceRadioButtonGroup = recurrenceRadioButtonGroupBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(recurrenceLabel!!))
                .build()
        transferLabel = transferLabelBuilder
                .withPosition(Positions.create(1,0).relativeToBottomOf(recurrenceLabel!!))
                .build()
        transferSavingsAccountButtonGroup = transferSavingsAccountButtonGroupBuilder
                .withPosition(Positions.create(0,0).relativeToBottomOf(transferLabel!!))
                .build()
        transferCreditAccountButtonGroup = transferCreditAccountButtonGroupBuilder
                .withPosition(Positions.create(0,0).relativeToBottomOf(transferSavingsAccountButtonGroup!!))
                .build()
    }

    private fun createHorizontalLayoutPanel(nameLabelBuilder: LabelBuilder, nameTextAreaBuilder: TextAreaBuilder,
                                         dueLabelBuilder: LabelBuilder, dueTextAreaBuilder: TextAreaBuilder,
                                            isRequiredLabelBuilder: LabelBuilder,
                                            isRequiredButtonGroupBuilder: RadioButtonGroupBuilder,
                                            scheduledAmountLabelBuilder: LabelBuilder,
                                            scheduledAmountTextAreaBuilder: TextAreaBuilder,
                                            actualAmountLabelBuilder: LabelBuilder,
                                            actualAmountTextAreaBuilder: TextAreaBuilder,
                                            isAutopayLabelBuilder: LabelBuilder,
                                            isAutopayButtonGroupBuilder: RadioButtonGroupBuilder,
                                            recurrenceLabelBuilder: LabelBuilder,
                                            recurrenceRadioButtonGroupBuilder: RadioButtonGroupBuilder,
                                            transferLabelBuilder: LabelBuilder,
                                            transferSavingsAccountButtonGroupBuilder: RadioButtonGroupBuilder,
                                            transferCreditAccountButtonGroupBuilder: RadioButtonGroupBuilder){

        nameLabel = nameLabelBuilder.withPosition(Positions.create(0,0))
                .build()
        nameTextArea = nameTextAreaBuilder.withPosition(Positions.create(1,0).relativeToRightOf(nameLabel!!))
                .build()
        dueLabel = dueLabelBuilder.withPosition(Positions.create(0,1).relativeToBottomOf(nameLabel!!))
                .build()
        dueTextArea = dueTextAreaBuilder.withPosition(Positions.create(1,0).relativeToRightOf(dueLabel!!))
                .build()
        isRequiredLabel = isRequiredLabelBuilder.withPosition(Positions.create(0,2).relativeToBottomOf(dueLabel!!))
                .build()
        isRequiredButtonGroup = isRequiredButtonGroupBuilder
                .withPosition(Position.create(0,0).relativeToRightOf(isRequiredLabel!!))
                .build()
        scheduledAmountLabel = scheduledAmountLabelBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(nameTextArea!!))
                .build()
        scheduledAmountTextArea = scheduledAmountTextAreaBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(scheduledAmountLabel!!))
                .build()
        actualAmountLabel = actualAmountLabelBuilder
                .withPosition(Positions.create(0,1).relativeToBottomOf(scheduledAmountLabel!!))
                .build()
        actualAmountTextArea = actualAmountTextAreaBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(actualAmountLabel!!))
                .build()
        isAutopayLabel = isAutopayLabelBuilder
                .withPosition(Positions.create(0,0).relativeToRightOf(isRequiredButtonGroup!!))
                .build()
        isAutopayButtonGroup = isAutopayButtonGroupBuilder
                .withPosition(Position.create(0,0).relativeToRightOf(isAutopayLabel!!))
                .build()
        recurrenceLabel = recurrenceLabelBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(scheduledAmountTextArea!!))
                .build()
        recurrenceRadioButtonGroup = recurrenceRadioButtonGroupBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(recurrenceLabel!!))
                .build()
        transferLabel = transferLabelBuilder
                .withPosition(Positions.create(1,0).relativeToRightOf(recurrenceRadioButtonGroup!!))
                .build()
        transferSavingsAccountButtonGroup = transferSavingsAccountButtonGroupBuilder
                .withPosition(Positions.create(0,0).relativeToRightOf(transferLabel!!))
                .build()
        transferCreditAccountButtonGroup = transferCreditAccountButtonGroupBuilder
                .withPosition(Positions.create(0,0).relativeToBottomOf(transferSavingsAccountButtonGroup!!))
                .build()
    }

    open fun generateItem(): BudgetItem {
        return BudgetItem(scheduledAmountText!!.toDouble(), actualAmountText!!.toDouble(), DueDate(due, actualAmount),
                currentRecurrence, name, isAutopay, isRequired,
                targetSavingsAccountName, targetCreditAccountName, ArrayList())
    }
}