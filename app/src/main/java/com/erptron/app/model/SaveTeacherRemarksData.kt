package com.positron.teachers.model

import java.io.Serializable

class SaveTeacherRemarksData: Serializable {
    var teacher_id: String? = null
    var exam_id: String? = null
    var class_id: String? = null
    var section_id: String? = null
    var student_id: String? = null
    var student_session_id: String? = null
    var working_days: String? = null
    var days_present: String? = null
    var remarks: String? = null

}