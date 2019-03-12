package view.screens.tasks

import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.Component
import view.temporal.calendar.CalendarMonthPanel
import view.task.DayTaskView
import view.task.ProjectsTaskView
import java.time.LocalDate

class DashboardTaskScreen(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents):
        BaseTaskScreen(width, height, uiComponents, component){

    private var projectsTaskView: ProjectsTaskView? = null
    private var calendarMonthPanel: CalendarMonthPanel? = null
    private var dayTaskView: DayTaskView? = null

    override fun update(): BudgetState? {
        calendarMonthPanel?.panel?.let { panel!!.removeComponent(it) }
        dayTaskView?.panel?.let { panel!!.removeComponent(it) }
        projectsTaskView?.update(uiComponents.currentLocalDate)
        var results =  super.update()

        calendarMonthPanel = CalendarMonthPanel(tasksConfigurationPanelWidth, taskConfigurationPanelHeight*2, uiComponents,
                false, Positions.create(0,0).relativeToBottomOf(super.taskConfigurationPanel!!.panel!!), baseScreen = this)
        calendarMonthPanel!!.update(uiComponents.currentLocalDate)
        calendarMonthPanel!!.panel?.let { panel!!.addComponent(it) }

        dayTaskView = DayTaskView(tasksConfigurationPanelWidth, taskConfigurationPanelHeight*4,
                Positions.create(0,0).relativeToBottomOf(calendarMonthPanel!!.panel!!),
                true, this as BaseTaskScreen)
        dayTaskView!!.update(uiComponents.currentLocalDate)
        dayTaskView!!.panel?.let { panel!!.addComponent(it) }
        return results
    }

    override fun build() {
        super.build()
        projectsTaskView = ProjectsTaskView(width-(TASK_CONFIGURATION_WIDTH), height,
                Positions.create(0,0), this as BaseTaskScreen)
        projectsTaskView!!.update(LocalDate.now())
        projectsTaskView!!.panel?.let { panel!!.addComponent(it) }


        super.taskConfigurationPanelHeight = (height/7)
        taskView = projectsTaskView!!
    }
}