package com.positron.teachers.model

import com.google.gson.annotations.SerializedName

data class ExamScheduleData(
/*    var student_id: String? = null,
    var v_payment_id: String? = null,
    var admission_no: String? = null,
    var roll_no: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var dob: String? = null,
    var father_name: String? = null,
    var stream_subject_id: String? = null,
    var second_language: String? = null,
    var student_photo: String? = null,
    var isRightImage: Boolean? = true,
    var exam_array:List<ExamArrayData>

)*/

val status : Boolean,
val message : String,
val subject_category: SubjectCategory,
val with_marks: List<WithMark>,
val marksgrade: List<GradeNew>,
val students: List<Any?>,
)

data class SubjectCategory(
    val category_name: String,
    val id: Long,
)

data class WithMark(
    var student_id: String? = null,
    var v_payment_id: String? = null,
    var admission_no: String? = null,
    var roll_no: String? = null,
    @SerializedName("firstname") var first_name: String? = null,
    var lastname: String? = null,
    var dob: String? = null,
    var father_name: String? = null,
    var stream_subject_id: String? = null,
    var second_language: String? = null,
    var student_photo: String? = null,
    var isRightImage: Boolean? = true,
    var exam_array: List<ExamArrayData> = emptyList()
) {
    val examData: ExamArrayData
        get() = exam_array.firstOrNull() ?: ExamArrayData()
}
