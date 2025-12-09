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
import com.positron.teachers.model.NoticeData
import com.module.utils.custom_views.TextViewRobotoRegular
import com.module.utils.custom_views.TextViewRobotoRegularBold
import java.text.SimpleDateFormat
import java.util.Locale

class NoticeBoardAdapter(
    private val context: Context,
    private val mList: List<NoticeData>,
    private var onClick: BookingDetailsAdapterInterface
) : RecyclerView.Adapter<NoticeBoardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.notice_board_list_item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = mList[position]

        holder.tvTitle.text = data.title
        holder.tvDescription.text = data.message
        holder.tvDate.text = "Date:- " + formatTimestamp(data.created_on.toString())

            if (data.content_type.equals("document")){
                holder.tvDescription.text = data.message
                holder.tvTitle.text = data.title
               holder.imageView.setImageResource(R.drawable.pdficon)
            } else if (data.content_type.equals("link")) {
                holder.tvDescription.text = data.message
                holder.tvTitle.text = data.title
                holder.imageView.setImageResource(R.drawable.linkicon)
            }else if(data.content_type.equals("text")){
                holder.tvDescription.text =data.message
                holder.tvTitle.text = data.title
                holder.imageView.setImageResource(R.drawable.testicon)
            }

        holder.imageView.setOnClickListener{
            onClick.onDocumentClick(data)
        }

        holder.dataLayout.setOnClickListener{
            onClick.onSelected(data)
        }
    }

    fun formatTimestamp(timestamp: String): String {
        // Define the input and output date formats
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        return try {
            // Parse the input timestamp and format it to the desired format
            val date = inputFormat.parse(timestamp)
            outputFormat.format(date)
        } catch (e: Exception) {
            // Handle parsing errors
            "Invalid date format"
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
        fun onDocumentClick(item: NoticeData?)
        fun onSelected(item: NoticeData?)
    }

}