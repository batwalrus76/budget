package utils

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

        val END_OF_DAY_HOUR: Int = 17
    }

}