package com.positron.teachers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.positron.teachers.R
import com.positron.teachers.model.InOutPunchData
import com.positron.teachers.model.NoticeData


class AttendanceAdapter(
    private val context: Context,

    private val mList: List<InOutPunchData>,
    private var onClick: BookingDetailsAdapterInterface
) : RecyclerView.Adapter<AttendanceAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.attendance_list_item_file, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = mList[position]

        holder.tvName.text = data.Name
        holder.tvEmpCode.text = data.Empcode
       // holder.tvPunchDate.text = data.PunchDate
        holder.tvPunchDate.text = data.DateString
        holder.tvIntime.text = data.INTime
        holder.tvOuttime.text = data.OUTTime
        //holder.tvMFlag.text = data.M_Flag
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_name)
        val tvEmpCode: TextView = itemView.findViewById(R.id.tv_emp_code)
        val tvPunchDate: TextView = itemView.findViewById(R.id.tv_punch_date)
        val tvMFlag: TextView = itemView.findViewById(R.id.tv_m_flag)
        val tvOuttime: TextView = itemView.findViewById(R.id.tv_outtime)
        val tvIntime: TextView = itemView.findViewById(R.id.tv_intime)
    }

    interface BookingDetailsAdapterInterface {
        fun onDocumentClick(documentUrl : String)
        fun onSelected(item: NoticeData?)
    }

}