package view.control

import control.PluginController.Companion.BUDGET_PLUGIN_AVAILABLE
import control.PluginController.Companion.TASK_PLUGIN_AVAILABLE
import control.handlers.state.UpdateHandler
import control.task.TaskWarriorCommandProcessor
import model.financial.account.Account
import model.financial.budget.BudgetAnalysisState
import model.financial.budget.BudgetItem
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.kotlin.onMouseReleased
import view.abstracts.BudgetPanel
import java.time.LocalDate

class SupplementaryControlPanel(width: Int, height: Int, var uIComponents: ApplicationUIComponents, var position: Position):
        BudgetPanel(width, height, uIComponents.applicationState!!), UpdateHandler {

    var calendarLabel: Label? = null
    var dayLabel: Label? = null
    var previousDayButton: Button? = null
    var nextDayButton: Button? = null
    var weekLabel: Label? = null
    var previousWeekButton: Button? = null
    var nextWeekButton: Button? = null
    var monthLabel: Label? = null
    var previousMonthButton: Button? = null
    var nextMonthButton: Button? = null
    var currentDateLabel: Label? = null

    var taskingLabel: Label? = null
    var projectLabel: Label? = null
    var projectNameLabel: Label? = null
    var projectButtons: MutableMap<String, Button> = HashMap()

    override fun build() {
        panel = Components.panel()
                .withSize(Sizes.create(width, height))
                .withPosition(position)
                .wrapWithBox(true)
                .build()
        var position = Positions.create(0,0)
        if(BUDGET_PLUGIN_AVAILABLE) {
            position = buildDateComponents(position)
        }
        if(TASK_PLUGIN_AVAILABLE) {
            position = buildTaskingComponents(position)
        }
    }

    private fun buildTaskingComponents(position: Position): Position {
        taskingLabel = Components.label()
                .withText("Tasking: ")
                .withPosition(position)
                .build()
        projectLabel = Components.label()
                .withText("Project: ")
                .withPosition(Positions.create(2,0).relativeToRightOf(taskingLabel!!))
                .build()
        projectNameLabel = Components.label()
                .withText(uIComponents.currentProject)
                .withPosition(Positions.create(2,0).relativeToRightOf(projectLabel!!))
                .build()

        var projectTaskMap = TaskWarriorCommandProcessor.retrieveProjectTaskData()
        var buttonPosition = Positions.create(10,0).relativeToRightOf(projectNameLabel!!)
        var numHorizontalButtons = (width - 30)/PROJECT_BUTTON_WIDTH
        var currentNumHorizontalButtons = 0
        var previousButtonRowLeftMostComponent: Component? = null
        projectTaskMap.entries.forEach { entry ->
            var projectButton =  Components.button()
                        .withText(String.format("%s (%d)",entry.key,entry.value.size))
                        .withPosition(buttonPosition)
                        .build()
            if(currentNumHorizontalButtons == 0){
                previousButtonRowLeftMostComponent = projectButton
            }
            projectButton!!.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.minusDays(1L)) }
            projectButtons.put(entry.key, projectButton)
            currentNumHorizontalButtons++
            if(currentNumHorizontalButtons == numHorizontalButtons){
                buttonPosition = Positions.create(0, 0).relativeToBottomOf(previousButtonRowLeftMostComponent!!)
                currentNumHorizontalButtons = 0
            } else {
                buttonPosition = Positions.create(buttonPosition.x+PROJECT_BUTTON_WIDTH,buttonPosition.y)
            }
        }

        panel!!.addComponent(taskingLabel!!)
        panel!!.addComponent(projectLabel!!)
        panel!!.addComponent(projectNameLabel!!)

        projectButtons.values.forEach { it -> panel!!.addComponent(it) }

        var returnPosition = Positions.create(0,0).relativeToBottomOf(taskingLabel!!)
        if(previousButtonRowLeftMostComponent != null){
            returnPosition = Positions.create(0,0).relativeToBottomOf(previousButtonRowLeftMostComponent!!)
        }
        return returnPosition
    }

    fun buildDateComponents(position: Position): Position{
        calendarLabel = Components.label()
                .withText("Day")
                .withPosition(position)
                .build()
        dayLabel = Components.label()
                .withText("Day")
                .withPosition(Positions.create(2,0).relativeToRightOf(calendarLabel!!))
                .build()

        previousDayButton = Components.button()
                .withText("<")
                .withPosition(Positions.create(1,0).relativeToRightOf(dayLabel!!))
                .build()
        previousDayButton!!.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.minusDays(1L)) }

        nextDayButton = Components.button()
                .withText(">")
                .withPosition(Positions.create(1,0).relativeToRightOf(previousDayButton!!))
                .build()
        nextDayButton!!.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.plusDays(1L)) }

        weekLabel = Components.label()
                .withText("Week")
                .withPosition(Positions.create(1,0).relativeToRightOf(nextDayButton!!))
                .build()

        previousWeekButton = Components.button()
                .withText("<")
                .withPosition(Positions.create(1,0).relativeToRightOf(weekLabel!!))
                .build()
        previousWeekButton!!.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.minusWeeks(1L)) }

        nextWeekButton = Components.button()
                .withText(">")
                .withPosition(Positions.create(1,0).relativeToRightOf(previousWeekButton!!))
                .build()
        nextWeekButton!!.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.plusWeeks(1L)) }

        monthLabel = Components.label()
                .withText("Month")
                .withPosition(Positions.create(1,0).relativeToRightOf(nextWeekButton!!))
                .build()

        previousMonthButton = Components.button()
                .withText("<")
                .withPosition(Positions.create(1,0).relativeToRightOf(monthLabel!!))
                .build()
        previousMonthButton!!.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.minusMonths(1L)) }

        nextMonthButton = Components.button()
                .withText(">")
                .withPosition(Positions.create(1,0).relativeToRightOf(previousMonthButton!!))
                .build()
        nextMonthButton!!.onMouseReleased { updateCurrentDate(uIComponents.currentLocalDate.plusMonths(1L)) }

        currentDateLabel = Components.label()
                .withText(String.format("Current Date: %s", uIComponents.currentLocalDate.toString()))
                .withPosition(Positions.create(1,0).relativeToRightOf(nextMonthButton!!))
                .build()

        panel!!.addComponent(calendarLabel!!)
        panel!!.addComponent(dayLabel!!)
        panel!!.addComponent(previousDayButton!!)
        panel!!.addComponent(nextDayButton!!)
        panel!!.addComponent(weekLabel!!)
        panel!!.addComponent(previousWeekButton!!)
        panel!!.addComponent(nextWeekButton!!)
        panel!!.addComponent(monthLabel!!)
        panel!!.addComponent(previousMonthButton!!)
        panel!!.addComponent(nextMonthButton!!)
        panel!!.addComponent(currentDateLabel!!)
        return Positions.create(-1,0).relativeToBottomOf(calendarLabel!!)
    }

    fun updateCurrentDateLabel(){
        currentDateLabel?.let { panel?.removeComponent(it) }
        currentDateLabel = Components.label()
                .withText(String.format("Current Date: %s", uIComponents.currentLocalDate.toString()))
                .withPosition(Positions.create(1,-1).relativeToRightOf(this!!.nextMonthButton!!))
                .build()
        panel?.addComponent(currentDateLabel!!)
    }

    fun updateCurrentDate(newLocalDate: LocalDate){
        uIComponents.updateDate(newLocalDate)
        updateCurrentDateLabel()
    }

    fun updateCurrentProject(projectName: String){
        uIComponents.updateCurrentProject(projectName)

    }

//    override fun update(vararg args: Any) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

    override fun update(budgetAnalysisState: BudgetAnalysisState) {

    }

    companion object {
        val PROJECT_BUTTON_WIDTH = 20
    }

}