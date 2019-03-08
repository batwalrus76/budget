package control.task

import model.tasks.Priority
import model.tasks.Task
import java.time.LocalDate

class TaskWarriorOutputParser {

    companion object {


        open fun parseString(taskLine: String): Task? {
            var task:Task? = null
            var taskLine0 = taskLine.trimStart().trimEnd()
            taskLine0 = taskLine0.replace("\\s+".toRegex(), ",")
            var taskLineParts = taskLine0.split(',')
            if(taskLineParts.size > 6) {
                var taskLinePartsIndex = 0
                val id = taskLineParts[taskLinePartsIndex++].toInt()
                var tempString = taskLineParts[taskLinePartsIndex++]
                var ageInMinutes = 0
                var priority = Priority.L
                val ageMatch = AGE_REGEX.containsMatchIn(tempString)
                if (ageMatch) {
                    ageInMinutes = processTemporalField(tempString)
                    priority = determinePriority(taskLineParts[taskLinePartsIndex++])
                } else {
                    priority = determinePriority(tempString)
                }
                var due: LocalDate? = null
                var project = taskLineParts[taskLinePartsIndex++]
                tempString = taskLineParts[taskLinePartsIndex++]
                val dueMatch = DATE_REGEX.containsMatchIn(tempString)
                if (dueMatch) {
                    due = LocalDate.parse(tempString)
                } else {
                    taskLinePartsIndex--
                }
                var descriptionStringBuilder = StringBuilder()
                for (i in taskLinePartsIndex..taskLineParts.size - 2) {
                    descriptionStringBuilder.append(taskLineParts[i])
                    descriptionStringBuilder.append(' ')
                }
                var urgency = taskLineParts[taskLineParts.size - 1].toDouble()
                task = Task(id, ageInMinutes, project, due, descriptionStringBuilder.toString().trim(), urgency,
                        priority)
            }
            return task
        }

        fun determinePriority(priorityString: String): Priority {
            var priority: Priority = Priority.L
            try {
                priority = Priority.valueOf(priorityString)
            } catch (exception: IllegalArgumentException){
                ;
            }
            return priority
        }
        
        fun processTemporalField(timeField: String): Int {
            var timeFieldInMinutes: Int = 0
            if(timeField.length > 0) {
                if (timeField.contains('-')) {
                    timeFieldInMinutes = 0
                } else if (timeField.endsWith('h')) {
                    timeFieldInMinutes = timeField.substring(0, timeField.lastIndexOf('h')).toInt() * MINUTES_IN_HOUR
                } else if (timeField.endsWith('d')) {
                    timeFieldInMinutes = timeField.substring(0, timeField.lastIndexOf('d')).toInt() * MINUTES_IN_DAY
                } else if (timeField.endsWith('w')) {
                    timeFieldInMinutes = timeField.substring(0, timeField.lastIndexOf('w')).toInt() * MINUTES_IN_WEEK
                } else if (timeField.endsWith('m')) {
                    timeFieldInMinutes = timeField.substring(0, timeField.lastIndexOf('m')).toInt() * MINUTES_IN_MONTH
                } else if (timeField.endsWith("min")) {
                    timeFieldInMinutes = timeField.substring(0, timeField.lastIndexOf("min")).toInt()
                }
            }
            return timeFieldInMinutes
        }

        open val MINUTES_IN_HOUR = 60
        open val MINUTES_IN_DAY = MINUTES_IN_HOUR * 24
        open val MINUTES_IN_WEEK = MINUTES_IN_DAY * 7
        open val MINUTES_IN_MONTH = MINUTES_IN_DAY * 30
        open val AGE_REGEX = """[0-9]+[h|d|w|m]""".toRegex()
        open val DATE_REGEX = """2[0-9]{3}-[0|1][0-9]-[0-3][0-9]""".toRegex()
    }
}