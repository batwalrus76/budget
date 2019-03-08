package view.screens.tasks

import control.task.TaskWarriorCommandProcessor
import model.financial.budget.BudgetState
import model.tasks.Priority
import model.tasks.Task
import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Button
import org.hexworks.zircon.api.component.Component
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.graphics.BoxType
import org.hexworks.zircon.api.kotlin.onMouseReleased
import view.screens.BaseScreen
import view.task.BaseTaskView
import view.configuration.task.TasksConfigurationPanel

abstract class BaseTaskScreen(width: Int, height: Int, uiComponents: ApplicationUIComponents, var component: Component):
        BaseScreen(width, height, uiComponents){

    var taskConfigurationPanel: TasksConfigurationPanel? = null
    var taskConfigurationPanelHeight: Int = height
    var tasksConfigurationPanelWidth: Int = TASK_CONFIGURATION_WIDTH

    var currentTask: Task? = null
    var submitButton: Button? = null
    var doneButton: Button? = null
    var deleteButton: Button? = null
    var taskView: BaseTaskView? = null


    override fun update(): BudgetState? {
        if (panel!!.children != null && panel!!.children.size > 1) {
            panel!!.removeComponent(taskConfigurationPanel!!.panel!!)
            taskConfigurationPanel!!.panel!!.children.forEach { it -> taskConfigurationPanel!!.panel!!.removeComponent(it) }
        }
        if(currentTask == null){
            taskConfigurationPanel = TasksConfigurationPanel(null, "", null, "",
                    Priority.L, tasksConfigurationPanelWidth, taskConfigurationPanelHeight, uiComponents.applicationState,
                    false, Position.create(0, 0).relativeToRightOf(taskView!!.panel), true)
        } else {
            taskConfigurationPanel = TasksConfigurationPanel(currentTask!!.id, currentTask!!.project,
                    currentTask!!.due, currentTask!!.description, currentTask!!.priority, tasksConfigurationPanelWidth,
                    taskConfigurationPanelHeight, uiComponents.applicationState, false,
                    Positions.create(0, 0).relativeToRightOf(taskView!!.panel), true)
        }
        taskConfigurationPanel!!.build()
        if(currentTask == null){
            submitButton = Components.button()
                    .withBoxType(BoxType.BASIC)
                    .withText("Add")
                    .withPosition(Positions.create(taskConfigurationPanel!!.width-10, taskConfigurationPanel!!.height-4))
                    .build()
        } else {
            doneButton = Components.button()
                    .withBoxType(BoxType.BASIC)
                    .withText("Done")
                    .withPosition(Positions.create(taskConfigurationPanel!!.width-30, taskConfigurationPanel!!.height-4))
                    .build()
            deleteButton = Components.button()
                    .withBoxType(BoxType.BASIC)
                    .withText("Delete")
                    .withPosition(Positions.create(taskConfigurationPanel!!.width-20, taskConfigurationPanel!!.height-4))
                    .build()
            submitButton = Components.button()
                    .withBoxType(BoxType.BASIC)
                    .withText("Modify")
                    .withPosition(Positions.create(taskConfigurationPanel!!.width-10, taskConfigurationPanel!!.height-4))
                    .build()
            doneButton!!.let { taskConfigurationPanel!!.panel?.addComponent(it) }
            doneButton!!.onMouseReleased { mouseAction ->
                var task = taskConfigurationPanel!!.generateTask()
                task.id?.let { TaskWarriorCommandProcessor.doneTask(it) }
                taskView!!.update(uiComponents.currentLocalDate)
                currentTask = null
                update()
            }
            deleteButton!!.let { taskConfigurationPanel!!.panel?.addComponent(it) }
            deleteButton!!.onMouseReleased { mouseAction ->
                var task = taskConfigurationPanel!!.generateTask()
                task.id?.let { TaskWarriorCommandProcessor.deleteTask(it) }
                taskView!!.update(uiComponents.currentLocalDate)
                currentTask = null
                update()
            }
        }
        submitButton!!.let { taskConfigurationPanel!!.panel?.addComponent(it) }
        submitButton!!.onMouseReleased { mouseAction ->
            var task = taskConfigurationPanel!!.generateTask()
            TaskWarriorCommandProcessor.addModifyTask(task)
            taskView!!.update(uiComponents.currentLocalDate)
            currentTask = null
            update()
        }
        taskConfigurationPanel!!.panel?.let { panel!!.addComponent(it) }
        return super.update()
    }

    override fun build() {
        panel = Components.panel()
                .wrapWithBox(false) // panels can be wrapped in a box
                .wrapWithShadow(false) // shadow can be added
                .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                .withPosition(Positions.create(0,0).relativeToBottomOf(component))
                .build()
    }

    companion object {
        val TASK_CONFIGURATION_WIDTH = 82
    }
}