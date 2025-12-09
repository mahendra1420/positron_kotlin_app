package com.positron.teachers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.positron.teachers.R


class DestinationCountryAdapter(
    private val context: Context,
    private val mList: List<String>,
    private var onClick: FilterSelectionInterface
) : RecyclerView.Adapter<DestinationCountryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_dialog_design, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mList[position]
        holder.textView.text = data

        holder.textView.setOnClickListener {
            onClick.onSelected(data)
        }

    }


    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val textView: TextView = itemView.findViewById(R.id.textViewList)
    }

    interface FilterSelectionInterface {
        fun onSelected(item: String?)
    }
}