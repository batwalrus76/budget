package view.budget

import model.ApplicationState
import model.enums.Recurrence
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Size
import org.hexworks.zircon.api.grid.TileGrid

class MonthlyBudgetPanel(var width: Int, var height: Int, val component: Component,
                         var applicationState: ApplicationState) {

    var panel: Panel? = null

    fun build(){
        panel = Components.panel()
                .wrapWithBox(true)
                .wrapWithShadow(false)
                .withTitle(TITLE)
                .withSize(Size.create(width, height))
                .withPosition(Positions.create(0,1).relativeToBottomOf(component))
                .build()
        var balance: Double = 0.0
        val budgetItems = applicationState?.nonOneTimeBudgetItems
        var previousLabel: Label? = null
        budgetItems?.values?.forEach { budgetItem ->
            var monthlyAmount: Double = 0.0
            var labelString: String? = null
            when(budgetItem.recurrence){
                Recurrence.WEEKLY -> {
                    monthlyAmount = 4*budgetItem.actualAmount
                    labelString = "Occurrence 4x\t"
                }
                Recurrence.BIWEEKLY -> {
                    monthlyAmount = 2*budgetItem.actualAmount
                    labelString = "Occurrence 2x\t"
                }
                Recurrence.MONTHLY -> {
                    monthlyAmount = budgetItem.actualAmount
                    labelString = "Occurrence 1x\t"
                }
            }
            balance = balance + monthlyAmount
            if(labelString != null){
                labelString = String.format("%s\t%s\t\t\t\t\t\t\t\t\tMonthly Amount: %.2f",labelString,budgetItem.toString(),monthlyAmount)
                if(previousLabel != null){
                    previousLabel = Components.label()
                            .withText(labelString)
                            .withPosition(Positions.create(-1,0).relativeToBottomOf(previousLabel!!))
                            .build()
                    panel!!.addComponent(previousLabel!!)
                } else {
                    previousLabel = Components.label()
                            .withText(labelString)
                            .withPosition(Positions.create(1,0))
                            .build()
                    panel!!.addComponent(previousLabel!!)
                }
            }
        }
        val balanceString: String = String.format("\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tMonthly Balance: %.2f", balance)
        if(previousLabel != null){
            previousLabel = Components.label()
                    .withText(balanceString)
                    .withPosition(Positions.create(-1,0).relativeToBottomOf(previousLabel!!))
                    .build()
            panel!!.addComponent(previousLabel!!)
        } else {
            previousLabel = Components.label()
                    .withText(balanceString)
                    .withPosition(Positions.create(1,0))
                    .build()
            panel!!.addComponent(previousLabel!!)
        }

    }

    companion object {
        val TITLE = "Monthly Budget"
    }
}