package com.positron.teachers.model

data class AttendanceCoRequest(
    val class_id: Int,
    val section_id: Int,
    val exam_id: Int,
    val subject_id: Int,
    val student_id: List<String>,
    val get_marks: List<String>,
    val attendance: List<String>,
)