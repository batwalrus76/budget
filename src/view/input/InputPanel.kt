package view.input

import model.ApplicationState
import model.BudgetAnalysisState
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.data.Position
import view.BudgetPanel

class InputPanel(width: Int, height: Int, component: Component, applicationState: ApplicationState) :
        BudgetPanel(width, height, component, applicationState) {

    override fun update(budgetAnalysisState: BudgetAnalysisState) {

    }

    var innerComponent:Component? = null

    fun update(component:Component) {
        innerComponent?.let { this.panel!!.removeComponent(it) }
        innerComponent = component
        this.panel!!.addComponent(innerComponent!!)
    }

    override fun build() {
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width!!, this.height!!)) // the size must be smaller than the parent's size
                .withPosition(DEFAULT_POSITION.relativeToBottomOf(component!!))
                .build() // position is always relative to the parent
    }

    companion object {
        val TITLE = "Input Panel"
        val DEFAULT_POSITION: Position = Positions.create(0,0)
    }

}