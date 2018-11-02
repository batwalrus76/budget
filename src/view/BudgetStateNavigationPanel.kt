package view

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position

class BudgetStateNavigationPanel {

    var panel: Panel? = null
    var previousBudgetStateLabel: Label? = null
    var modifyBudgetStateLabel: Label? = null
    var nextBudgetStateLabel: Label? = null
    var deleteBudgetStateLabel: Label? = null

    fun build(width: Int, height: Int, component: Component){
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(BUDGET_STATE_NAV_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, height))
                .withPosition(Position.offset1x1().relativeToBottomOf(component))
                .build()
        previousBudgetStateLabel = Components.label()
                .withText("<  Previous Budget State")
                .withPosition(Position.create(0,1))
                .build()
        modifyBudgetStateLabel = Components.label()
                .withText("= Modify Budget State")
                .withPosition(Positions.create(0, 1).relativeToBottomOf(previousBudgetStateLabel!!))
                .build()
        nextBudgetStateLabel = Components.label()
                .withText("> Next Budget State")
                .withPosition(Positions.create(0, 1).relativeToBottomOf(modifyBudgetStateLabel!!))
                .build()
        deleteBudgetStateLabel = Components.label()
                .withText("- Delete Budget State")
                .withPosition(Positions.create(0, 1).relativeToBottomOf(nextBudgetStateLabel!!))
                .build()
        this.panel!!.addComponent(previousBudgetStateLabel!!)
        this.panel!!.addComponent(modifyBudgetStateLabel!!)
        this.panel!!.addComponent(nextBudgetStateLabel!!)
        this.panel!!.addComponent(deleteBudgetStateLabel!!)
    }

    companion object {
        val BUDGET_STATE_NAV_TITLE: String = "Budget State Nav"
    }
}