package view.task

import control.task.TaskWarriorCommandProcessor.Companion.retrieveTaskData
import model.tasks.Task
import org.hexworks.zircon.api.data.Position
import view.screens.BaseScreen
import view.screens.tasks.BaseTaskScreen
import java.time.LocalDate

class ProjectTaskView (width: Int, height: Int, position: Position, var projectName: String? = null,
                       baseScreen: BaseScreen):
        BaseTaskView(width, height, position, baseScreen){

    var tasks: List<Task>? = null
    var updated:Boolean = false

    override fun update(date: LocalDate?) {
        panel.children.forEach { panel.removeComponent(it) }
        if(!updated){
            tasks = retrieveTaskData(project = projectName)
        }
        addTaskHeaders()
        processTasksIntoRadioButtonGroup(this.tasks)
        updated = false
    }

    open fun updateTasks(tasks: List<Task>? = null){
        this.tasks = tasks
        updated = true
    }
}