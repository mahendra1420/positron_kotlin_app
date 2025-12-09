package com.positron.teachers.model

import java.io.Serializable

class NoticeData : Serializable {
    var category: String? = null
    var to: String? = null
    var notice_id: Int? = null
    var title: String? = null
    var message: String? = null
    var notice_date: String? = null
    var staff_id: String? = null
    var content_type: String? = null
    var document: String? = null
    var link: String? = null
    var school_identifier: String? = null
    var session_identifier: String? = null
    var class_details: String? = null
    var section_details: String? = null
    var target_audience: String? = null
    var created_on: String? = null
    var attachment_url: String? = null
    var updated_at: String? = null
/*    var id: String? = null
    var session_id: String? = null
    var for_month: String? = null
    var message_date: String? = null
    var message_for: String? = null
    var content_type: String? = null
    var contents: String? = null
    var document: String? = null
    var created_on: String? = null
    var staff_id: String? = null
    var visible: String? = null
    var file_location: String? = null
    var entry_time: String? = null
    var title: String? = null*/
}