package com.positron.teachers.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.positron.teachers.R
import com.positron.teachers.model.GetStudentsPhotoList
import de.hdodenhof.circleimageview.CircleImageView

class StudentPhotoAdapter(
    private val context: Context,
    private val mList: List<GetStudentsPhotoList>,
    private var onClick: StudentDetailsAdapterInterface
) : RecyclerView.Adapter<StudentPhotoAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_studentphoto, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mList[position]
        holder.tvName.text = data.roll_no + ". " + data.first_name + " " + data.last_name
        Glide.with(context)
            .load(data.student_photo)
            .apply(RequestOptions().signature(ObjectKey(System.currentTimeMillis())))
            .placeholder(R.drawable.dummy)
            .error(R.drawable.dummy)
            .into(holder.profileImage)
        holder.btnTake.setOnClickListener {
            onClick.onSelected(position, data)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }


    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        val btnTake: Button = itemView.findViewById(R.id.btnTake)

    }

    interface StudentDetailsAdapterInterface {
        fun onSelected(position: Int,item:GetStudentsPhotoList? )
    }


}