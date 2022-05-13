package com.fitdback.test.barChartTest

import com.fitdback.database.DataBasket

class BarChartVariables {

    companion object {

        var lastDateOfXIndex: String = "" // 차트 X축 인덱스의 가장 마지막 날짜, "yyMMdd"
        lateinit var dateListOfWeek: MutableList<String>
        var firstTargetData: String? =
            null // firstTargetData: "squat" or "plank" or "sideLateralRaise"
        var secondTargetData: String? =
            null // secondTargetData: "ex_count" or "ex_calorie" or "ex_time"

    }

}