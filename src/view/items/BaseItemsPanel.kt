package view.items

import model.AccountItem
import model.ApplicationState
import model.BudgetItem
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.RadioButtonGroup
import org.hexworks.zircon.api.data.Position
import utils.DateTimeUtils

abstract class BaseItemsPanel (var width: Int, var height: Int, val component: Component,
                               var uiComponents: ApplicationUIComponents, var applicationState: ApplicationState) {

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
            applicationState.checkingAccount!!.reconciledItems.add(AccountItem(reconciledBudgetItem?.due!!, reconciledBudgetItem?.name!!, reconciledBudgetItem?.actualAmount!!))
            applicationState.checkingAccount!!.balance = applicationState.checkingAccount!!.balance + reconciledBudgetItem.actualAmount
            reconciledBudgetItem.dueDates.forEach { dueDate ->
                if(dueDate.isBefore(DateTimeUtils.currentDate().plusDays(1))){
                    reconciledBudgetItem.dueDates.remove(dueDate)
                }
            }
            if(reconciledBudgetItem.dueDates.size > 0){
                reconciledBudgetItem.due = reconciledBudgetItem.dueDates.first()
            }
        }
        budgetItemChange()
    }

    open fun update(){
        this.panel!!.removeComponent(this!!.radioButtonGroup!!)
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(0,1))
                .withSize(Sizes.create(this.width-4, this.height-4))
                .build()
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    open fun budgetItemChange(){
        update()
        uiComponents.update()
        uiComponents.clearInputScreen()
    }

}