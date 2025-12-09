package com.positron.teachers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.positron.teachers.R
import com.positron.teachers.model.AttendanceDays
import de.hdodenhof.circleimageview.CircleImageView

class AttendanceCorrectionAdapter(
    private val context: Context,
    private val mList: List<AttendanceDays>,
    private var onClick: BookingDetailsAdapterInterface,
) : RecyclerView.Adapter<AttendanceCorrectionAdapter.ViewHolder>() {
    var isChecked = true
    private var currentPosition = 0
    private var currentPlayingButton: ImageView? = null
    var isPlayingAudio = false


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_attendance, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val data = mList[position]

        holder.tvName.text = data.date

        val imageResource = if (data.status == "P") {
            R.drawable.right
        } else if (data.status == "A") {
            R.drawable.wrong
        } else if (data.status == "H") {
            R.drawable.hlogo
        } else {
            R.drawable.round_orange_ra
        }

        holder.ivRight.setImageResource(imageResource)
        holder.ivRight.setOnClickListener {
            isChecked = data.status == "P"
            if (data.status != "") {
                if (data.status != "H") {
                    if (isChecked) {
                        Log.e("fdgfd", "if")
                        isChecked = false
                        val newMarks = "A"
                        data.status = newMarks
                        val newImageResource =
                            R.drawable.wrong // Replace with your new image resource
                        holder.ivRight.setImageResource(newImageResource)
                        onClick.onSelected(position, newMarks)
                        currentPosition = position
                    } else {
                        Log.e("fdgfd", "else")
                        isChecked = true
                        val newMarks = "P"
                        data.status = newMarks
                        val newImageResource =
                            R.drawable.right // Replace with your new image resource
                        holder.ivRight.setImageResource(newImageResource)
                        onClick.onSelected(position, newMarks)
                    }
                } else {
                    Toast.makeText(context, "This day is holiday...", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (isChecked) {
                    Log.e("fdgfd", "if")
                    isChecked = false
                    val newMarks = "A"
                    data.status = newMarks
                    val newImageResource =
                        R.drawable.wrong // Replace with your new image resource
                    holder.ivRight.setImageResource(newImageResource)
                    onClick.onSelected(position, newMarks)
                    currentPosition = position
                } else {
                    Log.e("fdgfd", "else")
                    isChecked = true
                    val newMarks = "P"
                    data.status = newMarks
                    val newImageResource =
                        R.drawable.right // Replace with your new image resource
                    holder.ivRight.setImageResource(newImageResource)
                    onClick.onSelected(position, newMarks)
                }
            }
            notifyItemChanged(position)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvName)
        val profileImage: CircleImageView = itemView.findViewById(R.id.profile_image)
        val ivRight: ImageView = itemView.findViewById(R.id.iv_right)


    }

    interface BookingDetailsAdapterInterface {
        fun onSelected(position: Int, newMarks: String)
    }

}