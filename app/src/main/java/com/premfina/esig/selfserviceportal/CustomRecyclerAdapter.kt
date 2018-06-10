package com.premfina.esig.selfserviceportal

import android.content.Intent
import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.premfina.selfservice.dto.DocumentDto

class CustomRecyclerAdapter(private val dataSet: ArrayList<DocumentDto>) : RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder>() {

    class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView? = view.findViewById(R.id.recycler_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_layout, parent, false) as View


        /*view.setOnClickListener {
            Log.i("CUSTOM", "Clicked!")
            view.context.startActivity(Intent(view.context, DocumentDetailsActivity::class.java))
        }*/

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val doc = dataSet[position]

        holder.textView?.text = doc.name
    }

    override fun getItemCount() = dataSet.size

    fun refreshDataSet(newDataSet: ArrayList<DocumentDto>) {
        dataSet.clear()
        dataSet.addAll(newDataSet)
    }
}