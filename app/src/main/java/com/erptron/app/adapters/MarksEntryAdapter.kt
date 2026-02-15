package com.positron.teachers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.positron.teachers.R
import com.positron.teachers.model.GradeNew
import com.positron.teachers.model.WithMark
import de.hdodenhof.circleimageview.CircleImageView

class MarksEntryAdapter(
    private val context: Context,
    private val mList: List<WithMark>,
    private var onClick: BookingDetailsAdapterInterface,
    private var gradeList: MutableList<GradeNew>
) : RecyclerView.Adapter<MarksEntryAdapter.ViewHolder>() {

    var isChecked = false
    private var selectedPosition = -1

    init {
        // Add a default "Select Grade" option at the top of the list
        gradeList.add(0, GradeNew(id = 0, grade_name = "Select"))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_marksentry, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = mList[position]

        // Set up name and full marks
        holder.tvName.text = "${data.roll_no}. ${data.first_name}"
        holder.etMarks.setText(data.examData.get_marks)

        // Load profile image
        Glide.with(context)
            .load(data.student_photo)
            .apply(RequestOptions())
            .placeholder(R.drawable.dummy)
            .error(R.drawable.dummy)
            .into(holder.profileImage)

        // Update UI based on dropdown value
        if (data.examData.dropdown == 1) {
            holder.llSpinner.visibility = View.VISIBLE
            holder.etMarks.visibility = View.GONE
            holder.spinner.visibility = View.VISIBLE
            holder.tvGetMarks.visibility = View.GONE

            val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, gradeList)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            holder.spinner.adapter = adapter

            // Set spinner to the current selection
            val selectedGrade = gradeList.find { it.id == data.examData.input_in_grade }
            val spinnerPosition = adapter.getPosition(selectedGrade)
            if (spinnerPosition >= 0) {
                holder.spinner.setSelection(spinnerPosition)
            }

            holder.spinner.setOnTouchListener { _, _ ->
                if (data.examData.attendence == "A") {
                    Toast.makeText(context, "Student is absent; you can't give marks or grade.", Toast.LENGTH_SHORT).show()
                    return@setOnTouchListener true // Prevent spinner from opening
                }
                false
            }

            holder.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    val selectedGrade = gradeList[position]
                    data.examData.get_marks = selectedGrade.id.toString()
                    onClick.onMarksUpdated(holder.adapterPosition, selectedGrade.id.toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        } else {
            holder.tvGetMarks.visibility = View.GONE
            holder.spinner.visibility = View.GONE
            holder.etMarks.visibility = View.VISIBLE
            holder.llSpinner.visibility = View.GONE
        }

        // Set EditText interaction
        holder.etMarks.setOnTouchListener { _, _ ->
            if (data.examData.attendence == "A") {
                Toast.makeText(context, "Student is absent; you can't give marks.", Toast.LENGTH_SHORT).show()
                return@setOnTouchListener true // Prevent EditText from being edited
            }
            false
        }

        holder.etMarks.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.toString()?.toIntOrNull()?.let {
                    if (data.examData.full_marks != null && it > data.examData.full_marks!!.toInt()) {
                        Toast.makeText(context, "Marks cannot exceed ${data.examData.full_marks}", Toast.LENGTH_SHORT).show()
                        holder.etMarks.setText("")
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                val newMarks = s.toString()
                data.examData.get_marks = newMarks
                onClick.onMarksUpdated(holder.adapterPosition, newMarks)
            }
        })

        // Handle radio button state
        holder.radioButton.isChecked = data.examData.attendence == "A"
        holder.etMarks.isEnabled = data.examData.attendence != "A"

        holder.radioButton.setOnClickListener {
            if (data.examData.attendence == "A") {
                data.examData.attendence = "P"
                holder.radioButton.isChecked = false
                holder.etMarks.isEnabled = true
                onClick.onAttUpdated(position, "P")
            } else {
                data.examData.attendence = "A"
                holder.radioButton.isChecked = true
                holder.etMarks.isEnabled = false
                holder.etMarks.setText("") // Clear marks when absent
                data.examData.get_marks = ""
                onClick.onAttUpdated(position, "A")
            }
        }
    }




    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val tvFullMarks: TextView = itemView.findViewById(R.id.tvFullMarks)
        val tvGetMarks: TextView = itemView.findViewById(R.id.tvGetMarks)
        val etMarks: EditText = itemView.findViewById(R.id.editTextMarks)
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        val radioButton: RadioButton = itemView.findViewById(R.id.radio_button)
        val spinner: Spinner = itemView.findViewById(R.id.spinner)
        val llSpinner: LinearLayout = itemView.findViewById(R.id.llSpinner)
    }

    interface BookingDetailsAdapterInterface {
        fun onMarksUpdated(position: Int, marks: String)
        fun onAttUpdated(position: Int, att: String)
    }

    data class Grade(val id: String, val name: String) {
        override fun toString(): String {
            return name
        }
    }
}
