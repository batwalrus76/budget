package view.payPeriod

import model.budget.BudgetAnalysisState
import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.*
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.kotlin.onSelection
import view.screens.YearPayPeriodBalancesScreen

class PayPeriodPanel(var width: Int, var height: Int, val component: Component,
                      var uiComponents: ApplicationUIComponents,
                      var budgetAnalysis: MutableMap<BudgetState?, MutableList<BudgetAnalysisState>>,
                     var payPeriodIndex: Int, var yearPayPeriodBalancesScreen: YearPayPeriodBalancesScreen) {

    var panel: Panel? = Components.panel()
                        .wrapWithBox(true) // panels can be wrapped in a box
                        .wrapWithShadow(false) // shadow can be added
                        .withTitle(TITLE)
                        .withSize(Sizes.create(this.width-2, this.height-2)) // the size must be smaller than the parent's size
                        .withPosition(Positions.create(1,1))
                        .build()


    var radioButtonGroup: RadioButtonGroup? = null

    var headerString = "       Number |    Start  Date   |     End Date     | Final Balance | Net Balance   |"
    var headerLabel: Label? = null
    var dividerString = "-------------|------------------|------------------|---------------|---------------|"
    var dividerLabel: Label? = null

    var lastBalance = 0.0


    open fun build(){

    }

    open fun update(){
        lastBalance = 0.0
        panel?.children?.forEach { child -> panel?.removeComponent(child) }
        addHeaderLabelsToPanel()
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.create(0,1).relativeToBottomOf(this!!.dividerLabel!!))
                .withSize(Sizes.create(this.width-14, 52))
                .build()
        radioButtonGroup!!.onSelection { it ->
            payPeriodIndex = radioButtonGroup!!.fetchSelectedOption().get().toInt()
            yearPayPeriodBalancesScreen.updatePayPeriodIndex(payPeriodIndex)
        }
        loadButtons()
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

    fun loadButtons() {
        var payPeriodIndex = 0
        uiComponents.applicationState.futureBudgetStates.forEach {
            budgetState ->
            createPayPeriodsRadioButton(budgetState, payPeriodIndex)
            payPeriodIndex = payPeriodIndex + 1
        }
        panel?.addComponent(radioButtonGroup!!)
        var bottomDividerLabel = Components.label()
                .withText(dividerString)
                .withPosition(Positions.create(-1,1).relativeToBottomOf(radioButtonGroup!!))
                .build()
        panel!!.addComponent(bottomDividerLabel)
        var yearTotalBalanceString = String.format("                   |                        |                        | %.2f", lastBalance)
        var yearTotalBalanceLabel = Components.label()
                .withText(yearTotalBalanceString)
                .withPosition(Positions.create(-1,1).relativeToBottomOf(bottomDividerLabel))
                .build()
        panel!!.addComponent(yearTotalBalanceLabel)
    }

    fun createPayPeriodsRadioButton(budgetState: BudgetState, payPeriodIndex: Int) {
        val budgetAnalysisStates = budgetAnalysis?.get(budgetState)
        val lastBudgetState = budgetAnalysisStates?.last()
        var balancesStringBuilder = StringBuilder()
        balancesStringBuilder.append(String.format("   %02d   ", payPeriodIndex))
        balancesStringBuilder.append("|")
        balancesStringBuilder.append(String.format("    %s    |", budgetState.startDate))
        balancesStringBuilder.append(String.format("    %s    |", budgetState.endDate))
        val finalBalance = String.format("          %.2f |", lastBudgetState?.checkingAccountBalance!!-lastBalance)
        balancesStringBuilder.append(finalBalance.substring(finalBalance.length-16))
        val endBalance = String.format("          %.2f |", lastBudgetState?.checkingAccountBalance!!)
        balancesStringBuilder.append(endBalance.substring(endBalance.length-16))
        lastBalance = lastBudgetState?.checkingAccountBalance!!
        radioButtonGroup!!.addOption(payPeriodIndex.toString(),balancesStringBuilder.toString())
    }

    companion object {
        val TITLE: String = "Pay Periods"
    }
}