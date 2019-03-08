package control.task

import model.tasks.Priority
import model.tasks.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import java.io.OutputStreamWriter
import java.io.BufferedWriter



class TaskWarriorCommandProcessor {


    companion object {

        val formatter = DateTimeFormatter.ISO_LOCAL_DATE
        val TASK_COMMAND = "/usr/local/bin/task"

        open fun retrieveTaskData(id: Int? = null, ageInMinutes:Int? = null, project:String? = null,
                                  due: LocalDate? = null, priority: Priority? = null)
                : MutableList<Task>{
            var tasks: MutableList<Task> = ArrayList()
            var taskCommandStringBuilder = StringBuilder("/usr/local/bin/task list")
            if(id != null){
                taskCommandStringBuilder.append(String.format(" id:%i",id))
            }
            if(ageInMinutes != null){
                taskCommandStringBuilder.append(String.format(" age:%i",ageInMinutes))
            }
            if(priority != null){
                taskCommandStringBuilder.append(String.format(" priority:%s",priority.toString()))
            }
            if(project != null){
                taskCommandStringBuilder.append(String.format(" project:%s",project.toString()))
            }
            if(due != null){
                taskCommandStringBuilder.append(String.format(" due:%s",due.format(formatter)))
            }
            val proc = Runtime.getRuntime().exec(taskCommandStringBuilder.toString())
            var textLine = 0
            Scanner(proc.inputStream).use {
                while (it.hasNextLine()) {
                    textLine++
                    if(textLine <= 3){
                        it.nextLine()
                    } else if (it.hasNextLine()) {
                        var taskLine = it.nextLine()
                        if(it.hasNextLine()) {
                            tasks = processTaskLine(taskLine, it, tasks)
                        }
                    }
                }
            }
            return tasks
        }

        open fun retrieveProjectTaskData(id: Int? = null, ageInMinutes:Int? = null, project:String? = null,
                                         due: LocalDate? = null, priority: Priority? = null)
                : MutableMap<String, MutableList<Task>> {
            var projectMap: MutableMap<String, MutableList<Task>> = HashMap()
            var tasks: MutableList<Task> = retrieveTaskData(id, ageInMinutes, project, due, priority)
            tasks.forEach { task ->
                var taskList: MutableList<Task> = ArrayList()
                if(projectMap.containsKey(task.project)){
                    taskList = projectMap.get(task.project)!!
                }
                taskList.add(task)
                projectMap.put(task.project,taskList)
            }
            return projectMap
        }

        open fun addModifyTask(task: Task){
            var taskCommandStringBuilder = StringBuilder(TASK_COMMAND)
            if(task.id != null){
                taskCommandStringBuilder.append(String.format(" %d modify", task.id))
            } else {
                taskCommandStringBuilder.append(" add")
            }
            if(task.project != null){
                taskCommandStringBuilder.append(String.format(" project:%s",task.project))
            }
            if(task.due != null){
                taskCommandStringBuilder.append(String.format(" due:%s", task.due!!.format(formatter)))
            }
            if(task.priority != null){
                taskCommandStringBuilder.append(String.format(" priority:%s",task.priority.toString()))
            }
            if(task.description != null){
                taskCommandStringBuilder.append(String.format(" %s",task.description))
            }
            Runtime.getRuntime().exec(taskCommandStringBuilder.toString())
        }

        open fun doneTask(taskId: Int){
            var taskCommandString = String.format("%s %d done", TASK_COMMAND, taskId)
            Runtime.getRuntime().exec(taskCommandString)
        }

        open fun deleteTask(taskId: Int){
            var taskCommandString = String.format("%s %d delete", TASK_COMMAND, taskId)
            var process:Process = Runtime.getRuntime().exec(taskCommandString)
            val bw = BufferedWriter(OutputStreamWriter(process.outputStream))
            bw.write("y\n")
            bw.flush()
        }

        fun processTaskLine(taskLine: String, scanner: Scanner, tasks: MutableList<Task>): MutableList<Task> {
            var newTasks: MutableList<Task> = ArrayList()
            newTasks.addAll(tasks)
            val task = TaskWarriorOutputParser.parseString(taskLine)
            if(task != null) {
                var taskLines = ArrayList<String>()
                var doneProcessingLines = false
                while (scanner.hasNextLine() && !doneProcessingLines) {
                    var currentTaskLine = scanner.nextLine()
                    if (currentTaskLine!!.startsWith("  ")) {
                        taskLines.add(currentTaskLine.trim())
                    } else {
                        doneProcessingLines = true
                        if (currentTaskLine.length > 0) {
                            var descriptionStringBuilder = StringBuilder(task?.description)
                            taskLines?.forEach { it ->
                                descriptionStringBuilder.append(' ')
                                descriptionStringBuilder.append(it.trim())
                            }
                            task?.description = descriptionStringBuilder.toString().trim()
                        }
                        task?.let { it -> newTasks.add(it) }
                        newTasks = processTaskLine(currentTaskLine, scanner, newTasks)
                    }
                }
            }
            return newTasks
        }
    }

}