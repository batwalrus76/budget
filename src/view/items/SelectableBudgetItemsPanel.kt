package view.items

import model.state.ApplicationState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label

class SelectableBudgetItemsPanel(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents,
                                 applicationState: ApplicationState) :
        BaseItemsPanel(width, height, component, uiComponents, applicationState) {

    var headerLabel: Label? = null
    var dividerLabel: Label? = null

    override fun build() {
        super.build()
    }

    override fun update() {
        super.update()
    }
}