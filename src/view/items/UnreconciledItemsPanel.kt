package view.items

import model.ApplicationState
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component

class UnreconciledItemsPanel (width: Int, height: Int, component: Component, applicationState: ApplicationState) :
        BaseItemsPanel(width, height, component, applicationState){

    override fun build() {
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(UNRECONCILED_ITEMS_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, height))
                .withPosition(Positions.create(0,1))
                .build()
        super.build()
        radioButtonGroup?.let { this.panel!!.addComponent(it) }
    }

    override fun update(){
        var unreconciledItems = applicationState.pastUnreconciledBudgetItems!!.values
        super.update()
        unreconciledItems?.forEach { unreconciledItem ->
            radioButtonGroup!!.addOption(unreconciledItem.name, unreconciledItem.toNarrowString())
        }
    }

    companion object {
        val UNRECONCILED_ITEMS_TITLE: String = "Unreconciled Items"
    }

}