package com.positron.teachers.model

import java.io.Serializable

class VerifyRegOtp: Serializable {
    var verified: Boolean? = null
    var otp: String? = null
}