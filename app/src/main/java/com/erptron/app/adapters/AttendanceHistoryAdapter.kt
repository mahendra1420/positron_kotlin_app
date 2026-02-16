package com.positron.teachers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.positron.teachers.R
import com.positron.teachers.model.AttendanceData

class AttendanceHistoryAdapter(
    private val context: Context,
    private val attendanceData:List<AttendanceData>,
    private val attendanceHistoryAdapterInterface: AttendanceHistoryAdapterInterface
) : RecyclerView.Adapter<AttendanceHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_attendancehistory, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Glide.with(context)
            .load(attendanceData[position].student_photo)
            .apply(RequestOptions())
            .placeholder(R.drawable.dummy)
            .error(R.drawable.dummy)
            .into(holder.profile_image)

        val data = attendanceData[position]
        holder.tvName.text = "${data.roll_no ?: ""}. ${data.student_name ?: ""}"
        holder.editTextGreen.setText(data.present_count ?: "")
        holder.editTextRed.setText(data.absent_count ?: "")
        holder.editTextYellow.setText(data.holiday_count ?: "")

        holder.iv_right.setOnClickListener {
            attendanceHistoryAdapterInterface.onSelected(data)
        }


    }

    override fun getItemCount(): Int {
        return attendanceData.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val editTextGreen: EditText = itemView.findViewById(R.id.editTextGreen)
        val editTextRed: EditText = itemView.findViewById(R.id.editTextRed)
        val editTextYellow: EditText = itemView.findViewById(R.id.editTextYellow)
        val iv_right: ImageView = itemView.findViewById(R.id.iv_right)
        val profile_image: ImageView = itemView.findViewById(R.id.profile_image)
    }

    interface AttendanceHistoryAdapterInterface {
        fun onSelected(attendanceData: AttendanceData)
    }

}