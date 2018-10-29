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
        var name: String) : Serializable {

    fun validForWeek(start: LocalDateTime, end: LocalDateTime): Boolean{
        var valid: Boolean = false
        if((due.isAfter(start) or due.isEqual(start))and due.isBefore(end)){
            valid = true
        }
        return valid
    }

    override fun toString():String {
        return "id: $id\t Due: $due\tName: $name\tScheduledAmount: $scheduledAmount\tActualAmount: $actualAmount"
    }

    fun serializeBudgetItemToJson(): String {
        var budgetStateStringBuilder: StringBuilder = StringBuilder()
        budgetStateStringBuilder.append("{\n")
        budgetStateStringBuilder.append(String.format("\"%s\": %d,\n", ID_KEY, id))
        budgetStateStringBuilder.append(String.format("\"%s\": %.2f,\n", SCHEDULED_AMOUNT_KEY, scheduledAmount))
        budgetStateStringBuilder.append(String.format("\"%s\": %.2f,\n", ACTUAL_AMOUNT_KEY, actualAmount))
        budgetStateStringBuilder.append(String.format("\"%s\": %s,\n", DUE_KEY, dateConverter.toJson(due)))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", RECURRENCE_KEY, recurrence.name))
        budgetStateStringBuilder.append(String.format("\"%s\": \"%s\",\n", NAME_KEY, name))
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

        fun parseBudgetItemFromJsonObject(value: JsonObject?): BudgetItem? {
            val id: Int = value?.int(ID_KEY)!!
            val scheduledAmount: Double = value?.double(SCHEDULED_AMOUNT_KEY)!!
            val actualAmount: Double = value?.double(ACTUAL_AMOUNT_KEY)!!
            val dueString: String = value?.string(DUE_KEY)!!
            val due:LocalDateTime = dateStringParser(dueString)
            val recurrence: Recurrence = Recurrence.valueOf(value!!.string(RECURRENCE_KEY)!!)
            val name: String = value?.string(NAME_KEY)!!
            return BudgetItem(id, scheduledAmount, actualAmount, due, recurrence, name)
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
