package com.positron.teachers.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.RecyclerView
import com.positron.teachers.R
import com.positron.teachers.model.StudyMaterial
import com.module.utils.custom_views.TextViewRobotoRegular
import com.module.utils.custom_views.TextViewRobotoRegularBold
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class StudyAdapter(
    private val context: Context,

    private val mList: List<StudyMaterial>,
    private var onClick: BookingDetailsAdapterInterface
): RecyclerView.Adapter<StudyAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_study, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mList[position]

        holder.tvTitle.text ="Title: "+ data.title
        holder.tvName.text = data.description
        holder.tvDate.text ="Date: " + formatDate(data.created_at)

        //holder.tvName.text ="Name: " + data.name

       holder.imageView.setOnClickListener{
           onClick.onSelected(data)
        }

        holder.btnDelete.setOnClickListener{

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
        val tvName : TextViewRobotoRegularBold = itemView.findViewById(R.id.tvName)
        val tvDate: TextViewRobotoRegularBold = itemView.findViewById(R.id.tvDate)
        val tvDescription: TextViewRobotoRegular = itemView.findViewById(R.id.tvDescription)
        val imageView: ImageView = itemView.findViewById(R.id.image_view)
        val ivDownload: ImageView = itemView.findViewById(R.id.ivDownload)
        val btnDelete: AppCompatButton = itemView.findViewById(R.id.btnDelete)
        val dataLayout: RelativeLayout = itemView.findViewById(R.id.data_layout)

    }

    interface BookingDetailsAdapterInterface {
        fun onDocumentClick(item: StudyMaterial?)
        fun onSelected(item: StudyMaterial?)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC") // Handle UTC if needed
            val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            val date = inputFormat.parse(dateString)
            date?.let { outputFormat.format(it) } ?: dateString // Return formatted date or original if null
        } catch (e: Exception) {
            dateString // Return original if parsing fails
        }
    }

}