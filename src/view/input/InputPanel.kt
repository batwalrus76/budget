package view.input

import model.state.ApplicationState
import model.budget.BudgetAnalysisState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Panel
import view.BudgetPanel

class InputPanel(width: Int, height: Int, var xOffSet: Int, var yOffset: Int, var uiComponents: ApplicationUIComponents,
                 applicationState: ApplicationState) :
        BudgetPanel(width, height, applicationState) {

    override fun update(budgetAnalysisState: BudgetAnalysisState) {

    }

    var addItemPanel:AddItemPanel? = null
    var innerPanel: Panel? = null

    fun update(panel:Panel) {
        this.panel!!.children.forEach { child -> this.panel!!.removeComponent(child) }
        innerPanel = panel
        this.panel!!.addComponent(innerPanel!!)
    }

    fun clear(){
        addItemPanel = AddItemPanel(width-2,height-2, uiComponents, applicationState)
        addItemPanel!!.build()
        this.panel!!.children.forEach { child -> this.panel!!.removeComponent(child) }
        this.innerPanel = addItemPanel!!.panel
        this.panel!!.addComponent(this.innerPanel!!)
    }

    override fun build() {
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(width!!, height!!)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(xOffSet, yOffset))
                .build() // position is always relative to the parent
        addItemPanel = AddItemPanel(width-2,height-2, uiComponents, applicationState)
        addItemPanel!!.build()
        innerPanel = addItemPanel!!.panel
        panel!!.addComponent(this!!.innerPanel!!)
    }

    companion object {
        const val TITLE = "Input Panel"
    }

}