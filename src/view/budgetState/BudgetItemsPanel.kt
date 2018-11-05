package view.budgetState

import model.ApplicationState
import model.BudgetState
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import view.items.BaseItemsPanel

class BudgetItemsPanel(width: Int, height: Int, component: Component, inputComponent: Component,
                       applicationState: ApplicationState) :
                                BaseItemsPanel(width, height, component, applicationState){

    override fun build() {
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(BudgetStatePanel.ITINERARY) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(this.width, this.height))
                .withPosition(BudgetStatePanel.DEFAULT_OFFSET.relativeToBottomOf(component))
                .build()
        super.build()
        this.panel!!.addComponent(radioButtonGroup!!)
    }

    fun update(budgetState: BudgetState) {
        var budgetStateCurrentItems = budgetState.currentBudgetItems
        super.update()
        radioButtonGroup.onSelection { inputPanel.update() }
        budgetStateCurrentItems?.forEach { t, u ->
            radioButtonGroup!!.addOption(t,u.toString())
        }
    }

    fun generateInputForm(): Panel {
        var inputPanel: Input
    }

}