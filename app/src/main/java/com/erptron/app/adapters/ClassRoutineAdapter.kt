package com.positron.teachers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.positron.teachers.R
import com.positron.teachers.model.ClassRoutine



class ClassRoutineAdapter(
    private val context: Context,
    private val mList: List<ClassRoutine>,

) : RecyclerView.Adapter<ClassRoutineAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.class_routine_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = mList[position]

        holder.tvTeacherName.text = data.teacher_name
        holder.tvTime.text = data.class_time
        holder.tvSubject.text = data.subject_name
        holder.tvDay.text = data.day_name


    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {

        val tvTeacherName: TextView = itemView.findViewById(R.id.tv_teacherName)
        val tvTime: TextView = itemView.findViewById(R.id.tv_time)
        val tvSubject: TextView = itemView.findViewById(R.id.tv_subject)
        val tvDay: TextView = itemView.findViewById(R.id.tv_day)

    }



}