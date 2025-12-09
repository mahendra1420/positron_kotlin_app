package com.positron.teachers.model

data class TeacherRemarksRequest(
    val class_id: Int,
    val section_id: Int,
    val format_id: Int,
    val student_ids: List<String>,
    val remarks_ids: List<String>,
    val remarks: List<String>,
)