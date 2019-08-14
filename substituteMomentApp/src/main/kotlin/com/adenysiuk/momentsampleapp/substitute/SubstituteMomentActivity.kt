package com.adenysiuk.momentsampleapp.substitute

import android.os.Bundle
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.adenysiuk.momentsampleapp.common.createChartConfig
import com.highsoft.highcharts.common.hichartsclasses.HILang
import com.highsoft.highcharts.core.HIChartView
import java.text.DateFormatSymbols
import java.util.*

class SubstituteMomentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WebView.setWebContentsDebuggingEnabled(true)

        setContentView(R.layout.activity_substitute_moment)

        val hiChartView = findViewById<HIChartView>(R.id.chart)
        hiChartView.lang = HILang().apply {
            shortMonths = ArrayList(DateFormatSymbols.getInstance().shortMonths.asList())
        }
        hiChartView.options = createChartConfig(this)
    }

}