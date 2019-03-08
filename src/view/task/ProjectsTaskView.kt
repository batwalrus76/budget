package view.task

import control.task.TaskWarriorCommandProcessor.Companion.retrieveProjectTaskData
import org.hexworks.zircon.api.Positions
import org.hexworks.zircon.api.data.Position
import view.screens.BaseScreen
import view.screens.tasks.BaseTaskScreen
import java.time.LocalDate

class ProjectsTaskView(width: Int, height: Int, position: Position, baseScreen: BaseScreen):
        BaseTaskView(width, height, position, baseScreen){

    override fun update(date: LocalDate?) {
        panel.children.forEach { panel.removeComponent(it) }
        var projectTasksMap = retrieveProjectTaskData()
        var projectTaskViews: MutableList<ProjectTaskView> = ArrayList()
        var position = Positions.create(0,0)
        var projectTaskViewsHeight = height - 1
        var currentPanelHeight = 0
        var numberOfProjects = projectTasksMap.size
        do{
            projectTaskViewsHeight = (height/numberOfProjects)-1
            numberOfProjects--
        } while(numberOfProjects > 0 && height <= 0)
        if(projectTaskViewsHeight > 0) {
            projectTasksMap.keys.sorted().forEach { key ->
                if((height-(currentPanelHeight+1))<=projectTaskViewsHeight){
                    projectTaskViewsHeight = height-(currentPanelHeight+2)
                }
                var projectTaskView = ProjectTaskView(width - 2, projectTaskViewsHeight, position, key, baseScreen)
                projectTaskView.updateTasks(projectTasksMap[key])
                projectTaskView.update()
                projectTaskView.panel.let{panel.addComponent(it!!)}
                projectTaskViews.add(projectTaskView)
                position = Positions.create(-1,0).relativeToBottomOf(projectTaskView.panel)
                currentPanelHeight += (projectTaskViewsHeight + 1)
            }
        }
    }

}