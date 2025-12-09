package com.positron.teachers.model

import java.io.Serializable

data class StudentAttendanceHistory(
    var attendance_data: List<AttendanceData>,
)

class AttendanceData: Serializable {
    var admission_no: String? = null
    var student_id: String? = null
    var student_photo: String? = null
    var student_name: String? = null
    var class_name: String? = null
    var section_name: String? = null
    var roll_no: String? = null
    var present_count: Int? = null
    var absent_count: Int? = null
    var holiday_count: Int? = null
    var remaining_count: Int? = null
}