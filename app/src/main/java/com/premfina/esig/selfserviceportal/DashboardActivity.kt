package com.premfina.esig.selfserviceportal

import android.content.Intent
import android.os.Bundle
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.premfina.selfservice.dto.AgreementDto
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.app_bar_drawer.*
import kotlinx.android.synthetic.main.app_bar_drawer.view.*
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener
import lecho.lib.hellocharts.model.*
import org.jetbrains.anko.intentFor
import java.text.NumberFormat

class DashboardActivity : Drawer() {

    private val nf = NumberFormat.getCurrencyInstance()
    //totalToPay.toFloat()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(layoutResID = R.layout.activity_dashboard)

        toolbar.label_text.text = userPreferences.getString("username", "")
        supportActionBar?.setDisplayShowTitleEnabled(false)
        bottom_menu.menu.setGroupCheckable(0, true, true)


        val agreementDtos = jacksonObjectMapper().readValue<Array<AgreementDto>>(agreementsDetails.getString("agreements", "")!!)
        val amountOfAgreements = agreementDtos.size

        var totalToPay = 0.0
        var totalPaid = 0.0
        val columns = ArrayList<Column>(amountOfAgreements)
        val xAxisLabels = ArrayList<AxisValue>(amountOfAgreements)
        val toPayColor = getColor(R.color.fontColor)
        val paidColor = getColor(R.color.colorPrimary)
        for (i in 0 until amountOfAgreements) {
            val agreementDto = agreementDtos[i]
            val toPay = agreementDto.totalAmount.toFloat()
            val paid = agreementDto.clearedBalance.toFloat()
            totalToPay += toPay
            totalPaid += paid


            val subcolumns = ArrayList<SubcolumnValue>(2)
            subcolumns.add(SubcolumnValue(toPay, toPayColor))
            subcolumns.add(SubcolumnValue(paid, paidColor))

            columns.add(Column(subcolumns).setHasLabels(true).setHasLabelsOnlyForSelected(true))
            xAxisLabels.add(AxisValue(i.toFloat()).setLabel(agreementDto.agreementNumber))
        }

        val data = ColumnChartData(columns)
        data.isStacked = true
        data.axisXBottom = Axis(xAxisLabels).setTextColor(R.color.fontColor)
        data.axisYLeft = Axis()
        balance_chart.columnChartData = data
        balance_chart.onValueTouchListener = object : ColumnChartOnValueSelectListener {
            override fun onValueSelected(columnIndex: Int, subcolumnIndex: Int, value: SubcolumnValue?) {
                startActivity(intentFor<AgreementsDetailsActivity>("selectedAgreement" to columnIndex))
            }

            override fun onValueDeselected() {}

        }

        agreements_amount.text = amountOfAgreements.toString()
        to_pay.text = nf.format(totalToPay)
        paid.text = nf.format(totalPaid)

        balance_chart.setOnClickListener { startActivity(Intent(this, AgreementsDetailsActivity::class.java)) }
    }
}