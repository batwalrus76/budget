package view.control

import model.BudgetAnalysisState
import model.enums.View
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import view.BudgetPanel
import view.budgetState.BudgetStatePanel

class MainControlsPanel(width: Int, height: Int, var uIComponents: ApplicationUIComponents):
                                                 BudgetPanel(width, height, uIComponents.applicationState!!) {

    override fun update(budgetAnalysisState: BudgetAnalysisState) {

    }

    var weeklyViewButton: Button? = null
    var budgetViewButton: Button? = null

    override fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(BudgetStatePanel.TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0))
                .build()
        this.weeklyViewButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Weekly")
                .withPosition(Positions.create(0,1))
                .build()
        weeklyViewButton!!.onMouseReleased { uIComponents.switchScreen(View.WEEKLY) }
        this.budgetViewButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Budget")
                .withPosition(Positions.create(1,0).relativeToRightOf(weeklyViewButton!!))
                .build()
        budgetViewButton!!.onMouseReleased { uIComponents.switchScreen(View.BUDGET) }
        this.panel!!.addComponent(weeklyViewButton!!)
        this.panel!!.addComponent(budgetViewButton!!)
    }

    companion object {
        val TITLE:String = "View"
    }
}