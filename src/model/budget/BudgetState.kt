package model.budget

import com.beust.klaxon.JsonObject
import model.budget.BudgetItem.Companion.dateStringParser
import model.budget.BudgetItem.Companion.parseBudgetItemFromJsonObject
import utils.DateTimeUtils.Companion.currentDate
import utils.DateTimeUtils.Companion.nextThursday
import utils.DateTimeUtils.Companion.previousFriday
import java.io.Serializable
import java.time.LocalDate

data class BudgetState(var startDate:LocalDate = previousFriday(currentDate()), var endDate: LocalDate = nextThursday(currentDate())): Serializable {

    fun serializeBudgetStateToJson(): String{
        var budgetStateStringBuilder = StringBuilder()
        budgetStateStringBuilder.append("{\n")
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", START_DATE_KEY, startDate?.toString()))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\"\n", END_DATE_KEY, endDate?.toString()))
        budgetStateStringBuilder.append("}\n");
        return budgetStateStringBuilder.toString()
    }

    fun isValidForDueDate(dueDate: LocalDate): Boolean {
        return (dueDate.isAfter(startDate)&&dueDate.isBefore(endDate))
    }

    companion object {
        fun deserializeFromJsonObject(budgetStateObj: JsonObject): BudgetState? {
            val startDateString: String = budgetStateObj.string(START_DATE_KEY)!!
            var startDate: LocalDate = dateStringParser(startDateString)
            val endDateString: String = budgetStateObj.string(END_DATE_KEY)!!
            var endDate: LocalDate = dateStringParser(endDateString)
            return BudgetState(startDate, endDate)
        }

        fun serializeMapBudgetItemstoJson(mapBudgetItems: MutableMap<String, BudgetItem>?): String {
            var mapBudgetItemStringBuilder = StringBuilder()
            mapBudgetItemStringBuilder.append("{\n")
            mapBudgetItems?.forEach { (key, value) -> mapBudgetItemStringBuilder.append(String.format("\"%s\": %s,\n", key, value.serializeBudgetItemToJson())) }
            mapBudgetItemStringBuilder =
                    StringBuilder(mapBudgetItemStringBuilder.substring(0, mapBudgetItemStringBuilder.length-2))
            mapBudgetItemStringBuilder.append("}\n")
            return mapBudgetItemStringBuilder.toString()
        }
        
        fun parseBudgetItemsMapFromJsonObject(budgetItemsObject: JsonObject):
                MutableMap<String, BudgetItem> {
            var budgetItemsMap:MutableMap<String, BudgetItem> = HashMap()
            val entries: MutableSet<MutableMap.MutableEntry<String, Any?>> = budgetItemsObject.entries
            for(entry in entries){
                budgetItemsMap.put(entry.key, parseBudgetItemFromJson(entry.value as JsonObject))
            }
            return budgetItemsMap
        }

        fun parseBudgetItemFromJson(jsonObject: JsonObject): BudgetItem {
            return parseBudgetItemFromJsonObject(jsonObject)!!
        }

        val START_DATE_KEY:String = "startDate"
        val END_DATE_KEY:String = "endDate"


    }
}
