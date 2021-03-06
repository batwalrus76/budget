package model.account

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import model.account.AccountItem.Companion.parseAccountItemFromJsonObject
import java.io.Serializable

data class Account(val name: String, var balance: Double) : Serializable {

    var items: MutableList<AccountItem> = ArrayList()
    var reconciledItems: MutableList<AccountItem> = ArrayList()
    var notes = ""

    fun reconcile() {
        var balance: Double = balance
        while (items.iterator().hasNext()) {
            val item: AccountItem = items.iterator().next()
            balance.plus(item.amount)
            reconciledItems.plus(item)
        }
        items.clear()
    }

    fun serializeAccountToJson(): String {
        var accountStringBuilder = StringBuilder()
        accountStringBuilder.append("{\n")
        accountStringBuilder.append(String.format("\"%s\":\"%s\",\n", NAME_KEY, name))
        accountStringBuilder.append(String.format("\"%s\":%.2f,\n", BALANCE_KEY, balance))
        accountStringBuilder.append(String.format("\"%s\": %s", ITEMS_KEY,serialzeListAccountItem(items)))
        accountStringBuilder.append(String.format("\"%s\": %s", RECONCILED_ITEMS_KEY,serialzeListAccountItem(reconciledItems)))
        accountStringBuilder.append(String.format("\"%s\": \"%s\"\n", NOTES_KEY, notes))
        accountStringBuilder.append("}\n")
        return accountStringBuilder.toString()
    }

    private fun serialzeListAccountItem(accountItems: MutableList<AccountItem>): String {
        var accountItemListStringBuilder = StringBuilder()
        accountItemListStringBuilder.append("[\n")
        accountItems.forEach{accountItem -> accountItemListStringBuilder.append(accountItem.serializeAccountItemToJson()).append(',')}
        accountItemListStringBuilder.deleteCharAt(accountItemListStringBuilder.length-1)
        accountItemListStringBuilder.append("],\n")
        return accountItemListStringBuilder.toString()
    }

    override fun toString(): String {
        var formattedNameStringBuilder: StringBuilder = StringBuilder(name)
        for (nameLength in formattedNameStringBuilder.length..NAME_LENGTH){
            formattedNameStringBuilder.append(' ')
        }
        formattedNameStringBuilder.append(String.format("\tBalance = %.2f",balance))
        return formattedNameStringBuilder.toString()
    }

    fun toString(balance: Double): String {
        var formattedNameStringBuilder: StringBuilder = StringBuilder(name)
        for (nameLength in formattedNameStringBuilder.length..NAME_LENGTH){
            formattedNameStringBuilder.append(' ')
        }
        formattedNameStringBuilder.append(String.format("\tBalance = %.2f",balance))
        return formattedNameStringBuilder.toString()
    }

    companion object {

        val NAME_KEY: String = "name"
        val BALANCE_KEY: String = "balance"
        val ITEMS_KEY: String = "items"
        val RECONCILED_ITEMS_KEY: String = "reconciledItems"
        val NOTES_KEY: String = "notes"
        val NAME_LENGTH: Int = 30

        fun parseAccountFromJsonObject(accountObj: JsonObject): Account {
            val name: String = accountObj.string(NAME_KEY)!!
                val balance: Double = accountObj.double(BALANCE_KEY)!!
            var items: MutableList<AccountItem> = ArrayList()
            val itemsArray: JsonArray<JsonObject>? = accountObj.array(ITEMS_KEY)
            itemsArray?.forEach { jsonObject -> items.add(parseAccountItemFromJsonObject(jsonObject)) }
            var reconciledItems: MutableList<AccountItem> = ArrayList()
            val reconciledItemsAray: JsonArray<JsonObject>? = accountObj.array(RECONCILED_ITEMS_KEY)
            reconciledItemsAray?.forEach{ jsonObject -> reconciledItems.add(parseAccountItemFromJsonObject(jsonObject))}
            val notes:String = accountObj.string(NOTES_KEY)!!
            var account = Account(name, balance)
            account.items = items
            account.reconciledItems = reconciledItems
            account.notes = notes
            return account
        }
    }
}