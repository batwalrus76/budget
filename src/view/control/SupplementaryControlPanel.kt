package view.control

import model.budget.BudgetAnalysisState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.kotlin.onMouseReleased
import view.BudgetPanel
import java.time.LocalDate

class SupplementaryControlPanel(width: Int, height: Int, var uIComponents: ApplicationUIComponents, var position: Position):
        BudgetPanel(width, height, uIComponents.applicationState!!) {

    var dayLabel = Components.label()
            .withText("Day")
            .withPosition(Positions.create(0,0))
            .build()
    var previousDayButton = Components.button()
            .withText("<")
            .withPosition(Positions.create(1,0).relativeToRightOf(dayLabel))
            .build()
    var nextDayButton = Components.button()
            .withText(">")
            .withPosition(Positions.create(1,0).relativeToRightOf(previousDayButton))
            .build()
    var weekLabel = Components.label()
            .withText("Week")
            .withPosition(Positions.create(1,0).relativeToRightOf(nextDayButton))
            .build()
    var previousWeekButton = Components.button()
            .withText("<")
            .withPosition(Positions.create(1,0).relativeToRightOf(weekLabel))
            .build()
    var nextWeekButton = Components.button()
            .withText(">")
            .withPosition(Positions.create(1,0).relativeToRightOf(previousWeekButton))
            .build()
    var monthLabel = Components.label()
            .withText("Month")
            .withPosition(Positions.create(1,0).relativeToRightOf(nextWeekButton))
            .build()
    var previousMonthButton = Components.button()
            .withText("<")
            .withPosition(Positions.create(1,0).relativeToRightOf(monthLabel))
            .build()
    var nextMonthButton = Components.button()
            .withText(">")
            .withPosition(Positions.create(1,0).relativeToRightOf(previousMonthButton))
            .build()
    var currentDateLabel = Components.label()
            .withText(String.format("Current Date: %s", uIComponents.currentLocalDate.toString()))
            .withPosition(Positions.create(1,0).relativeToRightOf(nextMonthButton))
            .build()

    override fun build() {
        panel = Components.panel()
                .withSize(Sizes.create(width, height))
                .withPosition(position)
                .wrapWithBox(true)
                .build()
        panel!!.addComponent(dayLabel)
        previousDayButton.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.minusDays(1L)) }
        panel!!.addComponent(previousDayButton)
        nextDayButton.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.plusDays(1L)) }
        panel!!.addComponent(nextDayButton)
        panel!!.addComponent(weekLabel)
        previousWeekButton.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.minusWeeks(1L)) }
        panel!!.addComponent(previousWeekButton)
        nextWeekButton.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.plusWeeks(1L)) }
        panel!!.addComponent(nextWeekButton)
        panel!!.addComponent(monthLabel)
        previousMonthButton.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.minusMonths(1L)) }
        panel!!.addComponent(previousMonthButton)
        nextMonthButton.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.plusMonths(1L)) }
        panel!!.addComponent(nextMonthButton)
        panel!!.addComponent(currentDateLabel)
    }

    fun updateCurrentDate(newLocalDate: LocalDate){
        uIComponents.updateDate(newLocalDate)
        panel?.removeComponent(currentDateLabel)
        currentDateLabel = Components.label()
                .withText(String.format("Current Date: %s", uIComponents.currentLocalDate.toString()))
                .withPosition(Positions.create(1,-1).relativeToRightOf(nextMonthButton))
                .build()
        panel?.addComponent(currentDateLabel)
    }

    override fun update(budgetAnalysisState: BudgetAnalysisState) {

    }


}