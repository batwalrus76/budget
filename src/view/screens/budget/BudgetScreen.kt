package view.screens.budget

import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.*
import org.hexworks.zircon.api.component.Component
import view.financial.budget.AddHypotheticalItemPanel
import view.financial.budget.MonthlyBudgetPanel
import view.financial.budget.YearlyBudgetPanel
import view.screens.BaseScreen

class BudgetScreen(width: Int, height: Int, var component: Component, uiComponents: ApplicationUIComponents):
    BaseScreen(width, height, uiComponents){

    var monthlyBudgetPanel: MonthlyBudgetPanel? = null
    var yearlyBudgetPanel: YearlyBudgetPanel? = null
    var addHypotheticalItemPanel: AddHypotheticalItemPanel? = null

    override fun build() {
        panel = Components.panel()
                .wrapWithBox(false) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .build()
        panel!!.children.forEach { child -> panel!!.removeComponent(child) }
        monthlyBudgetPanel = MonthlyBudgetPanel(width, (2*height/5+2), uiComponents.applicationState!!)
        monthlyBudgetPanel!!.build()
        monthlyBudgetPanel!!.panel?.let { panel?.addComponent(it) }
        yearlyBudgetPanel = YearlyBudgetPanel(width, (2*height/5+4), monthlyBudgetPanel!!.panel!!, uiComponents.applicationState!!)
        yearlyBudgetPanel!!.build()
        yearlyBudgetPanel!!.panel?.let { panel?.addComponent(it) }
        addHypotheticalItemPanel = AddHypotheticalItemPanel(width, height/5-6, uiComponents)
        addHypotheticalItemPanel!!.build()
        addHypotheticalItemPanel!!.panel?.let { panel?.addComponent(it) }
    }

}