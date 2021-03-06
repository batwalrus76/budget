package view.budgetState

import model.state.ApplicationState
import model.budget.BudgetAnalysisState
import model.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BudgetStatePanel(var width: Int, var height: Int, var uiComponents: ApplicationUIComponents,
                       var applicationState: ApplicationState) {

    var panel: Panel? = null
    var startDate: LocalDate = uiComponents.currentViewedBudgetState!!.startDate!!
    var endDate: LocalDate = uiComponents.currentViewedBudgetState!!.endDate!!
    var startDateLabel: Label? = null
    var endDateLabel: Label? = null
    var payPeriodItineraryPanel:BudgetItemsPanel? = null
    var projectedBalancesButton: Button? = null
    var currentBalancesButton: Button? = null
    var nextBudgetStateButton: Button? = null
    var prevBudgetStateButton: Button? = null


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
                .withPosition(Position.create(1,0).relativeToRightOf(startDateLabel!!))
                .build()
        this.projectedBalancesButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Project Balances")
                .withPosition(Positions.create(1,0).relativeToRightOf(endDateLabel!!))
                .build()
        this.projectedBalancesButton!!.onMouseReleased { this.uiComponents.weeklyOverviewScreen!!.projectBalances() }
        this.currentBalancesButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Current Balances")
                .withPosition(Positions.create(1,0).relativeToRightOf(projectedBalancesButton!!))
                .build()
        this.currentBalancesButton!!.onMouseReleased { this.uiComponents.weeklyOverviewScreen!!.currentBalances() }
        this.prevBudgetStateButton = Components.button()
                .withBoxType(BoxType.SINGLE)
                .wrapWithBox(false)
                .withText("<")
                .withPosition(Positions.create(1,0).relativeToRightOf(currentBalancesButton!!))
                .build()
        this.prevBudgetStateButton!!.onMouseReleased { this.uiComponents.prevBudgetState() }
        this.nextBudgetStateButton = Components.button()
                .withBoxType(BoxType.SINGLE)
                .wrapWithBox(false)
                .withText(">")
                .withPosition(Positions.create(1,0).relativeToRightOf(prevBudgetStateButton!!))
                .build()
        this.nextBudgetStateButton!!.onMouseReleased { this.uiComponents.nextBudgetState() }
        this.payPeriodItineraryPanel = BudgetItemsPanel(this.width-4,
                this.height-(this.startDateLabel!!.height+5), startDateLabel!!, uiComponents, applicationState)
        this.payPeriodItineraryPanel!!.build()
        this.panel!!.addComponent(startDateLabel!!)
        this.panel!!.addComponent(endDateLabel!!)
        this.panel!!.addComponent(projectedBalancesButton!!)
        this.panel!!.addComponent(currentBalancesButton!!)
        this.panel!!.addComponent(prevBudgetStateButton!!)
        this.panel!!.addComponent(nextBudgetStateButton!!)
        this.panel!!.addComponent(payPeriodItineraryPanel!!.panel!!)
    }

    fun update(currentBudgetAnalysisStates: MutableList<BudgetAnalysisState>?) {
        var budgetState: BudgetState = uiComponents.currentViewedBudgetState!!
        this!!.startDateLabel?.let { panel!!.removeComponent(it) }
        this!!.endDateLabel?.let { panel!!.removeComponent(it) }
        this.startDate = budgetState.startDate!!
        this.endDate = budgetState.endDate!!
        this.startDateLabel = Components.label()
                .withText("Start Date: ${startDate.format(DateTimeFormatter.ISO_DATE)}")
                .withPosition(Position.offset1x1())
                .build()
        panel?.addComponent(startDateLabel!!)
        this.endDateLabel = Components.label()
                .withText("End Date: ${endDate.format(DateTimeFormatter.ISO_DATE)}")
                .withPosition(Position.create(0,-1).relativeToRightOf(startDateLabel!!))
                .build()
        panel?.addComponent(endDateLabel!!)
        payPeriodItineraryPanel?.currentBudgetAnalysisStates = currentBudgetAnalysisStates
        payPeriodItineraryPanel!!.update(budgetState)
    }

    companion object {
        val TITLE: String = "BudgetState"
        val ITINERARY: String = "Itinerary"
        val DEFAULT_OFFSET: Position = Positions.create(0,1)
    }

}