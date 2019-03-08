package view.task

import control.task.TaskWarriorCommandProcessor
import control.task.TaskWarriorOutputParser.Companion.MINUTES_IN_DAY
import model.tasks.Task
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Label
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.component.RadioButtonGroup
import org.hexworks.zircon.api.data.Position
import org.hexworks.zircon.api.kotlin.onSelection
import view.screens.BaseScreen
import view.screens.tasks.BaseTaskScreen
import java.time.LocalDate

class DayTaskView(width: Int, height: Int, position: Position, var showDate: Boolean = false,
                  baseScreen: BaseScreen): BaseTaskView(width, height, position, baseScreen){

    override fun update(date: LocalDate?) {
        if(showDate) {
            panel = Components.panel()
                    .wrapWithBox(true) // panels can be wrapped in a box
                    .wrapWithShadow(false) // shadow can be added
                    .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
                    .withPosition(position)
                    .withTitle(date.toString())
                    .build()
        }

        addTaskHeaders()

        var tasks = TaskWarriorCommandProcessor.retrieveTaskData(due=date)

        processTasksIntoRadioButtonGroup(tasks)
    }
}