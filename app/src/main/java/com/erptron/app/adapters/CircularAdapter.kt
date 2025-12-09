package com.positron.teachers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.positron.teachers.R

import com.positron.teachers.model.CircularUpdates


class CircularAdapter(
    private val context: Context,
    private val mList: List<CircularUpdates>,
   // private var onClick: BookingDetailsAdapterInterface
) : RecyclerView.Adapter<CircularAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.circular_list_item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = mList[position]

        holder.tvDate.text = data.notice_date
        holder.tvTitle.text = data.title
         holder.tvDescription.text = data.contents


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)

    }

    interface BookingDetailsAdapterInterface {
        fun onDocumentClick(documentUrl : String)
        fun onSelected(item: CircularUpdates?)
    }

}