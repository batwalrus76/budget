package view

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position


class CommandsPanel {
    var width: Int = DEFAULT_WIDTH
    var height: Int = DEFAULT_HEIGHT
    var xOffset: Int = DEFAULT_X_OFFSET
    var yOffset: Int = DEFAULT_Y_OFFSET

    var panel = Components.panel()
            .wrapWithBox(true) // panels can be wrapped in a box
            .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
            .wrapWithShadow(false) // shadow can be added
            .withSize(Sizes.create(width, height)) // the size must be smaller than the parent's size
            .withPosition(Positions.create(xOffset,yOffset))
            .build() // position is always relative to the parent

    var userInputLabel: Label = Components.label()
            .withText("User Input:")
            .withPosition(Position.create(0,1))
            .build()

//    var subPanelWidth = (this.width-6)/3
//    var userInputLabelHeight = this.userInputLabel.height
//    var subPanelHeight = this.height-(userInputLabelHeight+6)
//    var budgetStateNavigationPanel = BudgetStateNavigationPanel()
//    var budgetItemManagementPanel = BudgetItemManagementPanel()
//    var accountManagementPanel: Panel = Components.panel()
//            .wrapWithBox(true) // panels can be wrapped in a box
//            .wrapWithShadow(false) // shadow can be added
//            .withTitle(ACCOUNT_MANAGEMENT_TITLE) // if a panel is wrapped in a box a title can be displayed
//            .withSize(Sizes.create(subPanelWidth, subPanelHeight-1))
//            .withPosition(Positions.create(2*(subPanelWidth+1),userInputLabelHeight+2))
//            .build()

    fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(DEFAULT_WIDTH, DEFAULT_HEIGHT)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(DEFAULT_X_OFFSET,DEFAULT_Y_OFFSET))
                .build() // position is always relative to the parent
//        this.budgetStateNavigationPanel.build(subPanelWidth, subPanelHeight, userInputLabel)
//        this.panel!!.addComponent(budgetStateNavigationPanel.panel!!)
//        this.budgetItemManagementPanel.build(subPanelWidth, subPanelHeight, budgetStateNavigationPanel.panel!!)
//        this.panel!!.addComponent(userInputLabel)
//        this.panel!!.addComponent(budgetItemManagementPanel.panel!!)
//        this.panel!!.addComponent(accountManagementPanel)
    }

    companion object {
        val TITLE: String = "Commands"
        val ACCOUNT_MANAGEMENT_TITLE: String = "Account Management"
        val DEFAULT_WIDTH: Int = 160
        val DEFAULT_HEIGHT: Int = 24
        val DEFAULT_X_OFFSET: Int = 0
        val DEFAULT_Y_OFFSET: Int = 74
    }
}