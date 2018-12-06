package view.budgetState

import model.*
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
import view.items.BaseItemsPanel
import view.items.ItemConfigurationPanel
import kotlin.math.min

class BudgetItemsPanel(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents,
                       applicationState: ApplicationState) :
                                BaseItemsPanel(width, height, component, uiComponents, applicationState){

    var headerLabel: Label = Components.label()
            .withText("        | Auto | Req |     Due     |         Name         | Xfer |   Amt   |    Balance     ")
            .withPosition(Positions.create(0,1))
            .build()
    var dividerLabel: Label = Components.label()
            .withText("----------------------------------------------------------------------------------------------")
            .withPosition(Positions.create(0,0).relativeToBottomOf(headerLabel))
            .build()
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
        this.panel!!.addComponent(headerLabel)
        this.panel!!.addComponent(dividerLabel)
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(-1,-1).relativeToBottomOf(dividerLabel))
                .withSize(Sizes.create(this.width-2, this.height-5))
                .build()
        this.panel!!.addComponent(radioButtonGroup!!)
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel(it)
        }
    }

    fun update(budgetState: BudgetState) {
        currentBudgetState = budgetState
        var applicationStateBudgetAnalysis =  uiComponents.applicationStateBudgetAnalysis
        var budgetStateCurrentItemMap =
                applicationStateBudgetAnalysis?.retrieveApplicableBudgetItemsForState(currentBudgetState!!)
        var budgetStateCurrentItemsList = budgetStateCurrentItemMap?.values?.sortedWith(compareBy({ it.due}))
        currentBudgetAnalysisStates = applicationStateBudgetAnalysis?.performAnalysisOnBudgetItems(budgetStateCurrentItemMap)
        this.panel!!.removeComponent(this!!.radioButtonGroup!!)
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(-1,-1).relativeToBottomOf(dividerLabel))
                .withSize(Sizes.create(this.width-2, this.height-5))
                .build()
        this.panel!!.addComponent(radioButtonGroup!!)
        radioButtonGroup!!.onSelection { it ->
            updateInputPanel(it)
        }
        budgetStateCurrentItemsList?.forEach { u ->
            var budgetItemCheckingAccountBalance: Double? = 0.0
            currentBudgetAnalysisStates?.forEach { associatedBudgetAnalysisState ->
                if(associatedBudgetAnalysisState.budgetItem?.name.equals(u.name)){
                    budgetItemCheckingAccountBalance = associatedBudgetAnalysisState.checkingAccountBalance
                    var budgetItemText = "    "+u.toNarrowString(associatedBudgetAnalysisState.date) +
                            String.format("|    %.2f   ", budgetItemCheckingAccountBalance)
                    associatedBudgetAnalysisState.budgetItem?.name?.let { radioButtonGroup!!.addOption(it,budgetItemText) }
                }
            }
        }
    }

    fun updateBudgetItem(originalName: String, updatedBudgetItem: BudgetItem){
        applicationState.budgetItems?.remove(originalName)
        applicationState.budgetItems?.put(updatedBudgetItem.name, updatedBudgetItem)
        update(currentBudgetState!!)
    }

    private fun updateInputPanel(selection: RadioButtonGroup.Selection) {
        var inputPanel = uiComponents.weeklyOverviewScreen!!.inputPanel
        var applicationStateBudgetAnalysis =  uiComponents.applicationStateBudgetAnalysis
        var budgetStateCurrentItemMap =
                applicationStateBudgetAnalysis?.retrieveApplicableBudgetItemsForState(currentBudgetState!!)
        val budgetItem = budgetStateCurrentItemMap!![selection.key]

        var newPanel:Panel = Components.panel()
                .wrapWithBox(false)
                .wrapWithShadow(false)
                .withSize(Sizes.create(inputPanel!!.width-4, inputPanel!!.height-4))
                .withPosition(Positions.offset1x1())
                .build()

        var itemConfigurationPanel = budgetItem?.scheduledAmount?.let {
            ItemConfigurationPanel(budgetItem.name, budgetItem.due, budgetItem.required,
                    budgetItem.autopay, it, budgetItem.actualAmount, budgetItem.recurrence,
                    newPanel!!.width-25, newPanel!!.height-1, "null", "null",
                    applicationState)
        }
        itemConfigurationPanel!!.build()
        itemConfigurationPanel!!.panel?.let { newPanel!!.addComponent(it) }

        val submitButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Update")
                .withPosition(Positions.create(1,0).relativeToRightOf(itemConfigurationPanel!!.panel!!))
                .build()
        submitButton.onMouseReleased {
            mouseAction ->
            var updatedBudgetItem = itemConfigurationPanel.generateItem()
            updatedBudgetItem.fillOutDueDates()
            updateBudgetItem(updatedBudgetItem.name, updatedBudgetItem)
            uiComponents.update()
            uiComponents.clearInputScreen()
        }
        newPanel.addComponent(submitButton)
        val reconcileButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Reconcile")
                .withPosition(Positions.create(0,1).relativeToBottomOf(submitButton))
                .build()
        reconcileButton.onMouseReleased {
            reconcileBudgetItem(itemConfigurationPanel.name)
        }
        newPanel.addComponent(reconcileButton)
        val deleteButton: Button = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .withText("Delete")
                .withPosition(Positions.create(0,1).relativeToBottomOf(reconcileButton))
                .build()
        deleteButton.onMouseReleased {
            deleteBudgetItem(itemConfigurationPanel.name)
        }
        newPanel.addComponent(deleteButton)
        uiComponents.updateInputScreen(newPanel)
    }

    private fun reconcileBudgetItem(name: String) {
        var applicationStateBudgetAnalysis =  uiComponents.applicationStateBudgetAnalysis
        var reconciledBudgetItems = applicationStateBudgetAnalysis?.
                reconcileApplicationBudgetState(currentBudgetState!!, applicationState.pastUnreconciledBudgetItems!!)
        val reconciledBudgetItem = reconciledBudgetItems!!.remove(name)
        if(reconciledBudgetItem != null) {
            applicationState.checkingAccount!!.reconciledItems.add(
                    AccountItem(reconciledBudgetItem?.due!!,
                            reconciledBudgetItem?.name!!, reconciledBudgetItem?.actualAmount!!))
            applicationState.checkingAccount!!.balance =
                    applicationState.checkingAccount!!.balance + reconciledBudgetItem.actualAmount
            update(currentBudgetState!!)
            super.reconcileBudgetItem(reconciledBudgetItem)
        }
        update()
    }

    private fun deleteBudgetItem(name: String) {
        var applicationStateBudgetAnalysis =  uiComponents.applicationStateBudgetAnalysis
        var currentBudgetItems =
                applicationStateBudgetAnalysis!!.retrieveApplicableBudgetItemsForState(this!!.currentBudgetState!!)
        var budgetItem = currentBudgetItems.remove(name)
        for(dueDateIndex in 0..budgetItem?.dueDates!!.size-1){
            val dueDate = budgetItem!!.dueDates[dueDateIndex]
            if(currentBudgetState?.isValidForDueDate(dueDate)!!){
                budgetItem.dueDates.remove(dueDate)
                break
            }
        }
        budgetItem?.dueDates?.forEach { dueDate ->
        }
        if(currentBudgetState?.isValidForDueDate(budgetItem!!.due)!! && budgetItem?.dueDates?.size == 0) {
            uiComponents.applicationState.budgetItems?.remove(budgetItem.name)
        }
        update(currentBudgetState!!)
        uiComponents.update()
        uiComponents.clearInputScreen()
    }

}