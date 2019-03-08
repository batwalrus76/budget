package view.mixed

import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.data.Position

class MixedHeaderPanel(var width: Int, var height: Int, var position: Position) {

    var panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(position)
                .build()


}