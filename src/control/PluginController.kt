package control

import control.ApplicationStateManager.Companion.DEFAULT_STATE_FILE
import control.task.TaskWarriorCommandProcessor.Companion.TASK_COMMAND
import java.io.File

class PluginController {


    companion object {

        var TASK_PLUGIN_AVAILABLE: Boolean = testTaskPluginAvailability()
        var BUDGET_PLUGIN_AVAILABLE: Boolean = testBudgetPluginAvailability()

        open fun testTaskPluginAvailability(): Boolean{
            return File(TASK_COMMAND).exists()
        }

        open fun testBudgetPluginAvailability(): Boolean{
            return DEFAULT_STATE_FILE?.exists()
        }
    }
}