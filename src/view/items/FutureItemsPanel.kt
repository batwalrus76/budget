package view.items

import model.ApplicationState
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component

class FutureItemsPanel (width: Int, height: Int, component: Component, applicationState: ApplicationState) :
        BaseItemsPanel(width, height, component, applicationState){

    override fun build() {
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withTitle(FUTURE_ITEM_TITLE) // if a panel is wrapped in a box a title can be displayed
                .withSize(Sizes.create(width, height))
                .withPosition(Positions.create(0,0).relativeToRightOf(this!!.component!!))
                .build()
        super.build()
        radioButtonGroup?.let { this.panel!!.addComponent(it) }
    }

    override fun update(){
        var futureBudgetItems = applicationState.futureBudgetItems!!.values
        super.update()
        futureBudgetItems?.forEach { futureBudgetItem ->
            radioButtonGroup!!.addOption(futureBudgetItem.name, futureBudgetItem.toNarrowString())
        }
    }

    companion object {
        val FUTURE_ITEM_TITLE: String = "Future Items"
    }

}