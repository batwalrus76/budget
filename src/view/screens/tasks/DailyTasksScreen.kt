package view.screens.tasks

import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.component.Component
import view.task.DayTaskView
import java.time.LocalDate


class DailyTasksScreen (width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents):
        BaseTaskScreen(width, height, uiComponents, component){

    var dayTaskView: DayTaskView? = null

    override fun update(): BudgetState? {
        dayTaskView?.update(uiComponents.currentLocalDate)
        return super.update()
    }

    override fun build() {
        super.build()
        dayTaskView = DayTaskView(width-(TASK_CONFIGURATION_WIDTH), height,
                Positions.create(0,0), true, this as BaseTaskScreen)
        dayTaskView!!.update(LocalDate.now())
        dayTaskView!!.panel?.let { panel!!.addComponent(it) }
        taskView = dayTaskView!!
    }

}