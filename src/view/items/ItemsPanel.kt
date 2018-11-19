package view.items

import model.ApplicationState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position

class ItemsPanel (var width: Int, var height: Int, var component: Component, var uiComponents: ApplicationUIComponents,
                        var applicationState: ApplicationState) {

    var panel: Panel? = null
    var subPanelHeight = height-3
    var subPanelWidth = (width/2)-1
    var unreconciledItemsPanel: UnreconciledItemsPanel? = null

    var futureItemPanel: FutureItemsPanel? = null

    fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(width, height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(this!!.component!!))
                .build()
        this.unreconciledItemsPanel = UnreconciledItemsPanel(subPanelWidth, subPanelHeight, component, uiComponents,
                applicationState)
        this.unreconciledItemsPanel!!.build()
        this.futureItemPanel = FutureItemsPanel(subPanelWidth, subPanelHeight, unreconciledItemsPanel!!.panel!!,
                uiComponents, applicationState)
        this.futureItemPanel!!.build()
        this.panel!!.addComponent(unreconciledItemsPanel!!.panel!!)
        this.panel!!.addComponent(futureItemPanel!!.panel!!)
    }

    fun update() {
        this.unreconciledItemsPanel!!.update()
        this.futureItemPanel!!.update()
    }

    companion object {
        val TITLE: String = "Items"
        val ZERO_OFFSET: Position = Positions.create(0,0)
        val DEFAULT_OFFSET: Position = Positions.create(0,1)
    }
}