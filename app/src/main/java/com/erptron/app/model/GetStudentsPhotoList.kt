package com.positron.teachers.model

import java.io.Serializable

 class GetStudentsPhotoList : Serializable {
    var id: Int?= null
    var session_id: String? = null
    var class_id: String? = null
    var section_id: String? = null
    var class_name: String? = null
    var section_name: String? = null
    var student_id: String? = null
    var admission_no: String? = null
    var roll_no: String? = null
    var student_name: String? = null
    var first_name: String? = null
    var last_name: String? = null
    var father_name: String? = null
    var mother_name: String? = null
    var student_photo: String? = null
    var remarks_id: String? = null
    var remarks: String? = null
    var isRightImage: Boolean? = true
}

