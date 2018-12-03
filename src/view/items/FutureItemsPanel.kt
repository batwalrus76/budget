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
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import org.hexworks.zircon.api.kotlin.onSelection
import utils.DateTimeUtils
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.min

class FutureItemsPanel (width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents,
                                        applicationState: ApplicationState) :
        BaseItemsPanel(width, height, component, uiComponents, applicationState){

    var headerLabel: Label = Components.label()
            .withText("    | Auto | Req |     Due     |         Name         | Xfer |       Amt       ")
            .withPosition(Positions.create(0,1))
            .build()
    var dividerLabel: Label = Components.label()
            .withText("-------------------------------------------------------------------------------")
            .withPosition(Positions.create(0,0).relativeToBottomOf(headerLabel))
            .build()

    override fun build() {
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(FUTURE_ITEM_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, height))
                .withPosition(Positions.create(0,0).relativeToRightOf(this!!.component!!))
                .build()
        this.panel!!.addComponent(headerLabel)
        this.panel!!.addComponent(dividerLabel)
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(-1,-1).relativeToBottomOf(dividerLabel))
                .withSize(Sizes.create(this.width-2, this.height-5))
                .build()
        radioButtonGroup?.let { this.panel!!.addComponent(it) }
    }

    override fun update(){
        var budgetItems = applicationState.budgetItems!!.values.sortedWith(kotlin.comparisons.compareBy({ it.due }))
        this.panel!!.removeComponent(this!!.radioButtonGroup!!)
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(-1,-1).relativeToBottomOf(dividerLabel))
                .withSize(Sizes.create(this.width-2, this.height-5))
                .build()
        this.panel!!.addComponent(radioButtonGroup!!)
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel(it)
        }
        budgetItems?.forEach { budgetItem ->
            if((budgetItem.dueDates.size>0 && !budgetItem.dueDates.get(0).equals(budgetItem.due)) ||
                    budgetItem.due.isAfter(applicationState.currentPayPeriodBudgetState?.endDate)) {
                radioButtonGroup!!.addOption(budgetItem.name, budgetItem.toNarrowString())
            }
        }
    }

    companion object {
        val FUTURE_ITEM_TITLE: String = "Future Items"
    }

    private fun updateInputPanel(selection: RadioButtonGroup.Selection) {
        var inputPanel = uiComponents.weeklyOverviewScreen!!.inputPanel
        var newPanel: Panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(inputPanel!!.width-4, inputPanel!!.height-4))
                .withPosition(Positions.offset1x1())
                .build()
        val budgetItem = applicationState.budgetItems!!.get(selection.key)

        var itemConfigurationPanel = budgetItem?.scheduledAmount?.let {
            ItemConfigurationPanel(budgetItem.name, budgetItem.due, budgetItem.required,
                    budgetItem.autopay, it, budgetItem.actualAmount, budgetItem.recurrence,
                    newPanel!!.width-25, newPanel!!.height-1, "null", "null",
                    applicationState)
        }
        itemConfigurationPanel!!.build()
        itemConfigurationPanel!!.panel?.let { newPanel!!.addComponent(it) }

        val updateButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Update")
                .withPosition(Positions.create(1,0).relativeToRightOf(itemConfigurationPanel!!.panel!!))
                .build()

        updateButton.onMouseReleased {
            mouseAction ->
            var updatedBudgetItem = itemConfigurationPanel.generateItem()
            updatedBudgetItem.fillOutDueDates()
            updateBudgetItem(updatedBudgetItem.name, updatedBudgetItem)
        }
        newPanel.addComponent(updateButton)
        val reconcileButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Reconcile")
                .withPosition(Positions.create(0,1).relativeToBottomOf(updateButton))
                .build()
        reconcileButton.onMouseReleased {
            reconcileBudgetItem(itemConfigurationPanel.name)
        }
        newPanel.addComponent(reconcileButton)
        val removeDueDateButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Remove Due Date")
                .withPosition(Positions.create(0,1).relativeToBottomOf(reconcileButton))
                .build()
        removeDueDateButton.onMouseReleased {
            if (budgetItem != null) {
                removeDueDate(budgetItem)
            }
        }
        newPanel.addComponent(removeDueDateButton)
        val deleteButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Delete")
                .withPosition(Positions.create(0,1).relativeToBottomOf(removeDueDateButton))
                .build()
        deleteButton.onMouseReleased {
            deleteBudgetItem(itemConfigurationPanel.name)
        }
        newPanel.addComponent(deleteButton)
        uiComponents.updateInputScreen(newPanel)
    }

    private fun removeDueDate(budgetItem: BudgetItem) {
        val dueDate = budgetItem.dueDates.removeAt(0)
        if(budgetItem.due.equals(dueDate)){
            budgetItem.due = budgetItem.dueDates[0]
        }
        update()
    }

    private fun reconcileBudgetItem(name: String) {
        val reconciledBudgetItem = applicationState.budgetItems!!.remove(name)
        reconciledBudgetItem?.let { super.reconcileBudgetItem(it) }
    }

    private fun deleteBudgetItem(name: String) {
        applicationState.budgetItems!!.remove(name)
        budgetItemChange()
    }

    fun updateBudgetItem(originalName: String, updatedBudgetItem: BudgetItem){
        applicationState.budgetItems!!.remove(originalName)
        applicationState.budgetItems!!.put(updatedBudgetItem.name, updatedBudgetItem)
        budgetItemChange()
    }
}