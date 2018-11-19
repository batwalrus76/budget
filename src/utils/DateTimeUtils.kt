package utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

class DateTimeUtils {


    companion object {

        fun currentTime(): LocalDateTime{
            var localDateTime:LocalDateTime = LocalDateTime.now()
            if(localDateTime.toLocalTime().hour > END_OF_DAY_HOUR){
                localDateTime.plusDays(1)
                localDateTime.withHour(8)
                localDateTime.withMinute(0)
            }
            return localDateTime
        }

        fun currentDate(): LocalDate {
            return currentTime().toLocalDate()
        }

        fun previousFriday(localDate: LocalDate): LocalDate {
            var newLocalDate = localDate
            when(newLocalDate.dayOfWeek){
                DayOfWeek.SATURDAY -> newLocalDate = newLocalDate.minusDays(1)
                DayOfWeek.SUNDAY -> newLocalDate = newLocalDate.minusDays(2)
                DayOfWeek.MONDAY -> newLocalDate = newLocalDate.minusDays(3)
                DayOfWeek.TUESDAY -> newLocalDate = newLocalDate.minusDays(4)
                DayOfWeek.WEDNESDAY -> newLocalDate = newLocalDate.minusDays(5)
                DayOfWeek.THURSDAY -> newLocalDate = newLocalDate.minusDays(6)
            }
            return newLocalDate
        }

        fun nextThursday(localDate: LocalDate): LocalDate {
            var newLocalDate = localDate
            when(newLocalDate.dayOfWeek){
                DayOfWeek.FRIDAY -> newLocalDate = newLocalDate.plusDays(6)
                DayOfWeek.SATURDAY -> newLocalDate = newLocalDate.plusDays(5)
                DayOfWeek.SUNDAY -> newLocalDate = newLocalDate.plusDays(4)
                DayOfWeek.MONDAY -> newLocalDate = newLocalDate.plusDays(3)
                DayOfWeek.TUESDAY -> newLocalDate = newLocalDate.plusDays(2)
                DayOfWeek.WEDNESDAY -> newLocalDate = newLocalDate.plusDays(1)
            }
            return newLocalDate
        }

        val END_OF_DAY_HOUR: Int = 17
    }

}