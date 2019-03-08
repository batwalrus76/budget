package view.screens.tasks

import model.financial.budget.BudgetState
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Component
import view.task.ListTaskView

class ListTasksScreen (width: Int, height: Int, component: Component, uiComponents: ApplicationUIComponents):
        BaseTaskScreen(width, height, uiComponents, component){

    var listTaskView: ListTaskView? = null

    override fun update(): BudgetState? {
        listTaskView?.update(uiComponents.currentLocalDate)
        return super.update()
    }

    override fun build() {
        super.build()
        panel = Components.panel()
                .wrapWithBox(false) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .build()
        listTaskView = ListTaskView((2*width/3)-2, height, Positions.create(0,0),
                this as BaseTaskScreen)
        listTaskView!!.panel?.let { panel!!.addComponent(it) }
        taskView = listTaskView!!
    }
}