package view

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Panel

class ItemsPanel {

    var width: Int = DEFAULT_WIDTH
    var height: Int = DEFAULT_HEIGHT
    var xOffset: Int = DEFAULT_X_OFFSET
    var yOffset: Int = DEFAULT_Y_OFFSET

    var panel: Panel = Components.panel()
            .wrapWithBox(true) // panels can be wrapped in a box
            .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
            .wrapWithShadow(false) // shadow can be added
            .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
            .withPosition(Positions.create(this.xOffset,this.yOffset))
            .build()
    var subPanelWidth = this.width-2
    var totalSubPanelHeight = this.height-4
    var individualSubPanelHeight = totalSubPanelHeight/5
    var unreconciledItemsPanelHeight = 3*individualSubPanelHeight+1
    var unreconciledItemsPanel: Panel = Components.panel()
            .wrapWithBox(true) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withTitle(UNRECONCILED_ITEMS_TITLE) // if a panel is wrapped in a box a title can be displayed
            .withSize(Sizes.create(subPanelWidth, unreconciledItemsPanelHeight))
            .withPosition(Positions.create(0,1))
            .build()
    var currentItemSubPanelOffset = unreconciledItemsPanelHeight+1
    var currentItemPanelHeight = 2*individualSubPanelHeight
    var curentItemPanel: Panel = Components.panel()
            .wrapWithBox(true) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withTitle(CURRENT_ITEM_TITLE) // if a panel is wrapped in a box a title can be displayed
            .withSize(Sizes.create(subPanelWidth, currentItemPanelHeight))
            .withPosition(Positions.create(0,currentItemSubPanelOffset))
            .build()

    fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(this.xOffset,this.yOffset))
                .build() // position is always relative to the parent
        this.panel!!.addComponent(unreconciledItemsPanel)
        this.panel!!.addComponent(curentItemPanel)
    }

    companion object {
        val TITLE: String = "Items"
        val UNRECONCILED_ITEMS_TITLE: String = "Unreconciled Items"
        val CURRENT_ITEM_TITLE: String = "Current Item"
        val DEFAULT_WIDTH: Int = 160
        val DEFAULT_HEIGHT: Int = 24
        val DEFAULT_X_OFFSET: Int = 0
        val DEFAULT_Y_OFFSET: Int = 50
    }
}