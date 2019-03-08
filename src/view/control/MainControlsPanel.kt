package view.control

import control.PluginController.Companion.BUDGET_PLUGIN_AVAILABLE
import control.PluginController.Companion.TASK_PLUGIN_AVAILABLE
import control.handlers.state.UpdateHandler
import model.financial.budget.BudgetAnalysisState
import model.enums.view.View
import model.financial.account.Account
import model.financial.budget.BudgetItem
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import view.abstracts.BudgetPanel
import java.time.LocalDate

class MainControlsPanel(width: Int, height: Int, var uIComponents: ApplicationUIComponents):
                                                 BudgetPanel(width, height, uIComponents.applicationState!!), UpdateHandler {
    var budgetingLabel: Label? = null
    var weeklyViewButton: Button? = null
    var yearViewButton: Button? = null
    var budgetViewButton: Button? = null
    var calendarDayButton: Button? = null
    var calendarWeekButton: Button? = null
    var calendarMonthButton: Button? = null
    var calendarYearButton: Button? = null
    var taskingLabel: Label? = null
    var taskingListButton: Button? = null
    var dailyTaskingButton: Button? = null
    var weeklyTaskingButton: Button? = null
    var dashboardTaskingButton: Button? = null
    var projectTaskingButton: Button? = null
    var projectsTaskingButton: Button? = null
    var mixedUseLabel: Label? = null
    var mixedDayButton: Button? = null
    var mixedWeekButton: Button? = null
    var mixedDashboardButton: Button? = null
    var mixedListButton: Button? = null

    override fun build(){
        this.panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0))
                .build()
        var position = Positions.create(0,0)
        if(BUDGET_PLUGIN_AVAILABLE) {
            position = buildBudgetControls(position)
        }
        if(TASK_PLUGIN_AVAILABLE) {
            position = buildTaskControls(position)
        }
        if(BUDGET_PLUGIN_AVAILABLE && TASK_PLUGIN_AVAILABLE){
            position = buildMixedUseControls(position)
        }
    }

    open fun buildMixedUseControls(position: Position): Position {
        this.mixedUseLabel = Components.label()
                .withText("Mixed: ")
                .withPosition(position)
                .build()
        this.mixedDayButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Daily")
                .withPosition(Positions.create(1, 0).relativeToRightOf(mixedUseLabel!!))
                .build()
        mixedDayButton!!.onMouseReleased { uIComponents.switchScreen(View.MIXED_DAILY) }
        this.mixedWeekButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Weekly")
                .withPosition(Positions.create(1, 0).relativeToRightOf(mixedDayButton!!))
                .build()
        mixedWeekButton!!.onMouseReleased { uIComponents.switchScreen(View.MIXED_WEEKLY) }
        this.mixedDashboardButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Dashboard")
                .withPosition(Positions.create(1, 0).relativeToRightOf(mixedWeekButton!!))
                .build()
        mixedDashboardButton!!.onMouseReleased { uIComponents.switchScreen(View.MIXED_DASHBOARD) }
        this.mixedListButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("List")
                .withPosition(Positions.create(1, 0).relativeToRightOf(mixedDashboardButton!!))
                .build()
        mixedListButton!!.onMouseReleased { uIComponents.switchScreen(View.MIXED_LIST) }

        this.panel!!.addComponent(mixedUseLabel!!)
        this.panel!!.addComponent(mixedDayButton!!)
        this.panel!!.addComponent(mixedWeekButton!!)
        this.panel!!.addComponent(mixedDashboardButton!!)
        this.panel!!.addComponent(mixedListButton!!)

        return Positions.create(-1, 0).relativeToBottomOf(mixedUseLabel!!)
    }

    open fun buildBudgetControls(position: Position): Position {
        this.budgetingLabel = Components.label()
                .withText("Budgeting: ")
                .withPosition(position)
                .build()
        this.weeklyViewButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Weekly")
                .withPosition(Positions.create(0, 0).relativeToRightOf(budgetingLabel!!))
                .build()
        weeklyViewButton!!.onMouseReleased { uIComponents.switchScreen(View.BUDGET_WEEKLY) }
        this.yearViewButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Year")
                .withPosition(Positions.create(1, 0).relativeToRightOf(weeklyViewButton!!))
                .build()
        yearViewButton!!.onMouseReleased { uIComponents.switchScreen(View.YEAR) }
        this.budgetViewButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Budget")
                .withPosition(Positions.create(1, 0).relativeToRightOf(yearViewButton!!))
                .build()
        budgetViewButton!!.onMouseReleased { uIComponents.switchScreen(View.BUDGET) }
        this.calendarDayButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Calendar Day")
                .withPosition(Positions.create(1, 0).relativeToRightOf(budgetViewButton!!))
                .build()
        calendarDayButton!!.onMouseReleased { uIComponents.switchScreen(View.CALENDAR_DAY) }
        this.calendarWeekButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Calendar Week")
                .withPosition(Positions.create(1, 0).relativeToRightOf(calendarDayButton!!))
                .build()
        calendarWeekButton!!.onMouseReleased { uIComponents.switchScreen(View.CALENDAR_WEEK) }
        this.calendarMonthButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Calendar Month")
                .withPosition(Positions.create(1, 0).relativeToRightOf(calendarWeekButton!!))
                .build()
        calendarMonthButton!!.onMouseReleased { uIComponents.switchScreen(View.CALENDAR_MONTH) }
        this.calendarYearButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Calendar Year")
                .withPosition(Positions.create(1, 0).relativeToRightOf(calendarMonthButton!!))
                .build()
        calendarYearButton!!.onMouseReleased { uIComponents.switchScreen(View.CALENDAR_YEAR) }

        this.panel!!.addComponent(budgetingLabel!!)
        this.panel!!.addComponent(weeklyViewButton!!)
        this.panel!!.addComponent(yearViewButton!!)
        this.panel!!.addComponent(budgetViewButton!!)
        this.panel!!.addComponent(calendarDayButton!!)
        this.panel!!.addComponent(calendarWeekButton!!)
        this.panel!!.addComponent(calendarMonthButton!!)
        this.panel!!.addComponent(calendarYearButton!!)
        return Positions.create(-1, 0).relativeToBottomOf(budgetingLabel!!)
    }

    open fun buildTaskControls(position:Position): Position {
        this.taskingLabel = Components.label()
                .withText("Tasking: ")
                .withPosition(position)
                .build()
        this.taskingListButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("List")
                .withPosition(Positions.create(1, 0).relativeToRightOf(taskingLabel!!))
                .build()
        taskingListButton!!.onMouseReleased { uIComponents.switchScreen(View.TASK_LIST) }
        this.dailyTaskingButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Daily")
                .withPosition(Positions.create(1, 0).relativeToRightOf(taskingListButton!!))
                .build()
        dailyTaskingButton!!.onMouseReleased { uIComponents.switchScreen(View.TASK_DAILY) }
        this.weeklyTaskingButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Weekly")
                .withPosition(Positions.create(1, 0).relativeToRightOf(dailyTaskingButton!!))
                .build()
        weeklyTaskingButton!!.onMouseReleased { uIComponents.switchScreen(View.TASK_WEEKLY) }
        this.dashboardTaskingButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Dashboard")
                .withPosition(Positions.create(1, 0).relativeToRightOf(weeklyTaskingButton!!))
                .build()
        dashboardTaskingButton!!.onMouseReleased { uIComponents.switchScreen(View.TASK_DASHBOARD) }
        this.projectTaskingButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Project")
                .withPosition(Positions.create(1, 0).relativeToRightOf(dashboardTaskingButton!!))
                .build()
        projectTaskingButton!!.onMouseReleased { uIComponents.switchScreen(View.TASK_PROJECT) }
        this.projectsTaskingButton = Components.button()
                .withBoxType(BoxType.DOUBLE)
                .wrapWithBox(false)
                .withText("Projects")
                .withPosition(Positions.create(1, 0).relativeToRightOf(projectTaskingButton!!))
                .build()
        projectsTaskingButton!!.onMouseReleased { uIComponents.switchScreen(View.TASK_PROJECTS) }

        this.panel!!.addComponent(taskingLabel!!)
        this.panel!!.addComponent(taskingListButton!!)
        this.panel!!.addComponent(dailyTaskingButton!!)
        this.panel!!.addComponent(weeklyTaskingButton!!)
        this.panel!!.addComponent(dashboardTaskingButton!!)
        this.panel!!.addComponent(projectTaskingButton!!)
        this.panel!!.addComponent(projectsTaskingButton!!)

        return Positions.create(-1, 0).relativeToBottomOf(taskingLabel!!)
    }

    companion object {
        val TITLE:String = "View"
    }

    override fun update(vararg args: Any) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(budgetAnalysisState: BudgetAnalysisState) {

    }
}