package com.positron.teachers.model

data class GetStudentsAttendance(
    val date: String,
    val class_id: String,
    val section_id: String,
    val attendance: List<Attendance>,
    var error: String? = null,
)

data class Attendance (
    var date: String? = null,
    var student_id: String? = null,
    var first_name: String? = null,
    var last_name: String? = null,
    var student_session_id: String? = null,
    var roll_no: String? = null,
    var student_name: String? = null,
    var admission_no: String? = null,
    var student_photo: String? = null,
    var attendence_type_id: String? = null,
    var attendance: String? = null,
    var isRightImage: Boolean? = true,
        )