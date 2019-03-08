package view.task

import control.task.TaskWarriorCommandProcessor
import control.task.TaskWarriorOutputParser
import model.tasks.Task
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.kotlin.onSelection
import view.screens.tasks.BaseTaskScreen
import java.time.LocalDate

class ListTaskView(width: Int, height: Int, position: Position, baseTaskScreen: BaseTaskScreen):
        BaseTaskView(width, height, position, baseTaskScreen){


    override fun update(date: LocalDate?) {
        addTaskHeaders()
        var tasks = TaskWarriorCommandProcessor.retrieveTaskData()
        processTasksIntoRadioButtonGroup(tasks)
    }

}