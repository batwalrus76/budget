package view.screens.mixed

import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.screens.BaseScreen

abstract class BaseMixedScreen (width: Int, height: Int, uiComponents: ApplicationUIComponents, var component: Component):
        BaseScreen(width, height, uiComponents){

    override fun build() {
        panel = Components.panel()
                .wrapWithBox(false) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .build()
    }

    override fun update(): BudgetState? {
        return super.update()
    }
}