package view.control

import model.budget.BudgetAnalysisState
import model.enums.View
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import view.BudgetPanel
import view.budgetState.BudgetStatePanel

class MainControlsPanel(width: Int, height: Int, var uIComponents: ApplicationUIComponents):
                                                 BudgetPanel(width, height, uIComponents.applicationState!!) {

    override fun update(budgetAnalysisState: BudgetAnalysisState) {

    }

    var weeklyViewButton: Button? = null
    var yearViewButton: Button? = null
    var budgetViewButton: Button? = null
    var calendarDayButton: Button? = null
    var calendarWeekButton: Button? = null
    var calendarMonthButton: Button? = null
    var calendarYearButton: Button? = null

    override fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0))
                .build()
        this.weeklyViewButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Weekly")
                .withPosition(Positions.create(0,0))
                .build()
        weeklyViewButton!!.onMouseReleased { uIComponents.switchScreen(View.WEEKLY) }
        this.yearViewButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Year")
                .withPosition(Positions.create(1,0).relativeToRightOf(weeklyViewButton!!))
                .build()
        yearViewButton!!.onMouseReleased { uIComponents.switchScreen(View.YEAR) }
        this.budgetViewButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Budget")
                .withPosition(Positions.create(1,0).relativeToRightOf(yearViewButton!!))
                .build()
        budgetViewButton!!.onMouseReleased { uIComponents.switchScreen(View.BUDGET) }
        this.calendarDayButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Calendar Day")
                .withPosition(Positions.create(1,0).relativeToRightOf(budgetViewButton!!))
                .build()
        calendarDayButton!!.onMouseReleased { uIComponents.switchScreen(View.CALENDAR_DAY) }
        this.calendarWeekButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Calendar Week")
                .withPosition(Positions.create(1,0).relativeToRightOf(calendarDayButton!!))
                .build()
        calendarWeekButton!!.onMouseReleased { uIComponents.switchScreen(View.CALENDAR_WEEK) }
        this.calendarMonthButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Calendar Month")
                .withPosition(Positions.create(1,0).relativeToRightOf(calendarWeekButton!!))
                .build()
        calendarMonthButton!!.onMouseReleased { uIComponents.switchScreen(View.CALENDAR_MONTH) }
        this.calendarYearButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Calendar Year")
                .withPosition(Positions.create(1,0).relativeToRightOf(calendarMonthButton!!))
                .build()
        calendarYearButton!!.onMouseReleased { uIComponents.switchScreen(View.CALENDAR_YEAR) }
        this.panel!!.addComponent(weeklyViewButton!!)
        this.panel!!.addComponent(yearViewButton!!)
        this.panel!!.addComponent(budgetViewButton!!)
        this.panel!!.addComponent(calendarDayButton!!)
        this.panel!!.addComponent(calendarWeekButton!!)
        this.panel!!.addComponent(calendarMonthButton!!)
        this.panel!!.addComponent(calendarYearButton!!)
    }

    companion object {
        val TITLE:String = "View"
    }
}