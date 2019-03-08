package view.screens.tasks

import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.Component
import view.task.ProjectsTaskView
import java.time.LocalDate

class ProjectsTaskScreen(width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents):
        BaseTaskScreen(width, height, uiComponents, component){

    var projectsTaskView: ProjectsTaskView? = null

    override fun update(): BudgetState? {
        projectsTaskView?.update(uiComponents.currentLocalDate)
        return super.update()
    }

    override fun build() {
        super.build()
        projectsTaskView = ProjectsTaskView(width-(TASK_CONFIGURATION_WIDTH), height,
                Positions.create(0,0), this as BaseTaskScreen)
        projectsTaskView!!.update(LocalDate.now())
        projectsTaskView!!.panel?.let { panel!!.addComponent(it) }
        taskView = projectsTaskView!!
    }

}