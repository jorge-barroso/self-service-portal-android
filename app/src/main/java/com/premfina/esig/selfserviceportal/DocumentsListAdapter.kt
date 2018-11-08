package com.premfina.esig.selfserviceportal.com.premfina.esig.selfserviceportal

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.premfina.esig.selfserviceportal.R
import com.premfina.esig.selfserviceportal.com.premfina.esig.selfserviceportal.DocumentsListAdapter.ViewHolder
import com.premfina.selfservice.dto.DocumentDto
import kotlinx.android.synthetic.main.documents_list.view.*

class DocumentsListAdapter(private val documentDtos: Array<DocumentDto>, private val ctx: Context) : RecyclerView.Adapter<ViewHolder>() {

    private val itemStateArray = SparseBooleanArray(documentDtos.size)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(ctx).inflate(R.layout.documents_list, parent, false))
    }

    override fun getItemCount(): Int {
        return documentDtos.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.checkBox.isChecked = itemStateArray.get(position, false)

        holder.checkBox.text = documentDtos[position].name

        holder.checkBox.setOnClickListener {
            Log.i("Clicked!", position.toString())
            if (!itemStateArray[position, false])
                itemStateArray.put(position, true)
            else
                itemStateArray.put(position, false)

            Log.i("States", itemStateArray.toString())
        }
    }

    fun getSelectedDocuments(): ArrayList<Int> {
        val trueValues = ArrayList<Int>(itemStateArray.size())

        for (i in 0 until itemStateArray.size()) {
            val key = itemStateArray.keyAt(i)
            if (itemStateArray.get(key, false))
                trueValues.add(key)
        }

        trueValues.trimToSize()

        return trueValues
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox = view.documents_checkbox!!
    }
}