package com.positron.teachers.model

data class getStudentForRemarks(
    var status: Boolean? = false,
    var message: String? = null,
    var students : List<TeacherRemarks>? = null,
    var remarks : List<Remarks>? = null
)


data class Remarks (
    var id: Int? = null,
    var remark: String? = null
){
    override fun toString(): String {
        return remark.toString() // Display name in the spinner
    }
}
