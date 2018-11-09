package view.input

import model.ApplicationState
import model.BudgetAnalysisState
import model.BudgetItem
import model.enums.Recurrence
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.ColorThemes
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onKeyStroke
import org.hexworks.zircon.api.kotlin.onMouseReleased
import org.hexworks.zircon.api.kotlin.onSelection
import org.hexworks.zircon.api.util.Random
import utils.DateTimeUtils
import view.BudgetPanel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.min

class AddItemPanel(width: Int, height: Int, var parent: ApplicationUIComponents, applicationState: ApplicationState):
                BudgetPanel(width, height, applicationState){


    var id = Random.create().nextInt()
    var scheduledAmount = 0.0
    var actualAmount = 0.0
    var due:LocalDateTime = DateTimeUtils.currentTime()
    var recurrence = Recurrence.ONETIME
    var name = "PLACEHOLDER"
    var transferredSavingsAccountName: String? = null
    var transferredCreditAccountName: String? = null

    override fun build() {
        panel  = Components.panel()
            .wrapWithBox(false)
            .wrapWithShadow(false)
            .withSize(Sizes.create(width,height-1))
            .withPosition(Positions.offset1x1())
            .build()
        name = "PLACEHOLDER"
        val nameLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Name:")
                .withPosition(Positions.create(0,0))
                .build()
        panel!!.addComponent(nameLabel)
        var nameTextArea: TextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(name.substring(0, min(name.length,20)))
                .withSize(Sizes.create(20,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(nameLabel))
                .build()
        nameTextArea.onKeyStroke { name = nameTextArea.text }
        panel!!.addComponent(nameTextArea)
        due = DateTimeUtils.currentTime()
        val dueLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Due: ")
                .withPosition(Positions.create(0,1).relativeToBottomOf(nameLabel))
                .build()
        panel!!.addComponent(dueLabel)
        var dueTextArea: TextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(due.format(DateTimeFormatter.ISO_DATE))
                .withSize(Sizes.create(10,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(dueLabel))
                .build()
        panel!!.addComponent(dueTextArea)
        scheduledAmount = 0.0
        val scheduledAmountLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Scheduled Amount:")
                .withPosition(Positions.create(1,0).relativeToRightOf(nameTextArea))
                .build()
        panel!!.addComponent(scheduledAmountLabel)
        var scheduledAmountTextArea: TextArea = Components.textArea()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText(String.format("%.2f",scheduledAmount))
                .withSize(Sizes.create(10,2))
                .withPosition(Positions.create(1,0).relativeToRightOf(scheduledAmountLabel))
                .build()
        panel!!.addComponent(scheduledAmountTextArea)
        val recurrenceLabel: Label = Components.label()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withText("Recurrence:")
                .withPosition(Positions.create(1,0).relativeToRightOf(scheduledAmountTextArea))
                .build()
        panel!!.addComponent(recurrenceLabel)
        var currentRecurrence = Recurrence.ONETIME
        val recurrenceRadioButtonGroup: RadioButtonGroup = Components.radioButtonGroup()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(30, 10))
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
        panel!!.addComponent(recurrenceRadioButtonGroup)
        val addCurrentItemButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Add Current Budget Item")
                .withPosition(Positions.create(1,0).relativeToRightOf(recurrenceRadioButtonGroup))
                .build()
        addCurrentItemButton.onMouseReleased {
            mouseAction ->
            var newCurrentItem = BudgetItem(0, scheduledAmountTextArea.text.toDouble(),
                    scheduledAmountTextArea.text.toDouble(), due, currentRecurrence, name,
                    transferredSavingsAccountName, transferredCreditAccountName)
            applicationState.currentPayPeriodBudgetState!!.currentBudgetItems!!.put(name, newCurrentItem)
            parent.update()
            parent.clearInputPanel()
        }
        panel!!.addComponent(addCurrentItemButton)
        val addFutureItemButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Add Future Budget Item")
                .withPosition(Positions.create(0,1).relativeToBottomOf(addCurrentItemButton))
                .build()
        addFutureItemButton.onMouseReleased {
            mouseAction ->
            var newFutureItem = BudgetItem(0, scheduledAmountTextArea.text.toDouble(),
                    scheduledAmountTextArea.text.toDouble(), due, currentRecurrence, name,
                    transferredSavingsAccountName, transferredCreditAccountName)
            applicationState.futureBudgetItems!!.put(name, newFutureItem)
            parent.update()
            parent.clearInputPanel()
        }
        panel!!.addComponent(addFutureItemButton)
        val addUnreconciledItemButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Add Unreconciled Budget Item")
                .withPosition(Positions.create(0,1).relativeToBottomOf(addFutureItemButton))
                .build()
        addUnreconciledItemButton.onMouseReleased {
            mouseAction ->
            var newFutureItem = BudgetItem(0, scheduledAmountTextArea.text.toDouble(),
                    scheduledAmountTextArea.text.toDouble(), due, currentRecurrence, name,
                    transferredSavingsAccountName, transferredCreditAccountName)
            applicationState.pastUnreconciledBudgetItems!!.put(name, newFutureItem)
            parent.update()
            parent.clearInputPanel()
        }
        panel!!.addComponent(addUnreconciledItemButton)
    }

    override fun update(budgetAnalysisState: BudgetAnalysisState) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}