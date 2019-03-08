package view.screens.tasks

import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.task.WeekTaskView

class WeeklyTasksScreen  (width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents):
        BaseTaskScreen(width, height, uiComponents, component){

    var weeklyTaskView: WeekTaskView? = null

    override fun update(): BudgetState? {
        weeklyTaskView?.update()
        return super.update()
    }

    override fun build() {
        panel = Components.panel()
                .wrapWithBox(false) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .build()
        weeklyTaskView = WeekTaskView(width- TASK_CONFIGURATION_WIDTH, height, Positions.create(0,0), uiComponents,
                this as BaseTaskScreen)
        weeklyTaskView!!.panel?.let { panel!!.addComponent(it) }
        this.taskView = weeklyTaskView!!
    }
}