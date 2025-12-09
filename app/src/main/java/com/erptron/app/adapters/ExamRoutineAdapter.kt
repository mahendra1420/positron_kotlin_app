package com.positron.teachers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.positron.teachers.R
import com.positron.teachers.model.ExamRoutine



class ExamRoutineAdapter(
    private val context: Context,
    private val mList: List<ExamRoutine>,

) : RecyclerView.Adapter<ExamRoutineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.exam_routine_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = mList[position]

         holder.tvExamDate.text = data.exam_date
         holder.tvSubject.text = data.subject_name
          holder.tvFullMarks.text = data.full_marks
          holder.tvPassMarks.text = data.pass_marks


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val tvExamDate: TextView = itemView.findViewById(R.id.tv_examDate)
        val tvSubject: TextView = itemView.findViewById(R.id.tv_subject)
        val tvFullMarks: TextView = itemView.findViewById(R.id.tv_fullMarks)
        val tvPassMarks: TextView = itemView.findViewById(R.id.tv_passMarks)

    }


}