package view.task

import control.handlers.state.UpdateHandler
import control.task.TaskWarriorOutputParser
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
import java.time.LocalDate

abstract class BaseTaskView(var width: Int, var height: Int, val position: Position, var baseScreen: BaseScreen):
    UpdateHandler{

    var panel: Panel = Components.panel()
            .wrapWithBox(true) // panels can be wrapped in a box
            .wrapWithShadow(false) // shadow can be added
            .withSize(Sizes.create(this.width, this.height)) // the size must be smaller than the parent's size
            .withPosition(position)
            .build()

    var tasksHeaderLabel: Label? = null
    var dividerLabel: Label? = null
    var tasksRadioButtonGroup: RadioButtonGroup? = null
    var tasksMap: MutableMap<Int, Task> = HashMap()
    var nullTasksLabelBuilder = Components.label()
            .withText("No tasks available")
            .withSize(Sizes.create(20,2))


    abstract fun update(date: LocalDate? = null)

    open fun addTaskHeaders(){
        panel.children.forEach { panel.removeComponent(it) }
        tasksHeaderLabel = Components.label().withText(HEADER_TEXT).withPosition(Positions.create(0,1)).build()
        panel.addComponent(tasksHeaderLabel!!)
        var dividerStringBuilder = StringBuilder()
        for( i in 0..width-20){
            dividerStringBuilder.append('_')
        }
        dividerLabel = Components.label().
                withText(dividerStringBuilder.toString()).
                withPosition(Positions.create(0,0).relativeToBottomOf(tasksHeaderLabel!!))
                .build()
        dividerLabel.let{panel.addComponent(it!!)}
    }

    open fun processTasksIntoRadioButtonGroup(tasks: List<Task>? = null){
        if(tasks != null) {
            val rbgHeight = Math.min(height-7, tasks.size+1)
            tasksRadioButtonGroup = Components.radioButtonGroup()
                    .withPosition(Position.create(0,0).relativeToBottomOf(dividerLabel!!))
                    .withSize(Sizes.create(this.width-10, rbgHeight))
                    .build()
            var availableLines = tasksRadioButtonGroup!!.height-1
            tasks.forEach { task ->
                if (availableLines > 0) {
                    tasksMap.put(task.id!!, task)
                    tasksRadioButtonGroup!!.addOption(task.id.toString(), task.toString(TaskWarriorOutputParser.MINUTES_IN_DAY))
                }
                availableLines--
            }
            tasksRadioButtonGroup?.onSelection { it ->
                baseScreen.uiComponents.currentTask = tasksMap.get(it.key.toInt())!!
                baseScreen.update()
            }
            tasksRadioButtonGroup!!.let { panel.addComponent(it) }
        } else {
            val nullTasksLabel = nullTasksLabelBuilder
                    .withPosition(Positions.create(0,0).relativeToBottomOf(dividerLabel!!))
                    .build()

            nullTasksLabel!!.let { panel.addComponent(it) }
        }
    }
//
//    override fun update(vararg args: Any) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

    companion object {
        val HEADER_TEXT = "      ID    Age(days)    P     Project     Due         Urg       Description"
    }
}