package view.screens

import model.budget.BudgetAnalysisState
import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel

class YearPayPeriodBalancesScreen(var width: Int, var height: Int, var component: Component, var uiComponents: ApplicationUIComponents,
                                  var budgetAnalysis: MutableMap<BudgetState?, MutableList<BudgetAnalysisState>>){

    var panel: Panel? = Components.panel()
            .wrapWithBox(false) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
            .withPosition(Positions.create(0,0).relativeToBottomOf(component))
            .build()

    var headerString = " Pay Period Number |       Start  Date      |         End Date       | Final Balance "
    var headerLabel: Label? = null
    var dividerString = "-------------------|------------------------|------------------------|-------------"
    var dividerLabel: Label? = null

    var lastBalance = 0.0

    fun update(){
        lastBalance = 0.0
        panel?.children?.forEach { child -> panel?.removeComponent(child) }
        addHeaderLabelsToPanel()
        loadLabels()
    }

    fun build() {
        addHeaderLabelsToPanel()
        loadLabels()
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

    fun loadLabels() {
        var payPeriodIndex = 0
        var lastLabel = dividerLabel
        uiComponents.applicationState.futureBudgetStates.forEach {
            budgetState ->
            lastLabel = createPayPeriodsLabel(budgetState, lastLabel!!, payPeriodIndex)
            panel?.addComponent(lastLabel!!)
            payPeriodIndex = payPeriodIndex + 1
        }
        var bottomDividerLabel = Components.label()
                .withText(dividerString)
                .withPosition(Positions.create(0,1).relativeToBottomOf(lastLabel!!))
                .build()
        panel!!.addComponent(bottomDividerLabel)
        var yearTotalBalanceString = String.format("                   |                        |                        | %.2f", lastBalance)
        var yearTotalBalanceLabel = Components.label()
                .withText(yearTotalBalanceString)
                .withPosition(Positions.create(0,1).relativeToBottomOf(bottomDividerLabel))
                .build()
        panel!!.addComponent(yearTotalBalanceLabel)
    }

    fun createPayPeriodsLabel(budgetState: BudgetState, previousComponent: Component, payPeriodIndex: Int): Label {
        val budgetAnalysisStates = budgetAnalysis?.get(budgetState)
        val lastBudgetState = budgetAnalysisStates?.last()
        var balancesStringBuilder = StringBuilder()
        balancesStringBuilder.append(String.format("         %02d        ", payPeriodIndex))
        balancesStringBuilder.append("|")
        balancesStringBuilder.append(String.format("       %s       |", budgetState.startDate))
        balancesStringBuilder.append(String.format("       %s       |", budgetState.endDate))
        balancesStringBuilder.append(String.format(" %.2f \n", lastBudgetState?.checkingAccountBalance!!-lastBalance))
        lastBalance = lastBudgetState?.checkingAccountBalance!!
       return Components.label()
                .withText(balancesStringBuilder.toString())
                .withPosition(Positions.create(0,1).relativeToBottomOf(previousComponent))
                .build()
    }
}