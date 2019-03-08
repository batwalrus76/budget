package view.screens.tasks

import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.Component
import view.task.ProjectTaskView
import java.time.LocalDate

class ProjectTaskScreen(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents):
        BaseTaskScreen(width, height, uiComponents, component){

    var projectTaskView: ProjectTaskView? = null

    override fun update(): BudgetState? {
        projectTaskView?.update(uiComponents.currentLocalDate)
        return super.update()
    }

    override fun build() {
        super.build()
        projectTaskView = ProjectTaskView(width-(TASK_CONFIGURATION_WIDTH), height,
                Positions.create(0,0), uiComponents.currentProject, this as BaseTaskScreen)
        projectTaskView!!.update(LocalDate.now())
        projectTaskView!!.panel?.let { panel!!.addComponent(it) }
        taskView = projectTaskView!!
    }

}