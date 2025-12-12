package com.positron.teachers.model

data class AttendanceRequest(
    val student_session_id: List<String>,
    val attendance_type_id: List<String>,
    val teacher_id: String,
    val attendance_date: List<String>,
//    val student_id: List<String>,
//    val attendance_type_id: List<String>,
//    val attendance_date: String,
//    val class_id: String,
//    val section_id: String
)