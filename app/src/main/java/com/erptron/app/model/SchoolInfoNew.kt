package com.positron.teachers.model

data class SchoolInfoNew<T>(
    val school_info : T? = null,
    val status :Boolean,
    val message:String? = null,
    var error_message: String? = null,
)
