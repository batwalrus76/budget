enum class Recurrence(val intervalDaysOrMonths: Long) {
    DAILY(1L),
    WEEKLY(7L),
    BIWEEKLY(14L),
    MONTHLY(1L),
    YEARLY(12L),
    ONETIME(0L)
}