package com.positron.teachers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.positron.teachers.R
import com.positron.teachers.model.FeePaymentData


import com.module.utils.custom_views.TextViewRobotoRegular
import com.module.utils.custom_views.TextViewRobotoRegularBold

class FeePaymentHistoryAdapter(
    private val context: Context,
    private val mList: List<FeePaymentData>,
    private var onClick: BookingDetailsAdapterInterface
) : RecyclerView.Adapter<FeePaymentHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.notice_board_list_item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = mList[position]

        holder.tvTitle.text = "Receipt No :- " + data.receipt_no
        holder.tvDescription.text = "Amount :- " + data.amount.toString()
        holder.tvDate.text = data.payment_date
        holder.imageView.setImageResource(R.drawable.linkicon)


        holder.imageView.setOnClickListener{
            onClick.onDocumentClick(data)
        }

        holder.dataLayout.setOnClickListener{
            onClick.onSelected(data)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvTitle: TextViewRobotoRegularBold = itemView.findViewById(R.id.tvTitle)
        val tvDate: TextViewRobotoRegularBold = itemView.findViewById(R.id.tvDate)
        val tvDescription: TextViewRobotoRegular = itemView.findViewById(R.id.tvDescription)
        val tvDocumentLink: TextView = itemView.findViewById(R.id.tv_document_link)
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val dataLayout: RelativeLayout = itemView.findViewById(R.id.data_layout)
    }

    interface BookingDetailsAdapterInterface {
        fun onDocumentClick(item: FeePaymentData?)
        fun onSelected(item: FeePaymentData?)
    }
}