package model.account

import com.beust.klaxon.JsonObject
import model.budget.BudgetItem
import java.time.LocalDate

data class AccountItem(var date: LocalDate, var name: String, var amount: Double): java.io.Serializable {

    fun serializeAccountItemToJson(): String {
        var accountItemStringBuilder: StringBuilder = StringBuilder()
        accountItemStringBuilder.append("{\n")
        accountItemStringBuilder.append(String.format("\"%s\": \"%s\",\n", NAME_KEY, name))
        accountItemStringBuilder.append(String.format("\"%s\": \"%s\",\n", DATE_KEY, date.toString()))
        accountItemStringBuilder.append(String.format("\"%s\": %.2f \n", AMOUNT_KEY, amount))
        accountItemStringBuilder.append("}\n")
        return accountItemStringBuilder.toString()
    }

    companion object {

        val NAME_KEY = "name"
        val DATE_KEY = "date"
        val AMOUNT_KEY = "amount"

        fun parseAccountItemFromJsonObject(jsonObject: JsonObject): AccountItem {
            val name: String = jsonObject.string(NAME_KEY)!!
            val date: LocalDate = BudgetItem.dateStringParser(jsonObject.string(DATE_KEY))!!
            val amount: Double = jsonObject.double(AMOUNT_KEY)!!
            return AccountItem(date, name, amount)
        }
    }
}