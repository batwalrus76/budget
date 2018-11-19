package view.budget

import model.ApplicationState
import model.BudgetItem
import model.enums.Recurrence
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.grid.TileGrid

class MonthlyBudgetPanel(var width: Int, var height: Int, var applicationState: ApplicationState) {

    var panel: Panel? = null

    fun build(){
        panel = Components.panel()
                .wrapWithBox(true)
                .wrapWithShadow(false)
                .withTitle(TITLE)
                .withSize(Size.create(width, height))
                .withPosition(Positions.create(0,0))
                .build()
        var balance: Double = 0.0
        val budgetItems = applicationState?.budgetItems
        var previousLabel: Label? = null
        budgetItems?.values?.forEach { budgetItem ->
            when(budgetItem.recurrence){
                Recurrence.DAILY, Recurrence.WEEKLY, Recurrence.BIWEEKLY, Recurrence.MONTHLY ->
                    balance = updateBudgetPanelItems(budgetItem, balance)
            }

        }
        var balanceStringBuilder = StringBuilder()
        for(balanceStringIndex in 0..(BUDGET_ITME_LENGTH+ MONTHLY_AMOUNT_LENGTH)){
            balanceStringBuilder.append('\t')
        }
        balanceStringBuilder.append("   Balance: ")
        balanceStringBuilder.append(String.format("%.2f",balance))
        if(previousLabel != null){
            previousLabel = Components.label()
                    .withText(balanceStringBuilder.toString())
                    .withPosition(Positions.create(-1,0).relativeToBottomOf(panel!!.children.last()))
                    .build()
            panel!!.addComponent(previousLabel!!)
        } else {
            previousLabel = Components.label()
                    .withText(balanceStringBuilder.toString())
                    .withPosition(Positions.create(0,1).relativeToBottomOf(panel!!.children.last()))
                    .build()
            panel!!.addComponent(previousLabel!!)
        }

    }

    fun updateBudgetPanelItems(budgetItem: BudgetItem, balance: Double): Double {
        var monthlyAmount = 0.0
        var budgetItemStringBuilder = StringBuilder()
        when(budgetItem.recurrence){
            Recurrence.WEEKLY -> {
                monthlyAmount = 4*budgetItem.actualAmount
                budgetItemStringBuilder.append("Occurrence 4x\t\t")
            }
            Recurrence.BIWEEKLY -> {
                monthlyAmount = 2*budgetItem.actualAmount
                budgetItemStringBuilder.append("Occurrence 2x\t\t")
            }
            Recurrence.MONTHLY -> {
                monthlyAmount = budgetItem.actualAmount
                budgetItemStringBuilder.append("Occurrence 1x\t\t")
            }
        }
        val newBalance = balance + monthlyAmount
        if(budgetItemStringBuilder.length > 0){
            budgetItemStringBuilder.append(budgetItem.toString())
            for(budgetTextLineIndex in budgetItemStringBuilder.length..BUDGET_ITME_LENGTH){
                budgetItemStringBuilder.append("\t")
            }
            budgetItemStringBuilder.append("Monthly: ")
            budgetItemStringBuilder.append(monthlyAmount)
            for(budgetTextLineIndex in budgetItemStringBuilder.length..(BUDGET_ITME_LENGTH+ MONTHLY_AMOUNT_LENGTH)){
                budgetItemStringBuilder.append("\t")
            }
            budgetItemStringBuilder.append("Balance: ")
            budgetItemStringBuilder.append(String.format("%.2f",newBalance))
            if(!panel!!.children.isEmpty()){
                val label = Components.label()
                        .withText(budgetItemStringBuilder.toString())
                        .withPosition(Positions.create(-1,0).relativeToBottomOf(panel!!.children.last()))
                        .build()
                panel!!.addComponent(label!!)
            } else {
                val label = Components.label()
                        .withText(budgetItemStringBuilder.toString())
                        .withPosition(Positions.create(1,0))
                        .build()
                panel!!.addComponent(label!!)
            }
        }
        return newBalance
    }

    companion object {
        val TITLE = "Monthly Budget"
        val BUDGET_ITME_LENGTH = 100
        val MONTHLY_AMOUNT_LENGTH = 30
    }
}