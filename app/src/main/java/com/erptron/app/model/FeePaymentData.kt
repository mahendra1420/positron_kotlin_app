package com.positron.teachers.model

import java.io.Serializable

class FeePaymentData: Serializable {
    var receipt_no: String? = null
    var payment_date: String? = null
    var amount: Int? = null
    var receipt_url: String? = null
}