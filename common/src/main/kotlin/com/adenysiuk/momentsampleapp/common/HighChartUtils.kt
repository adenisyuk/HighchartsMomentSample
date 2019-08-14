package com.adenysiuk.momentsampleapp.common

import android.content.Context
import android.icu.text.DecimalFormatSymbols
import android.text.format.DateFormat
import com.highsoft.highcharts.common.hichartsclasses.*
import com.highsoft.highcharts.core.HIFunction
import java.util.*
import java.util.concurrent.TimeUnit

fun createChartConfig(context: Context): HIOptions {
    return HIOptions().apply {
        series = arrayListOf(HIArea().apply {
            data = ArrayList(generateChartData())
        })

        chart = HIChart().apply {
            panning = true
            zoomType = "x"
            exporting = HIExporting().apply {
                enabled = false
            }
            events = HIEvents().apply {
                load = HIFunction(makeOnLoadJsFunction(Locale.getDefault().language))
            }
        }

        xAxis = ArrayList<HIXAxis>().apply {
            add(HIXAxis().apply {
                type = "datetime"
            })
        }

        tooltip = HITooltip().apply {
            useHTML = true
            formatter = HIFunction(
                tooltipFormatterJsFunction(
                    DateFormat.is24HourFormat(context)
                )
            )
        }
    }
}

fun generateChartData(): List<Array<Any>> {
    val now = System.currentTimeMillis()
    val timeStampStep = TimeUnit.DAYS.toMillis(1)
    return List(60 * 4) { index ->
        arrayOf(now + timeStampStep * index, Math.random())
    }
}

fun tooltipFormatterJsFunction(
    is24HourTimeFormat: Boolean
): String {
    return """
    |function () {
        |var point = this.point;
        |var timezone = 'Europe/Warsaw';

        |var html =
            |'<style>
                |.point-value {
                    |text-align: left;
                    |font-size: 14px; 
                    |white-space: nowrap;
                    |overflow: hidden;
                    |text-overflow: ellipsis;
                |}
                |.point-timestamp {
                    |text-align: left;
                    |font-size: 14px;
                    |white-space: nowrap;
                    |overflow: hidden;
                    |text-overflow: ellipsis;
                |}
            |</style>';

        |html +=
            |`<style>
                |.icon-centered {
                    |background: url(
                        |"data:image/svg+xml;
                        |utf8,
                        |<svg xmlns='http://www.w3.org/2000/svg'>
                            |<circle
                                |cx='5'
                                |cy='5'
                                |r='5'
                                |fill='` + encodeURIComponent(point.color)
                            |+ `'/>
                        |</svg>"
                    |)
                    |left center / 5px no-repeat;
                    |padding-left: 5px;
                |}
            |</style>`;

        |html +=
            |'<div class="icon-centered">
                |<div class="point-value">'
                    |+ Highcharts.numberFormat(
                        |point.y,
                        |2,
                        |'${DecimalFormatSymbols.getInstance().decimalSeparator}',
                        |'${DecimalFormatSymbols.getInstance().groupingSeparator}'
                    |)
                    |+ ' Units'
                |+ '</div>
                |<div class="point-timestamp">'
                    |+ moment
                        |.tz(point.x, timezone)
                        |.format('LL ${getTimeFormatForTooltip(is24HourTimeFormat)}, Z')
                |+ '</div>
            |</div>';

        |return html;
    |}
    """.removeConvenientFormatting()
}

fun getTimeFormatForTooltip(is24HourTimeFormat: Boolean): String {
    return if (is24HourTimeFormat) {
        "H:mm"
    } else {
        "h:mm A"
    }
}

fun makeOnLoadJsFunction(languageCode: String): String {
    return """
    |function () {
        |moment.locale('$languageCode');
    |}
    """.removeConvenientFormatting()
}

private fun String.removeConvenientFormatting() =
    this
        .trimMargin()
        .replace("\n", " ")
