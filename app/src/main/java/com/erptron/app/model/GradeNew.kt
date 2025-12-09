package com.positron.teachers.model

data class GradeNew(val id: Int, val grade_name: String) {
    override fun toString(): String {
        return grade_name // Display name in the spinner
    }
}