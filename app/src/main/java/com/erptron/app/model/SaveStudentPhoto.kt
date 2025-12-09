package com.positron.teachers.model

data class SaveStudentPhoto(
    var response: Boolean? = false,
    var message: String? = null,
    var student_id: String? = null,
    var new_url: String? = null,


)
