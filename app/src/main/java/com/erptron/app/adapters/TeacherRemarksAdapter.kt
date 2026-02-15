package com.positron.teachers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.positron.teachers.R

import com.positron.teachers.model.TeacherRemarks
import de.hdodenhof.circleimageview.CircleImageView

class TeacherRemarksAdapter(
    private val context: Context,
    private val mList: List<TeacherRemarks>,
    private var onClick: BookingDetailsAdapterInterface
) : RecyclerView.Adapter<TeacherRemarksAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_teacherremarks, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = mList[position]

        holder.tvName.text = data.roll_no+". " + data.student_name

       holder.editTextRemarks.setText(data.remarks)

        holder.editTextAtt.setText(data.days_present ?: "")



        Glide.with(context)
            .load(data.student_photo)
            .apply(RequestOptions())
            .placeholder(R.drawable.dummy)
            .error(R.drawable.dummy)
            .into(holder.profileImage)


        var remarks = ""
        var daypresent = ""
        //data.remarks = remarks
        //data.days_present = daypresent
        remarks = data.remarks.toString()
        daypresent = data.days_present.toString()

        onClick.onSelected(position, remarks,daypresent)

        holder.editTextRemarks.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                var newMarks = s.toString()
                data.remarks = newMarks
                onClick.onSelected(position, newMarks,daypresent)

            }
        })
        holder.editTextAtt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                var daypresent = s.toString()
                data.days_present = daypresent
                onClick.onSelected(position, remarks,daypresent)

            }
        })

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)

        val editTextRemarks: EditText = itemView.findViewById(R.id.editTextRemarks)
        val editTextAtt: EditText = itemView.findViewById(R.id.editTextAtt)
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)

    }

    interface BookingDetailsAdapterInterface {

        fun onSelected(position: Int, remarks: String,day: String)
    }

}