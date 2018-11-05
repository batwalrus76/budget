package view.items

import model.ApplicationState
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.RadioButtonGroup
import org.hexworks.zircon.api.data.Position
import view.input.InputPanel

abstract class BaseItemsPanel (var width: Int, var height: Int, val component: Component, val inputPanel: InputPanel,
                               var applicationState: ApplicationState) {

    var radioButtonGroup: RadioButtonGroup? = null
    var panel: Panel? = null

    open fun build(){
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.offset1x1())
                .withSize(Sizes.create(this.width-4, this.height-4))
                .build()
    }

    open fun update(){
        this.panel!!.removeComponent(this!!.radioButtonGroup!!)
        radioButtonGroup = Components.radioButtonGroup()
                .withPosition(Position.offset1x1())
                .withSize(Sizes.create(this.width-4, this.height-4))
                .build()
        this.panel!!.addComponent(radioButtonGroup!!)
    }

}