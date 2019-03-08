package view.abstracts

import control.handlers.configuration.BaseConfigurationHandler
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position

abstract class BaseConfigurationPanel (var width: Int, var height: Int,
                                        var useHorizontalLayout: Boolean = true,
                                       var position: Position = Positions.create(0,0),
                                       var wrapWithBox: Boolean = false) {

    var panel: Panel? = null

    fun build() {
        panel = Components.panel()
                .wrapWithBox(wrapWithBox)
                .wrapWithShadow(false)
                .withSize(Sizes.create(width,height))
                .withPosition(position)
                .build()
        processPanelComponents()
        updatePanelWithComponents()
        addComponentBehaviours()
    }

    abstract fun processPanelComponents()
    abstract fun updatePanelWithComponents()
    abstract fun addComponentBehaviours()
}