package com.premfina.esig.selfserviceportal

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import android.widget.*
import com.premfina.selfservice.dto.DocumentDto
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_layout.view.*

class MainActivity : Drawer() {

    /**
     * TODO implement functionality for multiple agreements:
     * - Store an Map<String, List<DocumentDto>> with agreement numbers as key, as we populate and add the cards and spinners
     * - Give the spinners an identification (don't remember property for this extra value) matching the index of the agreement the spinner is for
     * - By this we can get the exact document
     */
    private val documents1: ArrayList<DocumentDto> = arrayListOf(DocumentDto("", "SECCI_AGREEMENT", "02/04/2018 14:36", "https://apps.esignlive.eu/auth?target=https%3A%2F%2Fapps.esignlive.eu%2Ftransaction%2FMsIRYW24fwNX8NBJk2uW6M5_VHM%3D%2Fsign&loginToken=My4zbjk5b2oxcTF6SkU0OVBZbXlNczg3ZFZDc1VPcENiMjNIWFBWSTdqNkViRlc0KzdGWjUyTTFJYmZNRjFiYXA0c1ZCcDFNTE9jeFVYUWR2cmlOVDR6QT09"), DocumentDto("", "Renewal Letter", "02/04/2018 14:36", ""))
    private val documents2: ArrayList<DocumentDto> = arrayListOf(DocumentDto("", "Renewal Letter", "02/04/2018 14:36", ""))
    private val agreements: Array<String> = arrayOf("000000107", "000000108")
    private val allData: Map<String, ArrayList<DocumentDto>> = mapOf(agreements[0] to documents1, agreements[1] to documents2)
    private var selectedDocumentsNames: ArrayList<DocumentDto> = ArrayList(0)
    private var currentAgreement: String = ""
    //val CLASSNAME = this::class.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addToFrame(layoutResID = R.layout.activity_main)

        val linearLayout = LinearLayoutManager(this@MainActivity)
        val recyclerAdapter = CustomRecyclerAdapter(selectedDocumentsNames)


        //Spinner
        val arrayAdapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1)
        arrayAdapter.addAll(allData.keys)

        agreements_spinner.adapter = arrayAdapter
        agreements_spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentAgreement = agreements[position]

                //val listValues = allData[currentAgreement] ?: ArrayList(0)
                recyclerAdapter.refreshDataSet(allData[agreements[position]] ?: ArrayList(0))
                recyclerAdapter.notifyDataSetChanged()
            }
        }

        //RecyclerView
        documents_view.apply {
            layoutManager = linearLayout

            adapter = recyclerAdapter
        }

        //Extra element to go to agreement details
        included_view.isFocusable = false
        val recyclerTextView: TextView = included_view.recycler_text_view
        recyclerTextView.text = getString(R.string.agreement_details)
        //recyclerTextView.isClickable = true
        recyclerTextView.isFocusableInTouchMode = false
        recyclerTextView.setOnClickListener { moreDetails() }
    }

    private fun moreDetails() {
        val intent = Intent(this@MainActivity, DocumentDetailsActivity::class.java)
        intent.putExtra("agreement", currentAgreement)
        for(document in allData[currentAgreement] ?: ArrayList(0))
        {
            if(!TextUtils.isEmpty(document.signedDocumentUrl))
                intent.putExtra("agreement_url", document.signedDocumentUrl)
        }
        startActivity(intent)
    }
}
