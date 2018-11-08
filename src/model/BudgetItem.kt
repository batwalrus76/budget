package model

import model.json.KlaxonDate
import model.json.KlaxonRecurrence
import model.enums.Recurrence
import com.beust.klaxon.*
import java.io.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class BudgetItem @JvmOverloads constructor(
        @Json(name="id")
        var id: Int,
        @Json(name="scheduledAmount")
        var scheduledAmount: Double,
        @Json(name="actualAmount")
        var actualAmount: Double,
        @Json(name="due")
        @KlaxonDate
        var due: LocalDateTime,
        @Json(name="recurrence")
        @KlaxonRecurrence
        var recurrence: Recurrence,
        @Json(name="name")
        var name: String,
        @Json(name="transferredToSavingsAccountName")
        var transferredToSavingsAccountName: String?,
        @Json(name="transferredToCreditAccountName")
        var transferredToCreditAccountName: String?) : Serializable {

    fun validForWeek(start: LocalDateTime, end: LocalDateTime): Boolean{
        var valid: Boolean = false
        if((due.isAfter(start) or due.isEqual(start))and due.isBefore(end)){
            valid = true
        }
        return valid
    }


    override fun toString():String {
        var formattedNameStringBuilder: StringBuilder = StringBuilder(name)
        for (nameLength in formattedNameStringBuilder.length..NAME_LENGTH){
            formattedNameStringBuilder.append(' ')
        }
        var scheduledAmountStringBuilder: StringBuilder = StringBuilder(""+scheduledAmount)
        for (nameLength in scheduledAmountStringBuilder.length..AMOUNT_LENGTH){
            scheduledAmountStringBuilder.append(' ')
        }
        return "Due: ${due.format(DateTimeFormatter.ISO_DATE)}\tName: ${formattedNameStringBuilder.substring(0, NAME_LENGTH)}\tScheduled: ${scheduledAmountStringBuilder}\tActual: $actualAmount"
    }

    fun toNarrowString():String {
        var formattedNameStringBuilder: StringBuilder = StringBuilder(name)
        for (nameLength in formattedNameStringBuilder.length..NAME_LENGTH){
            formattedNameStringBuilder.append(' ')
        }
        var scheduledAmountStringBuilder: StringBuilder = StringBuilder(""+scheduledAmount)
        for (nameLength in scheduledAmountStringBuilder.length..AMOUNT_LENGTH){
            scheduledAmountStringBuilder.append(' ')
        }
        var transferString = "[x]"
        if(transferredToCreditAccountName.equals("null") && transferredToSavingsAccountName.equals("null")){
            transferString = "[ ]"
        }
        return "${due.format(DateTimeFormatter.ISO_DATE)}\t${formattedNameStringBuilder.substring(0, NAME_LENGTH)}\txfer: ${transferString}\tScheduled: ${scheduledAmountStringBuilder}\t"
    }

    fun serializeBudgetItemToJson(): String {
        var budgetStateStringBuilder = StringBuilder()
        budgetStateStringBuilder.append("{\n")
        budgetStateStringBuilder.append(String.format("\"%s\": %d,\n", ID_KEY, id))
        budgetStateStringBuilder.append(String.format("\"%s\": %.2f,\n", SCHEDULED_AMOUNT_KEY, scheduledAmount))
        budgetStateStringBuilder.append(String.format("\"%s\": %.2f,\n", ACTUAL_AMOUNT_KEY, actualAmount))
        budgetStateStringBuilder.append(String.format("\"%s\": %s,\n", DUE_KEY, dateConverter.toJson(due)))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", RECURRENCE_KEY, recurrence.name))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", NAME_KEY, name))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", SAVINGS_ACCOUNT_NAME_KEY, transferredToSavingsAccountName))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", CREDIT_ACCOUNT_NAME_KEY, transferredToCreditAccountName))
        budgetStateStringBuilder.append("}\n")
        return budgetStateStringBuilder.toString()
    }

    companion object {

        val ID_KEY = "id"
        val SCHEDULED_AMOUNT_KEY = "scheduledAmount"
        val ACTUAL_AMOUNT_KEY = "actualAmount"
        val DUE_KEY = "due"
        val RECURRENCE_KEY = "recurrence"
        val NAME_KEY = "name"
        val SAVINGS_ACCOUNT_NAME_KEY = "transferToSavingsAccount"
        val CREDIT_ACCOUNT_NAME_KEY = "transferToCreditAccount"
        val NAME_LENGTH=20
        val AMOUNT_LENGTH=8

        fun parseBudgetItemFromJsonObject(value: JsonObject?): BudgetItem? {
            val id: Int = value?.int(ID_KEY)!!
            val scheduledAmount: Double = value?.double(SCHEDULED_AMOUNT_KEY)!!
            val actualAmount: Double = value?.double(ACTUAL_AMOUNT_KEY)!!
            val dueString: String = value?.string(DUE_KEY)!!
            val due:LocalDateTime = dateStringParser(dueString)
            val recurrence: Recurrence = Recurrence.valueOf(value!!.string(RECURRENCE_KEY)!!)
            val name: String = value?.string(NAME_KEY)!!
            var transferredToSavingsAccountName: String? = null
            if(value.containsKey(SAVINGS_ACCOUNT_NAME_KEY)) {
                transferredToSavingsAccountName = value?.string(SAVINGS_ACCOUNT_NAME_KEY)!!
            }
            var transferredToCreditAccountName: String? = null
            if(value.containsKey(SAVINGS_ACCOUNT_NAME_KEY)) {
                transferredToCreditAccountName = value?.string(CREDIT_ACCOUNT_NAME_KEY)!!
            }
            return BudgetItem(id, scheduledAmount, actualAmount, due, recurrence, name, transferredToSavingsAccountName,
                    transferredToCreditAccountName)
        }

        fun dateStringParser(dateString: String?): LocalDateTime{
            var localDateTime: LocalDateTime? = null
            if(dateString?.contains('.',true)!!){
                localDateTime =
                        LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS"))
            } else {
                localDateTime =
                        LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm"))
            }
            return localDateTime
        }

        val dateConverter = object: Converter {
            override fun canConvert(cls: Class<*>)
                    = cls == LocalDateTime::class.java

            override fun fromJson(jv: JsonValue) =
                    if (jv.string != null) {
                        dateStringParser(jv.string!!)
                    } else {
                        throw KlaxonException("Couldn't parse date: ${jv.string}")
                    }

            override fun toJson(o: Any) =
                    '\"' + o.toString() + '\"'

        }

        val recurenceConverter = object: Converter {
            override fun canConvert(cls: Class<*>)
                    = cls == Recurrence::class.java

            override fun fromJson(jv: JsonValue): Recurrence =
                    if (jv.string != null) {
                        val jvString = jv.string
                        Recurrence.valueOf(jvString!!)
                    } else {
                        throw KlaxonException("Couldn't parse date: ${jv.string}")
                    }

            override fun toJson(o: Any): String = if(o is Recurrence) {
                    String.format("\"recurrence\" : %s", o.name)
                } else {
                    ""
                }
        }
    }

}
