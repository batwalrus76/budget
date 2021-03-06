package view.screens

import model.budget.BudgetAnalysisState
import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import view.payPeriod.PayPeriodItemsPanel
import view.payPeriod.PayPeriodPanel
import kotlin.properties.Delegates

class YearPayPeriodBalancesScreen(width: Int, height: Int, var component: Component, uiComponents: ApplicationUIComponents,
                                  var budgetAnalysis: MutableMap<BudgetState?, MutableList<BudgetAnalysisState>>):
    BaseScreen(width, height, uiComponents){

    var payPeriodIndex = 0
    var payPeriodPanel: PayPeriodPanel =
            PayPeriodPanel((width / 2) - 2, height - 3, component, uiComponents, budgetAnalysis, payPeriodIndex, this)
    var payPeriodItemsPanel: PayPeriodItemsPanel =
            PayPeriodItemsPanel((width / 2) - 2, height - 3, payPeriodPanel!!.panel!!, uiComponents, budgetAnalysis)

    fun updatePayPeriodIndex(index: Int){
        payPeriodIndex = index
        payPeriodItemsPanel.update(uiComponents.applicationState.futureBudgetStates[index])
    }

    override fun update():BudgetState{
        this.panel!!.children.forEach { it -> this.panel!!.removeComponent(it) }
        payPeriodPanel.update()
        payPeriodItemsPanel.update(uiComponents.applicationState.futureBudgetStates[payPeriodIndex])
        this.panel!!.addComponent(payPeriodPanel.panel!!)
        this.panel!!.addComponent(payPeriodItemsPanel.panel!!)
        return uiComponents.currentViewedBudgetState!!
    }

    override fun build() {
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle("Yearly Pay Period Balance View")
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .build()
        payPeriodPanel.build()
        payPeriodItemsPanel.build()
    }

}