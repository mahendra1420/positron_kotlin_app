package com.positron.teachers.model

data class StudentAttendanceHistoryByDay(
    var attendance_data: AttendanceDataByDay,
)

class AttendanceDataByDay (
    var admission_no: String? = null,
    var student_id: String? = null,
    var student_photo: String? = null,
    var student_name: String? = null,
    var class_name: String? = null,
    var section_name: String? = null,
    var roll_no: String? = null,
    var present_count: Int? = null,
    var absent_count: Int? = null,
    var holiday_count: Int? = null,
    var attendance_days: List<AttendanceDays>
)

class AttendanceDays (
    var date: String? = null,
    var status: String? = null
)