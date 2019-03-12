package view.screens.mixed

import control.task.TaskWarriorCommandProcessor
import model.calendar.CalendarEvent
import model.financial.budget.BudgetState
import model.enums.budget.Recurrence
import model.tasks.Priority
import model.tasks.Task
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.data.Position
import view.temporal.calendar.CalendarDayPanel
import view.temporal.event.DayEventPanel
import view.financial.items.ItemConfigurationPanel
import view.mixed.MixedHeaderPanel
import view.screens.BaseScreen
import view.task.DayTaskView
import view.configuration.task.TasksConfigurationPanel
import java.time.LocalDate

class DailyMixedScreen(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents):
        BaseMixedScreen(width, height, uiComponents, component){

    val position = Positions.create(0,0).relativeToBottomOf(component)
    val calendarHeight = (height-((CONFIGURATION_PANEL_HEIGHT *2)+ HEADER_HEIGHT))
    val subPanelWidth = width/2

    val dayEventPanelPosition = Positions.create(0, HEADER_HEIGHT)
    var taskEventPanelPosition: Position? = null
    var calendarPanelPosition: Position? = null

    var headerPanel: MixedHeaderPanel? = MixedHeaderPanel(width-3,HEADER_HEIGHT, Positions.create(0,0))

    var dayEventPanel: DayEventPanel? = null
    var dayTaskView: DayTaskView? = null
    var calendarDayPanel: CalendarDayPanel? = null

    var itemModificationPanel: ItemConfigurationPanel? = null
    var tasksConfigurationPanel: TasksConfigurationPanel? = null

    fun update(date: LocalDate?):BudgetState? {
        panel!!.children.forEach { it -> panel!!.removeComponent(it) }
        panel = Components.panel()
                .wrapWithBox(true) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(position)
                .withTitle(date.toString())
                .build()
        buildHeaderPanel()

        var tasks = TaskWarriorCommandProcessor.retrieveTaskData(due=date)
        var events = date?.let { uiComponents.getDateCalendarEvents(it) }

        events?.let { buildMainPanel(tasks, it, date) }

        buildConfigurationPanels()

        return super.update()
    }

    fun buildHeaderPanel(){
        headerPanel!!.panel?.let { panel!!.addComponent(it) }
    }

    fun buildMainPanel(tasks: MutableList<Task>, events: List<CalendarEvent>, date: LocalDate?){

        dayEventPanel = DayEventPanel(subPanelWidth-1, calendarHeight, uiComponents,
                true, dayEventPanelPosition)
        dayEventPanel!!.build()
        dayEventPanel!!.update(events)
        dayEventPanel!!.panel?.let { panel!!.addComponent(it) }

        taskEventPanelPosition = Positions.create(-1,-1).relativeToRightOf(dayEventPanel!!.panel)
        dayTaskView = DayTaskView(subPanelWidth, calendarHeight/2, taskEventPanelPosition!!, false,
                this as BaseScreen)
        dayTaskView!!.update(date)
        dayTaskView!!.panel?.let { panel!!.addComponent(it) }

        var currentDate = date
        if(currentDate == null){
            currentDate = LocalDate.now()
        }
        var appropriateBudgetAnalysisStates =
                uiComponents.budgetAnalysis[uiComponents.currentViewedBudgetState]?.filter {
                    (it.date!= null && it.date!!.equals(currentDate))
                }
        calendarPanelPosition = Positions.create(-1,-1).relativeToBottomOf(dayTaskView!!.panel)
        calendarDayPanel = CalendarDayPanel(subPanelWidth, calendarHeight/2,
                uiComponents, true, calendarPanelPosition!!, baseScreen = this)
        date?.let {
            if (appropriateBudgetAnalysisStates != null) {
                calendarDayPanel!!.update(it, appropriateBudgetAnalysisStates.toMutableList())
            }
        }
        calendarDayPanel!!.panel?.let { panel!!.addComponent(it) }
    }

    fun buildConfigurationPanels(){
        itemModificationPanel = ItemConfigurationPanel("", LocalDate.now(),
                false, false, 0.0, 0.0, Recurrence.NONE, width-2,
                CONFIGURATION_PANEL_HEIGHT-2, "", "",
                uiComponents.applicationState, true,
                Position.create(-1,0).relativeToBottomOf(dayEventPanel!!.panel),
                true)
        itemModificationPanel!!.build()
        itemModificationPanel!!.panel?.let { panel!!.addComponent(it) }

        tasksConfigurationPanel = TasksConfigurationPanel(null, "", null,
                "", Priority.L, width - 2, CONFIGURATION_PANEL_HEIGHT - 2,
                uiComponents.applicationState, true,
                Position.create(-1, 0).relativeToBottomOf(itemModificationPanel!!.panel!!), true)
        tasksConfigurationPanel!!.build()
        tasksConfigurationPanel!!.panel?.let { panel!!.addComponent(it) }
    }

    override fun update():BudgetState? {
        val date = uiComponents.currentLocalDate
        return update(date)
    }

    companion object {
        val CONFIGURATION_PANEL_HEIGHT = 16
        val HEADER_HEIGHT = 9
    }
}