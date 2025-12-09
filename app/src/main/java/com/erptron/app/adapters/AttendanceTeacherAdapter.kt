package com.positron.teachers.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.positron.teachers.R
import com.positron.teachers.model.Attendance
import de.hdodenhof.circleimageview.CircleImageView

class AttendanceTeacherAdapter(
    private val context: Context,
    private val mList: List<Attendance>,
    private var onClick: BookingDetailsAdapterInterface
) : RecyclerView.Adapter<AttendanceTeacherAdapter.ViewHolder>() {
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

        holder.tvName.text = data.roll_no+". " + data.first_name + data.last_name

        Glide.with(context)
            .load(data.student_photo)
            .apply(RequestOptions())
            .placeholder(R.drawable.dummy)
            .error(R.drawable.dummy)
            .into(holder.profileImage)
       // data.attendance = "pre"

          /*  val imageResourcee =  if(data.attendance == "P") {
                R.drawable.right
            } else {
                R.drawable.wrong
            }
        holder.ivRight.setImageResource(imageResourcee)*/

        val imageResource = if (data.attendance == "P") {
            R.drawable.right
        } else if(data.attendance == "A" ){
            R.drawable.wrong
        } else if (data.attendance == "H") {
            R.drawable.hlogo
        } else {
            R.drawable.round_orange_ra
        }

        /*val imageResource = if (isChecked == true && data.attendance == "P") {
            R.drawable.right
        } else {
            R.drawable.wrong
        }*/
        holder.ivRight.setImageResource(imageResource)

      /*  holder.ivRight.setOnClickListener {
            if (currentPosition != position) {
                data.isRightImage = !data.isRightImage!!

                val newMarks = if (data.isRightImage!!) "P" else "A"
                data.attendance = newMarks

                val newImageResource = if (data.isRightImage!!) {
                    R.drawable.right
                } else {
                    R.drawable.wrong
                }
                holder.ivRight.setImageResource(newImageResource)
                onClick.onSelected(position, newMarks)
                currentPosition = position

                // Notify the adapter that the item has changed
                notifyItemChanged(position)
            }
        }*/


        holder.ivRight.setOnClickListener {
           // val newMarks = "abs"
            // Toggle the isRightImage field
           // data.isRightImage = !data.isRightImage!!
          //  data.attendance = newMarks
            //data.attendance = "abs"
           // Log.e("MyLogData ==> " , " $newMarks")
           // onClick.onSelected(position,newMarks)
          /*  val imageResource = if (isChecked && data.attendance == "P") {
                R.drawable.right
            } else {
                R.drawable.wrong
            }*/
            isChecked = data.attendance == "P"

            if (data.attendance == "") {
                Log.e("fdgfd", "else")
                isChecked = true
                val newMarks = "P"
                data.attendance = newMarks
                val newImageResource = R.drawable.right // Replace with your new image resource
                holder.ivRight.setImageResource(newImageResource)
                onClick.onSelected(position, newMarks)
            } else if (isChecked) {
                    Log.e("fdgfd", "if")
                    isChecked = false
                    val newMarks = "A"
                    data.attendance = newMarks
                    val newImageResource = R.drawable.wrong // Replace with your new image resource
                    holder.ivRight.setImageResource(newImageResource)
                    onClick.onSelected(position, newMarks)
                    currentPosition = position
                } else {
                    Log.e("fdgfd", "else")
                    isChecked = true
                    val newMarks = "P"
                    data.attendance = newMarks
                    val newImageResource = R.drawable.right // Replace with your new image resource
                    holder.ivRight.setImageResource(newImageResource)
                    onClick.onSelected(position, newMarks)
                }



           notifyItemChanged(position)
           // holder.ivRight.invalidate()
        }

        /*holder.ivRight.setOnClickListener {

            if (currentPosition != position) {
                if (isPlayingAudio) {
                    Log.e("fdgfd","1")
                    isPlayingAudio = false
                    isChecked = false
                    var newMarks = "A"
                    data.attendance = newMarks
                    onClick.onSelected(position,newMarks)
                    holder.ivRight.setImageResource(R.drawable.wrong);
                    currentPosition = position
                } else {
                    Log.e("fdgfd","2")
                    isPlayingAudio = true
                    isChecked = true
                    var newMarks = "P"
                    data.attendance = newMarks
                    onClick.onSelected(position,newMarks)
                    holder.ivRight.setImageResource(R.drawable.right);
                    currentPosition = position
                }
            } else {
                if (isPlayingAudio) {
                    Log.e("fdgfd","3")
                    isPlayingAudio = true
                    isChecked = true
                    var newMarks = "A"
                    data.attendance = newMarks
                    onClick.onSelected(position,newMarks)
                    holder.ivRight.setImageResource(R.drawable.wrong);
                } else {
                    Log.e("fdgfd","4")
                    isPlayingAudio = false
                    isChecked = false
                    var newMarks = "P"
                    data.attendance = newMarks
                    onClick.onSelected(position,newMarks)
                    holder.ivRight.setImageResource(R.drawable.right);
                }
            }
            notifyItemChanged(position)
        }*/

      /*  holder.ivRight.setOnClickListener {
            if (isPlayingAudio) {
                Log.e("fdgfd","1")
                isPlayingAudio = true
                currentPosition = position;
                isChecked = true
                var newMarks = "A"
                data.attendance = newMarks
                onClick.onSelected(position,newMarks)
                holder.ivRight.setImageResource(R.drawable.wrong);
                Log.e("fdgfd","1" + currentPosition)
            } else {
                if (currentPosition != position) {
                    Log.e("fdgfd","2")
                    //isPlayingAudio = false

                    isChecked = true
                    isPlayingAudio = true
                    var newMarks = "P"
                    data.attendance = newMarks
                    onClick.onSelected(position,newMarks)
                    holder.ivRight.setImageResource(R.drawable.right);

                    currentPosition = position;
                    Log.e("fdgfd","2" + currentPosition)
                } else {
                    if (isPlayingAudio) {
                        Log.e("fdgfd","3")
                        isPlayingAudio = false
                        var newMarks = "A"
                        data.attendance = newMarks
                        onClick.onSelected(position,newMarks)
                        holder.ivRight.setImageResource(R.drawable.wrong);
                        currentPosition = position
                        Log.e("fdgfd","3" + currentPosition)

                    } else {
                        Log.e("fdgfd","4")
                        isPlayingAudio = true
                        isChecked = true
                        var newMarks = "P"
                        data.attendance = newMarks
                        onClick.onSelected(position,newMarks)
                        holder.ivRight.setImageResource(R.drawable.right);
                        currentPosition = position
                        Log.e("fdgfd","4" + currentPosition)
                    }
                }
            }
        }*/





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
        //fun onDocumentClick(documentUrl : String)
        fun onSelected(position: Int,newMarks: String)
    }

}