package view.financial.payPeriod

import model.financial.budget.BudgetAnalysisState
import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.RadioButtonGroup
import org.hexworks.zircon.api.data.Position

class PayPeriodItemsPanel(var width: Int, var height: Int, val component: Component,
                     var uiComponents: ApplicationUIComponents,
                     var budgetAnalysis: MutableMap<BudgetState?, MutableList<BudgetAnalysisState>>) {


    var panel: Panel? = Components.panel()
            .wrapWithBox(true) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withTitle(TITLE)
            .withSize(Sizes.create(this.width - 2, this.height - 2)) // the size must be smaller than the parent's size
            .withPosition(Positions.create(1, 0).relativeToRightOf(component))
            .build()


    var radioButtonGroup: RadioButtonGroup? = null

    var headerLabel: Label? = null
    var dividerLabel: Label? = null
    var balance = 0.0f
    var budgetState: BudgetState? = null

    open fun build() {

    }

    open fun update(budgetState:BudgetState) {
        this.budgetState = budgetState
        balance = 0.0f
        panel?.children?.forEach { child -> panel?.removeComponent(child) }
        addHeaderLabelsToPanel()
        val budgetAnalysisStates = budgetAnalysis?.get(budgetState)
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(0,1).relativeToBottomOf(this!!.dividerLabel!!))
                .withSize(Sizes.create(this.width-8, budgetAnalysisStates!!.size+1))
                .build()
        //In the future make selection do something
        budgetAnalysisStates?.let { loadButtons(it) }
    }


    fun loadButtons(budgetAnalysisStates: MutableList<BudgetAnalysisState>) {
        budgetAnalysisStates!!.forEach { budgetAnalysisState -> createBudgetAnalysisStateRadioButton(budgetAnalysisState) }
        panel?.addComponent(radioButtonGroup!!)
        var bottomDividerLabel = Components.label()
                .withText(dividerString)
                .withPosition(Positions.create(-2,1).relativeToBottomOf(radioButtonGroup!!))
                .build()
        panel!!.addComponent(bottomDividerLabel)
        var payPeriodTotalBalanceString = String.format("                                |                        |           | %.2f", balance)
        var payPeriodTotalBalanceLabel = Components.label()
                .withText(payPeriodTotalBalanceString)
                .withPosition(Positions.create(-1,1).relativeToBottomOf(bottomDividerLabel))
                .build()
        panel!!.addComponent(payPeriodTotalBalanceLabel)
    }

    fun createBudgetAnalysisStateRadioButton(budgetAnalysisState: BudgetAnalysisState) {
        var budgetItemStringBuilder = StringBuilder()
        var nameStringBuilder = StringBuilder(budgetAnalysisState.budgetItem!!.name)
        for(nmeIndex in budgetAnalysisState.budgetItem!!.name.length..20){
            nameStringBuilder.append(" ")
        }
        budgetItemStringBuilder.append(String.format("   %s   ", nameStringBuilder.toString()))
        budgetItemStringBuilder.append("|")
        budgetItemStringBuilder.append(String.format("       %s       |", budgetAnalysisState.date))
        var amount =
                budgetAnalysisState!!.budgetItem!!.dueDates.find { it.dueDate.equals(budgetAnalysisState.date) }!!.amount
        var amountStringBuilder = StringBuilder(String.format("  %.2f",amount))
        for(amountIndex in String.format("  %.2f",amount).length..10){
            amountStringBuilder.append(" ")
        }
        budgetItemStringBuilder.append(amountStringBuilder.toString())
        balance = (balance + amount).toFloat()
        budgetItemStringBuilder.append(String.format("| %.2f \n", balance ))
        radioButtonGroup!!.addOption(budgetAnalysisState!!.budgetItem!!.name,budgetItemStringBuilder.toString())
    }

    fun addHeaderLabelsToPanel(){
        headerLabel = Components.label()
                .withText(headerString)
                .withPosition(Positions.create(1,1))
                .build()
        panel!!.addComponent(headerLabel!!)
        dividerLabel = Components.label()
                .withText(dividerString)
                .withPosition(Positions.create(0,1).relativeToBottomOf(headerLabel!!))
                .build()
        panel!!.addComponent(dividerLabel!!)
    }

    companion object {
        val TITLE: String = "Pay Period Items"
        var headerString = "                   Name          |        Due  Date       |  Amount   |  Balance "
        var dividerString = "        ------------------------|------------------------|-----------|----------"
    }

}

