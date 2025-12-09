package com.positron.teachers.model

data class TeacherRemarks(
    var id: Int? =  null,
    var student_id: String? = null,
    var student_session_id: String? = null,
    var roll_no: String? = null,
    var student_name: String? = null,
    var first_name: String? = null,
    var last_name: String? = null,
    var admission_no: String? = null,
    var student_photo: String? = null,
    var working_days: String? = null,
    var days_present: String? = null,
    var remarks_id: String? = null,
    var remarks: String? = null,
    var isChecked: Boolean = false
)
