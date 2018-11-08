package com.premfina.esig.selfserviceportal.com.premfina.esig.selfserviceportal

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.premfina.esig.selfserviceportal.CustomRecyclerAdapter
import com.premfina.selfservice.dto.AgreementDto
import com.premfina.selfservice.dto.DocumentDto

class AgreementsSpinner(ctx: Context, agreementDtos: Array<AgreementDto>, spinner: Spinner, docView: RecyclerView? = null) {
    lateinit var currentAgreement: AgreementDto

    init {
        val agreements = ArrayList<String>(agreementDtos.size)
        val allData = HashMap<String, AgreementDto>(agreements.size)
        agreementDtos.map { agreement ->
            agreements.add(agreement.agreementNumber)
            allData += (agreement.agreementNumber to agreement)
        }

        val linearLayout = LinearLayoutManager(ctx)
        val recyclerAdapter = CustomRecyclerAdapter(ArrayList(0), ctx)

        //Spinner
        val arrayAdapter = ArrayAdapter<String>(ctx, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1)
        arrayAdapter.addAll(allData.keys)

        spinner.adapter = arrayAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentAgreement = allData[agreements[position]]!!

                recyclerAdapter.refreshDataSet(allData[currentAgreement.agreementNumber]!!.documentDtos.toCollection(ArrayList<DocumentDto>()))
                recyclerAdapter.notifyDataSetChanged()
            }
        }

        //RecyclerView
        docView?.apply {
            layoutManager = linearLayout

            adapter = recyclerAdapter
        }
    }
}