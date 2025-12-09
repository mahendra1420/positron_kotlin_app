package com.positron.teachers.model

class ResponsePunch<T>(
    var Error: String? = null,
    var Msg: String? = null,
    var IsAdmin: String? = null,
    val PunchData : T? = null,
)