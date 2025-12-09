package com.positron.teachers.model

class ResponsePunchInOut<T>(
    var Error: String? = null,
    var Msg: String? = null,
    var IsAdmin: String? = null,
    val InOutPunchData : T? = null,
)