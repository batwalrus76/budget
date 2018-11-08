package model

import model.BudgetItem.Companion.dateStringParser
import model.BudgetItem.Companion.parseBudgetItemFromJsonObject
import com.beust.klaxon.JsonObject
import model.enums.Recurrence
import java.io.Serializable
import java.time.DayOfWeek
import java.time.LocalDateTime

data class BudgetState(var currentBudgetItems: MutableMap<String, BudgetItem>? = HashMap<String, BudgetItem>(),
                       var startDate: LocalDateTime? = LocalDateTime.now(), var endDate: LocalDateTime? = LocalDateTime.now()): Serializable {

    fun serializeBudgetStateToJson(): String{
        var budgetStateStringBuilder = StringBuilder()
        budgetStateStringBuilder.append("{\n")
        budgetStateStringBuilder.append(String.format("\"%s\": \n%s,\n",
                CURRENT_BUDGET_ITEMS_KEY, serializeMapBudgetItemstoJson(currentBudgetItems)))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", START_DATE_KEY, startDate?.toString()))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\"\n", END_DATE_KEY, endDate?.toString()))
        budgetStateStringBuilder.append("}\n");
        return budgetStateStringBuilder.toString()
    }

    companion object {
        fun deserializeFromJsonObject(budgetStateObj: JsonObject): BudgetState? {
            val currentBudgetItemObj: JsonObject = budgetStateObj.obj(CURRENT_BUDGET_ITEMS_KEY)!!
            var currentBudgetItems: MutableMap<String, BudgetItem> =
                    parseBudgetItemsMapFromJsonObject(currentBudgetItemObj)
            val startDateString: String = budgetStateObj.string(START_DATE_KEY)!!
            var startDate: LocalDateTime = dateStringParser(startDateString)
            val endDateString: String = budgetStateObj.string(END_DATE_KEY)!!
            var endDate: LocalDateTime = dateStringParser(endDateString)
            return BudgetState(currentBudgetItems, startDate, endDate)
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

        private fun parseBudgetItemFromJson(jsonObject: JsonObject): BudgetItem {
            return parseBudgetItemFromJsonObject(jsonObject)!!
        }

        fun serializeMapBudgetItemstoJson(mapBudgetItems: MutableMap<String, BudgetItem>?): String {
            var mapBudgetItemStringBuilder = StringBuilder()
            mapBudgetItemStringBuilder.append("{\n")
            mapBudgetItems?.forEach { (key, value) -> mapBudgetItemStringBuilder.append(String.format("\"%s\": %s,\n", key, value.serializeBudgetItemToJson())) }
            mapBudgetItemStringBuilder.append("}\n")
            return mapBudgetItemStringBuilder.toString()
        }

        val CURRENT_BUDGET_ITEMS_KEY:String = "currentBudgetItems"
        val START_DATE_KEY:String = "startDate"
        val END_DATE_KEY:String = "endDate"


        fun determinePreviousDueLocalDateTime(dayOfWeek: DayOfWeek, currentDateTime: LocalDateTime): LocalDateTime {
            return when (dayOfWeek) {
                DayOfWeek.FRIDAY -> currentDateTime
                DayOfWeek.SATURDAY -> currentDateTime.minusDays(1)
                DayOfWeek.SUNDAY -> currentDateTime.minusDays(2)
                DayOfWeek.MONDAY -> currentDateTime.minusDays(3)
                DayOfWeek.TUESDAY -> currentDateTime.minusDays(4)
                DayOfWeek.WEDNESDAY -> currentDateTime.minusDays(5)
                DayOfWeek.THURSDAY -> currentDateTime.minusDays(6)
            }
        }

        fun determineNextDueLocalDateTime(futureBudgetItemRecurrence: Recurrence, due: LocalDateTime): LocalDateTime {
            return when (futureBudgetItemRecurrence) {
                Recurrence.DAILY -> due.plusDays(Recurrence.DAILY.intervalDaysOrMonths)
                Recurrence.WEEKLY -> due.plusDays(Recurrence.WEEKLY.intervalDaysOrMonths)
                Recurrence.BIWEEKLY -> due.plusDays(Recurrence.BIWEEKLY.intervalDaysOrMonths)
                Recurrence.MONTHLY -> due.plusMonths(Recurrence.MONTHLY.intervalDaysOrMonths)
                Recurrence.YEARLY -> due.plusMonths(Recurrence.YEARLY.intervalDaysOrMonths)
                Recurrence.ONETIME -> due.plusMonths(0L)
            }
        }

    }
}
