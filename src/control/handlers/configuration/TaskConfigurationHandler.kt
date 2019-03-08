package control.handlers.configuration

import model.tasks.Task

interface TaskConfigurationHandler: BaseConfigurationHandler {

    fun handleConfigurationTask(task: Task)

}