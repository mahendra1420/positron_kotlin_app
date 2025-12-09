package com.positron.teachers.model

data class getFormat(
    var status: Boolean? = false,
    var message: String? = null,
    var format : List<FormatData>? = null
)
