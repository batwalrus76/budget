package control.handlers.view

import model.tasks.Task

interface TaskViewHandler: BaseViewHandler {

    fun handle(task: Task)
    fun handle(project: String)
}