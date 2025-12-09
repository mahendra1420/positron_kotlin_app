package com.positron.teachers.model

data class GetExamList(
    var id: String? = null,
    var name: String? = null,
    var uni_name: String? = null,
    var sesion_id: String? = null,
    var note: String? = null,
    var is_active: String? = null,
    var entry_locked: String? = null,
    var result_published: String? = null,
    var attendance: String? = null,
    var result_publish_date: String? = null,
    var report_card_format: String? = null,
    var created_at: String? = null,
    var updated_at: String? = null,
    var is_hidden: String? = null,
    var title: String? = null,
    var visible_to_teachers: String? = null,
    var exam_type: String? = null,
    var visible_to_outsource: String? = null,

)
