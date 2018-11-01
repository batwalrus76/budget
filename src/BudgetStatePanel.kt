import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import java.time.LocalDateTime

class BudgetStatePanel {

    var startDate: LocalDateTime = LocalDateTime.now()
    var endDate: LocalDateTime = LocalDateTime.now()
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
    var startDateLabel: Label = Components.label()
                .withText("Start Date: $startDate")
                .withPosition(Position.create(20,1))
                .build()
    var endDateLabel: Label = Components.label()
                .withText("End Date: $endDate")
                .withPosition(Position.create(60,1))
                .build()
    var payPeriodItineraryPanel: Panel = Components.panel()
            .wrapWithBox(true) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withTitle(ITINERARY) // if a panel is wrapped in a box a title can be displayed
            .withSize(Sizes.create(this.width-10, this.height-(this.startDateLabel.height+10)))
            .withPosition(Positions.create(this.xOffset,this.yOffset+this.startDateLabel.height))
            .build()

    fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(this.xOffset,this.yOffset))
                .build() // position is always relative to the parent
        this.startDateLabel = Components.label()
                .withText("Start Date: $startDate")
                .withPosition(Position.create(10,1))
                .build()
        this.endDateLabel = Components.label()
                .withText("End Date: $endDate")
                .withPosition(Position.create(50,1))
                .build()
        this.panel!!.addComponent(startDateLabel)
        this.panel!!.addComponent(endDateLabel)
        this.panel!!.addComponent(payPeriodItineraryPanel)
    }

    companion object {
        val TITLE: String = "BudgetState"
        val ITINERARY: String = "Itinerary"
        val DEFAULT_WIDTH: Int = 100
        val DEFAULT_HEIGHT: Int = 49
        val DEFAULT_X_OFFSET: Int = 0
        val DEFAULT_Y_OFFSET: Int = 1
    }

}