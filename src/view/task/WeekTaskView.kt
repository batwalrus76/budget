package view.task

import model.view.ApplicationUIComponents
import org.hexworks.zircon.api.Components
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.Sizes
import org.hexworks.zircon.api.component.Panel
import org.hexworks.zircon.api.data.Position
import view.screens.BaseScreen
import view.screens.tasks.BaseTaskScreen
import java.time.LocalDate

class WeekTaskView(width: Int, height: Int, position: Position, var uiComponents: ApplicationUIComponents,
                   baseScreen: BaseScreen): BaseTaskView(width, height, position, baseScreen){

    val seventhHeight = (height/7)-1
    var currentDayTaskViewsHeight = 0

    override fun update(date: LocalDate?) {
        currentDayTaskViewsHeight = 0
        panel.children.forEach { panel.removeComponent(it) }
        var currentLocalDate = uiComponents.currentLocalDate
        var dayTaskView = addDayTaskView(currentLocalDate, Positions.create(0,0))
        for(i in 0..5) {
            currentLocalDate = currentLocalDate.plusDays(1L)
            dayTaskView = addDayTaskView(currentLocalDate,
                    Positions.create(-1, 0).relativeToBottomOf(dayTaskView.panel))
        }
    }

    fun addDayTaskView(date: LocalDate, position: Position): DayTaskView {
        var dayTaskViewHeight = seventhHeight
        if((height-8) - currentDayTaskViewsHeight < seventhHeight){
            dayTaskViewHeight = (height-8) - currentDayTaskViewsHeight
        }
        var dayTaskView = DayTaskView(width-2, dayTaskViewHeight, position, true, baseScreen)
        dayTaskView!!.update(date)
        dayTaskView!!.panel.let { panel.addComponent(it) }
        currentDayTaskViewsHeight += dayTaskViewHeight
        return dayTaskView
    }
}