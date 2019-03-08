package view.financial.items

import control.handlers.view.BudgetViewHandler
import model.financial.account.AccountItem
import model.representation.state.ApplicationState
import model.financial.budget.BudgetItem
import model.financial.core.DueDate
import model.enums.budget.Recurrence
import model.financial.account.Account
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.RadioButtonGroup
import org.hexworks.zircon.api.data.Position
import utils.DateTimeUtils
import java.time.LocalDate

abstract class BaseItemsPanel (var width: Int, var height: Int, val component: Component,
                               var uiComponents: ApplicationUIComponents, var applicationState: ApplicationState): BudgetViewHandler {

    var radioButtonGroup: RadioButtonGroup? = null
    var panel: Panel? = null

    open fun build(){
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(0,1))
                .withSize(Sizes.create(this.width-4, this.height-4))
                .build()
    }

    open fun reconcileBudgetItem(reconciledBudgetItem: BudgetItem) {
        if(reconciledBudgetItem != null) {
            applicationState.checkingAccount!!.reconciledItems.add(AccountItem(reconciledBudgetItem?.due!!.dueDate,
                    reconciledBudgetItem?.name!!, reconciledBudgetItem?.actualAmount!!))
            applicationState.checkingAccount!!.balance = applicationState.checkingAccount!!.balance + reconciledBudgetItem.actualAmount
            var newReconciledBudgetItemDueDates =  ArrayList<DueDate>()
            reconciledBudgetItem.dueDates.forEach { dueDate ->
                if(dueDate.dueDate.isAfter(DateTimeUtils.currentDate())){
                    newReconciledBudgetItemDueDates.add(dueDate)
                }
            }
            reconciledBudgetItem.dueDates = newReconciledBudgetItemDueDates
            if(reconciledBudgetItem.dueDates.size > 0){
                reconciledBudgetItem.due = reconciledBudgetItem.dueDates.first()
            } else if(reconciledBudgetItem.recurrence== Recurrence.ONETIME){
                applicationState.budgetItems?.remove(reconciledBudgetItem.name)
            }
        }
        budgetItemChange()
    }

    override fun handleNewBudgetItem(budgetItem: BudgetItem, hypothetical: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleModifiedBudgetItem(budgetItem: BudgetItem) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleNewAccount(account: Account) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleDay(date: LocalDate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleWeek(startDate: LocalDate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleMonth(startDate: LocalDate) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    open fun update(){
        this.panel!!.removeComponent(this!!.radioButtonGroup!!)
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(0,1).relativeToBottomOf(this.panel!!.children.last()))
                .withSize(Sizes.create(this.width-4, this.height-8))
                .build()
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    open fun budgetItemChange(){
        update()
        uiComponents.update()
        uiComponents.clearInputScreen()
    }

}