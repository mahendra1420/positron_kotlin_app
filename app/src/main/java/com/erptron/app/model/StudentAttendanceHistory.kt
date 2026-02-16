package com.positron.teachers.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class StudentAttendanceHistory(
    var attendance_data: List<AttendanceData>,
)

class AttendanceData : Serializable {
    var admission_no: String? = null
    var student_id: String? = null
    var student_session_id: String? = null
    var student_photo: String? = null
    var student_name: String? = null
    var class_name: String? = null
    var section_name: String? = null
    var roll_no: String? = null
    @SerializedName("total_present") var present_count: String? = null
    @SerializedName("total_absent") var absent_count: String? = null
    @SerializedName("total_holiday") var holiday_count: String? = null
    var remaining_count: String? = null
}