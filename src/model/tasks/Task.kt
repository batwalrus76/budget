package model.tasks

import java.time.LocalDate

class Task(var id:Int? = null, var ageInMinutes:Int? = null, var project:String, var due: LocalDate? = null,
                var description: String, var urgency: Double? = null, var priority: Priority = Priority.L) {


    fun toString(timeConversionRate:Int = 1): String {
        var taskStringBuilder = StringBuilder()
        taskStringBuilder.append(id)
        for(i in 0..(ID_TEXT_LEN+1) - id.toString().length){
            taskStringBuilder.append(' ')
        }
        var ageLength = 0
        if(ageInMinutes != null) {
            val age = Math.ceil(ageInMinutes!!.toDouble()/timeConversionRate)
            taskStringBuilder.append(age)
            ageLength = age.toString().length
        }
        for(i in 0..(AGE_TEXT_LEN+5) - ageLength){
            taskStringBuilder.append(' ')
        }
        taskStringBuilder.append(priority)
        for(i in 0..(PRIORITY_TEXT_LEN) - priority.toString().length){
            taskStringBuilder.append(' ')
        }
        taskStringBuilder.append(project)
        for(i in 0..(PROJECT_TEXT_LEN) - project.length){
            taskStringBuilder.append(' ')
        }
        var dueString = due.toString()
        if(due == null) {
            dueString = ""
        }
        taskStringBuilder.append(dueString)
        for (i in 0..(DUE_TEXT_LEN + 2) - dueString.length) {
            taskStringBuilder.append(' ')
        }
        taskStringBuilder.append(urgency)
        for(i in 0..(URGENCY_TEXT_LEN+1) - urgency.toString().length){
            taskStringBuilder.append(' ')
        }
        taskStringBuilder.append(description)
        return taskStringBuilder.toString()
    }

    companion object {
        val ID_TEXT_LEN = 5
        val AGE_TEXT_LEN = 6
        val PRIORITY_TEXT_LEN = 5
        val PROJECT_TEXT_LEN = 10
        val DUE_TEXT_LEN = 10
        val URGENCY_TEXT_LEN = 5
    }
}