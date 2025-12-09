package com.positron.teachers.model

data class ReMarkRequest(
    val teacher_id: String,
    val exam_id: String,
    val class_id: String,
    val section_id: String,
    val student_session_id: List<String>,
    val working_days: List<String>,
    val days_present: List<String>,
    val student_id: List<String>,
    val remarks: List<String>,
)