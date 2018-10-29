import BudgetItem.Companion.dateStringParser
import BudgetItem.Companion.parseBudgetItemFromJsonObject
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import java.io.Serializable
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

    }

}
