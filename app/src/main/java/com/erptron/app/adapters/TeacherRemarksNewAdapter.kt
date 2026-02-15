package com.positron.teachers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.positron.teachers.R
import com.positron.teachers.model.GetStudentsPhotoList
import com.positron.teachers.model.GradeNew
import com.positron.teachers.model.Remarks

import com.positron.teachers.model.TeacherRemarks
import de.hdodenhof.circleimageview.CircleImageView

class TeacherRemarksNewAdapter(
    private val context: Context,
    private val mList: List<TeacherRemarks>,
    private var onClick: BookingDetailsAdapterInterface,
    private var remarksList: MutableList<Remarks>
) : RecyclerView.Adapter<TeacherRemarksNewAdapter.ViewHolder>() {

    init {
        // Add a default "Select Remark" option at the top of the list
        remarksList.add(0, Remarks(id = 0, remark = "Select Remark"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_teacherremarks, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = mList[position]

        holder.tvName.text = data.roll_no + ". " + (data.student_name ?: data.first_name ?: "")

        holder.editTextRemarks.isEnabled = !data.remarks.isNullOrEmpty()
        holder.editTextRemarks.setText(data.remarks)

       // holder.editTextAtt.setText(data.days_present)



        /*Glide.with(context)
            .load(data.student_photo)
            .apply(RequestOptions())
            .placeholder(R.drawable.dummy)
            .error(R.drawable.dummy)
            .into(holder.profileImage)*/



        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, remarksList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinner.adapter = adapter

        // Set spinner to the current selection
        val selectedGrade = remarksList.find { it.id == data.roll_no?.toInt() }
        val spinnerPosition = adapter.getPosition(selectedGrade)
        if (spinnerPosition >= 0) {
            holder.spinner.setSelection(spinnerPosition)
        }

        /*holder.spinner.setOnTouchListener { _, _ ->
            if (data.exam_array.attendence == "A") {
                Toast.makeText(context, "Student is absent; you can't give marks or grade.", Toast.LENGTH_SHORT).show()
                return@setOnTouchListener true // Prevent spinner from opening
            }
            false
        }*/

        holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedGrade = remarksList[position]
              //  data.exam_array.get_marks = selectedGrade.id.toString()
                data.remarks_id = selectedGrade.id?.toString()
               // onClick.onMarksUpdated(holder.adapterPosition, selectedGrade.id.toString())
                holder.editTextRemarks.text.clear()
                onClick.onSelectednew(holder.adapterPosition,data.remarks.toString(),selectedGrade.id.toString())
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        var remarks = ""
        var daypresent = ""
        //data.remarks = remarks
        //data.days_present = daypresent
        if (data.remarks==null)
        {
            remarks = data.remarks.toString()
        }
        remarks = data.remarks.toString()
        daypresent = data.remarks_id.toString()

        onClick.onSelectednew(position, remarks,daypresent)

        holder.editTextRemarks.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                var newMarks = s.toString()
                data.remarks = newMarks
                var daypresent = ""
                daypresent = data.remarks_id.toString()
                onClick.onSelectednew(position, newMarks,daypresent)

            }
        })
        /* holder.editTextAtt.addTextChangedListener(object : TextWatcher {
             override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
             override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
             override fun afterTextChanged(s: Editable?) {
                 var daypresent = s.toString()
                 data.days_present = daypresent
                 onClick.onSelected(position, remarks,daypresent)

             }
         })*/

        // Handle radio button state
        //holder.radioButton.isChecked = false
       // holder.editTextRemarks.isEnabled = data

       /* holder.radioButton.setOnClickListener {
            if (holder.radioButton.isChecked ) {
               // data.exam_array.attendence = "P"
                holder.radioButton.isChecked = false
                holder.editTextRemarks.isEnabled = true
              //  onClick.onAttUpdated(position, "P")
            } else {
               // data.exam_array.attendence = "A"
                holder.radioButton.isChecked = true
                holder.editTextRemarks.isEnabled = false
                holder.editTextRemarks.setText("") // Clear marks when absent
               // data.exam_array.get_marks = ""
               // onClick.onAttUpdated(position, "A")
            }
        }*/


        holder.radioButton.isChecked = data.isChecked
        holder.editTextRemarks.isEnabled = !data.isChecked
        holder.radioButton.setOnCheckedChangeListener { _, isChecked ->
            data.isChecked = isChecked
            holder.editTextRemarks.isEnabled = !isChecked  // Disable EditText when checked
            daypresent = data.remarks_id.toString()
            onClick.onSelectednew(position, remarks,daypresent)
        }



    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)

        val editTextRemarks: EditText = itemView.findViewById(R.id.editTextRemarks)
        val editTextAtt: EditText = itemView.findViewById(R.id.editTextAtt)
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        val radioButton: CheckBox = itemView.findViewById(R.id.radio_button)
        val spinner: Spinner = itemView.findViewById(R.id.spinner)
        val llSpinner: LinearLayout = itemView.findViewById(R.id.llSpinner)
    }

    interface BookingDetailsAdapterInterface {

       // fun onSelected(position: Int, remarks: String,day: String)
        fun onSelectednew(position: Int, remarks: String,id: String)
    }

}