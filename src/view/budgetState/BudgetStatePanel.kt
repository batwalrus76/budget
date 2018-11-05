package view.budgetState

import model.ApplicationState
import model.BudgetState
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BudgetStatePanel(var width: Int, var height: Int, var applicationState: ApplicationState) {

    var panel: Panel? = null
    var startDate: LocalDateTime = LocalDateTime.now()
    var endDate: LocalDateTime = LocalDateTime.now()
    var startDateLabel: Label? = null
    var endDateLabel: Label? = null
    var payPeriodItineraryPanel:BudgetItemsPanel? = null


    fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .withTitle(TITLE) // if a panel is wrapped in a box a title can be displayed
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,1))
                .build()
        this.startDateLabel = Components.label()
                .withText("Start Date: ${startDate.format(DateTimeFormatter.ISO_DATE)}")
                .withPosition(Position.offset1x1())
                .build()
        this.endDateLabel = Components.label()
                .withText("End Date: ${endDate.format(DateTimeFormatter.ISO_DATE)}")
                .withPosition(Position.create(startDateLabel!!.width,0).relativeToRightOf(startDateLabel!!))
                .build()
        this.payPeriodItineraryPanel = BudgetItemsPanel(this.width-4,
                this.height-(this.startDateLabel!!.height+5), startDateLabel!!, applicationState)
        this.payPeriodItineraryPanel!!.build()
        this.panel!!.addComponent(startDateLabel!!)
        this.panel!!.addComponent(endDateLabel!!)
        this.panel!!.addComponent(payPeriodItineraryPanel!!.panel!!)
    }

    fun update() {
        var budgetState: BudgetState = this.applicationState.currentPayPeriodBudgetState!!
        payPeriodItineraryPanel!!.update(budgetState)
    }

    companion object {
        val TITLE: String = "BudgetState"
        val ITINERARY: String = "Itinerary"
        val DEFAULT_OFFSET: Position = Positions.create(0,1)
    }

}