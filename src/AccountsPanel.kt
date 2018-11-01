import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import java.time.LocalDateTime

class AccountsPanel {

    var date: LocalDateTime = LocalDateTime.now()
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
    var dateLabel: Label = Components.label()
            .withText("Date: $date")
            .withPosition(Position.create(10,1))
            .build()
    var checkingAccountPanel: Panel = Components.panel()
            .wrapWithBox(true) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withTitle(CHECKING_ACCOUNT_TITLE) // if a panel is wrapped in a box a title can be displayed
            .withSize(Sizes.create(this.width-2, this.height-(this.dateLabel.height+5)))
            .withPosition(Positions.create(DEFAULT_CHECKING_PANEL_X_OFFSET,1+this.dateLabel.height))
            .build()

    fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(this.xOffset,this.yOffset))
                .build() // position is always relative to the parent
        var dateLabel: Label = Components.label()
                .withText("Date: $date")
                .withPosition(Position.create(20,1))
                .build()
        this.panel!!.addComponent(dateLabel)
        this.panel!!.addComponent(checkingAccountPanel)
    }

    companion object {
        val TITLE: String = "Accounts"
        val CHECKING_ACCOUNT_TITLE: String = "Checking Account"
        val DEFAULT_WIDTH: Int = 60
        val DEFAULT_HEIGHT: Int = 49
        val DEFAULT_CHECKING_PANEL_X_OFFSET: Int = 0
        val DEFAULT_X_OFFSET: Int = 100
        val DEFAULT_Y_OFFSET: Int = 1
    }
}