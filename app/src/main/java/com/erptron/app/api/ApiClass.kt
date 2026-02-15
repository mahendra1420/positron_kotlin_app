package com.positron.teachers.api

import Attendance
import com.positron.teachers.model.*
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface ApiClass {

    @GET("{school_code}/H3M96F4BVQJZGWP7K8A5SR")
    fun getDataFromSchoolInfo(
        @Path("school_code") schoolCode: String
    ): Call<SchoolInfoNew<SchoolInfo>>



    @GET("app_version")
    fun getAppVersion(): Call<AppVersionData>

    /*------------------------------- Student APIS -------------------------------------------*/

    @POST("student_info/{admission_number}")
    fun getDataFromAdmissionNumber(
        @Path("admission_number") admission_number: String,
    ): Call<ResultListNew<StudentsInfo>>

    @FormUrlEncoded
    @POST("reg_student")
    fun registerApi(
        @Field("admission_no") admission_no: String,
        @Field("student_name") student_name: String,
        @Field("app_code") app_code: String,
        @Field("token") token: String,
    ): Call<RegisterData>

    @GET("notice_board_mobile_app")
    fun getNoticeBoardData(): Call<NoticeBoardWeb<List<NoticeData>>>

    @GET("notice_board_mobile_app/{year}/{month}")
    fun getNoticeBoardDataFromDate(
        @Path("month") month: String,
        @Path("year") year: String,
    ): Call<NoticeBoardWeb<List<NoticeData>>>

    @GET("punch_report/{date}/{admission_no}/{id}")
    fun getPunchReport(
        @Path("date") date: String,
        @Path("admission_no") admission_no: String,
        @Path("id") id: String,
    ): Call<ResponsePunch<List<PunchData>>>

    @GET("verify_reg_otp/{admission_no}/{token}")
    fun verifyRegotp(
        @Path("admission_no") admission_no: String,
        @Path("token") token: String,
    ): Call<VerifyRegOtp>

    @GET("verify_otp/{admission_no}/{OTP}/{token}")
    fun verifyotp(
        @Path("admission_no") admission_no: String,
        @Path("OTP") otp: String,
        @Path("token") token: String,
    ): Call<VerifyRegOtp>

    @GET("personal_notice/{admission_no}")
    fun getPersonalNotice(
        @Path("admission_no") admission_no: String,
    ): Call<PersonalNotice<List<PNoticeData>>>

    @GET("punch_report_in_out/{date}/{datee}/{admission_no}/{id}")
    fun getPunchReportInOut(
        @Path("date") date: String,
        @Path("datee") datee: String,
        @Path("admission_no") admission_no: String,
        @Path("id") id: String,
    ): Call<ResponsePunchInOut<List<InOutPunchData>>>


    @GET("study_material/{year}/{month}/{admission_no}")
    fun getStudyMaterial(
        @Path("year") year: String,
        @Path("month") month: String,
        @Path("admission_no") admission_no: String,
    ): Call<List<StudyMaterial>>

    @GET("fee_payment_history/{admission_no}")
    fun getFeePaymentHistory(
        @Path("admission_no") admission_no: String,
    ): Call<FeePayment<List<FeePaymentData>>>


    /*-------------------------------TEACHER APIS-------------------------------------------*/

    @POST("teachers_login/{email}/{password}")
    fun getTeacherLogin(
        @Path("email") email: String,
        @Path("password") password: String,
//        @Query("school_code") school_code: String,
//        @Query("school_id") school_id: String,
//        @Query("fcm_token") fcm_token: String,
//        @Query("app_version") app_version: String,
//        @Query("device_details") device_details: String,
    ): Call<TeachersLogin>


    @FormUrlEncoded
    @POST("changePassword")/*teachers_login/{email_id}/{password}*/
    fun passwordChange(
        @Header("Authorization") Authorization: String,
        @Field("current_password") current_password: String,
        @Field("new_password") new_password: String,
        @Field("confirm_password") confirm_password: String,
    ): Call<TeachersLogin>

    @FormUrlEncoded
    @POST("resetPassword")/*teachers_login/{email_id}/{password}*/
    fun resetPassword(
        @Field("school_id") school_id: String,
        @Field("email") email: String,
    ): Call<ResetPasswordModel>


    @GET("getAttendanceClass")
    fun getTeacherClass(
        @Header("Authorization") Authorization: String,
    ): Call<List<Teacherclass>>

    //getClassTeacherClass
    @GET("getClassTeacherclass/{Teacher_ID}")
    fun getClassTeacherClass(
        @Path("Teacher_ID") teacher_id: String,
    ): Call<List<getClassTeacherClass>>

    //getClassTeacherSection
    //@FormUrlEncoded
    @GET("getClassTeacherSection/{Teacher_ID}/{class_id}")
    fun getClassTeacherSection(
//        @Header("Authorization") Authorization: String,
        @Path("Teacher_ID") teacher_id: String,
        @Path("class_id") class_id: String,
    ): Call<List<getClassTeacherSection>>

    //getSubjectTeacherClass
    @GET("getSubjectTeacherClass/{Teacher_ID}")
    fun getSubjectTeacherClass(
        @Path("Teacher_ID") teacher_id: String,
    ): Call<List<getSubjectTeacherclass>>

    //getSubjectTeacherSection
    //@FormUrlEncoded
    @GET("getSubjectTeacherSection/{Teacher_ID}/{class_id}")
    fun getSubjectTeacherSection(
        @Path("class_id") class_id: String,
        @Path("Teacher_ID") teacher_id: String,

        ): Call<List<getSubjectTeacherSection>>

    @GET("teacher_notice")
    fun getNoticeData(
        @Header("Authorization") Authorization: String,
    ): Call<NoticeBoardWeb<List<NoticeData>>>

    @GET("circular")
    fun getCircularData(): Call<Circular<List<CircularUpdates>>>


    @GET("getStudentsAttendance/{Class_ID}/{Section_ID}/{Date}")
    fun getStudentsAttendance(
        @Header("Authorization") Authorization: String,
        @Path("Class_ID") class_id: String,
        @Path("Section_ID") section_id: String,
        @Path("Date") date: String,
    ): Call<List<Attendance>>


    @GET("getExamList/{Teacher_ID}/{class_id}/{Section_ID}")
    fun getExamList(
        @Path("class_id") class_id: String,
        @Path("Teacher_ID") Teacher_ID: String,
        @Path("Section_ID") section_id: String,
    ): Call<List<GetExamList>>

    @GET("getExamList")
    fun getExamListSimple(): Call<List<GetExamList>>


    @POST("save_attendance")
    fun SaveAttendanceAPI(
        @Header("Authorization") token: String,
        @Body request: AttendanceRequest
    ): Call<SaveAttendance>



    @POST("saveStudentMonthAttendanceHistory")
    fun saveStudentMonthAttendanceHistory(
        @Header("Authorization") Authorization: String,
        @Body requestBody: JsonObject,
    ): Call<SaveAttendance>


    @POST("savemarks")
    fun SaveMarksEntry(
        @Header("Authorization") Authorization: String,
        @Body attendanceRequest: AttendanceCoRequest,
    ): Call<SaveAttendance>

    @FormUrlEncoded
    @POST("save_marks_entry")
    fun saveMarksEntryForm(
        @Header("Authorization") Authorization: String,
        @Field("save_exam") save_exam: String,
        @Field("exam_id") exam_id: String,
        @Field("student_id") student_id: String,
        @Field("exam_schedule_id") exam_schedule_id: String,
        @Field("class_id") class_id: String,
        @Field("section_id") section_id: String,
        @Field("subject_id") subject_id: String,
        @Field("teacher_id") teacher_id: String,
        @Field("get_marks") get_marks: String,
        @Field("attendence") attendence: String,
    ): Call<SaveAttendance>

    @GET("getStudentsExam/{Class_ID}/{Section_ID}/{Subject_ID}/{Exam_ID}/{Subject_Category_ID}/{Term_ID}")
    fun getStudentsExam(
        @Path("Class_ID") class_id: String,
        @Path("Section_ID") section_id: String,
        @Path("Subject_ID") subject_id: String,
        @Path("Exam_ID") exam_id: String,
        @Path("Subject_Category_ID") subject_category_id: String,
        @Path("Term_ID") term_id: String,
    ): Call<List<WithMark>>

//    @POST("getSubjectsList")//{Teacher_ID}/{Exam_ID}/{Class_ID}/{Section_ID}
//    fun getSubjects(
//        @Header("Authorization") Authorization: String,
//        /* @Path("Teacher_ID") teacher_id: String,
//         @Path("Exam_ID") exam_id: String,*/
//        @Field("class_id") class_id: String,
//        /*@Path("Section_ID") section_id: String,*/
//    ): Call<List<GetSubjects>>
    @GET("getSubjects/{Teacher_ID}/{Exam_ID}/{Class_ID}/{Section_ID}")
    fun getSubjects(
        @Path("Teacher_ID") Teacher_ID: String,
        @Path("Exam_ID") Exam_ID: String,
        @Path("Class_ID") classId: String,
        @Path("Section_ID") sectionId: String
    ): Call<List<GetSubjects>>



    @GET("getTeacherRemarks")//teacher_remarks/{Teacher_ID}/{Exam_ID}/{Class_ID}/{Section_ID}
    fun getTeacherRemarks(
        @Header("Authorization") Authorization: String,
    ): Call<List<TeacherRemarks>>

    @FormUrlEncoded
    @POST("save_teacher_remarks")
    fun saveTeacherRemarks(
        @Header("Authorization") Authorization: String,
        @Body attendanceRequest: ReMarkRequest,
    ): Call<SaveTeacherRemarks<List<SaveTeacherRemarksData>>>


    @FormUrlEncoded
    @POST("save_study_materials")
    fun SaveStudyMaterials(
        @Field("class_id") class_id: String,
        @Field("section_id") section_id: String,
        @Field("subject_id") subject_id: String,
        @Field("teacher_id") teacher_id: String,
        @Field("document") document: File,
        @Field("title") title: String,
    ): Call<SaveStudyMaterials>

    @FormUrlEncoded
    @POST("getStudyMaterials")
    fun getTeacherStudyMaterial(
        @Header("Authorization") Authorization: String,
        @Field("class_id") class_id: String,
        @Field("section_id") section_id: String,
        @Field("subject_id") subject_id: String,
    ): Call<StudyMaterialMain>


    @Multipart
    @POST("save_study_materials")
    fun addComplain(
        @Part("class_id") class_id: String,
        @Part("section_id") section_id: String,
        @Part("subject_id") subject_id: String,
        @Part("teacher_id") teacher_id: String,
        @Part("title") title: String,
        @Part document: MultipartBody.Part? = null,
    ): Call<SaveStudyMaterials>

    //UploadStudyMaterial
    @Multipart
    @POST("saveStudyMaterial")
    fun uploadStudyMaterial(
        @Header("Authorization") Authorization: String,
        @Part("class_id") class_id: Int,
        @Part("section_id") section_id: Int,
        @Part("subject_id") subject_id: Int,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody,
        @Part attachment_file: MultipartBody.Part? = null,
    ): Call<SaveStudyMaterials>

    @FormUrlEncoded
    @POST("getSubjects") //General/{Teacher_ID}/{Class_ID}/{Section_ID}
    fun getSubjectsGeneral(
        @Header("Authorization") Authorization: String,
        @Field("class_id") class_id: String,
        @Field("section_id") section_id: String,
    ): Call<List<GetSubjectsNew>>

    @GET("class_routine/{Class_ID}/{Section_ID}")
    fun getClassRoutine(
        @Path("Class_ID") class_id: String,
        @Path("Section_ID") section_id: String,
    ): Call<ClassRoutineObject<List<ClassRoutine>>>

    @GET("getClassRoutine")
    fun getClassRoutine1(
        @Header("Authorization") token: String
    ): Call<ClassRoutinePDF>

    @GET("exam_routine/{Teacher_ID}/{Class_ID}/{Section_ID}")
    fun getExamRoutine(
        @Path("Teacher_ID") teacher_id: String,
        @Path("Class_ID") class_id: String,
        @Path("Section_ID") section_id: String,
    ): Call<ExamRoutineObject<List<ExamRoutine>>>

    @GET("ddl_grades")
    fun getGrades(): Call<Grade>

    @FormUrlEncoded
    @POST("getStudentPhoto") //getStudentListPhotoSession/{Teacher_ID}/{Class_ID}/{Section_ID}/{CATEGORY}/T4veGNVrhnTqUR
    fun getStudentListPhotoSession(
        @Header("Authorization") Authorization: String,
        @Field("class_id") class_id: String,
        @Field("section_id") section_id: String,
    ): Call<GetStudentsPhotoListMain>

    @Multipart
    @POST("saveStudentPhoto")
    fun UploadStudentPhoto(
        @Header("Authorization") Authorization: String,
        @Part("student_id") student_id: Int,
        @Part student_photo: MultipartBody.Part? = null,
    ): Call<SaveStudentPhoto>


    @FormUrlEncoded
    @POST("getMonthAttendanceHistory")
    fun getMonthAttendanceHistory(
        @Header("Authorization") Authorization: String,
        @Field("class_id") class_id: String,
        @Field("section_id") section_id: String,
        @Field("month") month: String,
        @Field("year") year: String,
    ): Call<StudentAttendanceHistory>

    @FormUrlEncoded
    @POST("getStudentMonthAttendanceHistory")
    fun getStudentMonthAttendanceHistory(
        @Header("Authorization") Authorization: String,
        @Field("class_id") class_id: String,
        @Field("section_id") section_id: String,
        @Field("month") month: String,
        @Field("year") year: String,
        @Field("student_id") student_id: String,
    ): Call<StudentAttendanceHistoryByDay>

    @FormUrlEncoded
    @POST("getFormat")
    fun getFormat(
        @Header("Authorization") Authorization: String,
        @Field("class_id") class_id: String,
        @Field("section_id") section_id: String,
    ) : Call<getFormat>

    @FormUrlEncoded
    @POST("getStudentForRemarks")
    fun getStudentForRemarks(
        @Header("Authorization") Authorization: String,
        @Field("class_id") class_id: String,
        @Field("section_id") section_id: String,
        @Field("format_id") format_id: String,
    ) : Call<getStudentForRemarks>

    @GET("teacher_remarks/{Teacher_ID}/{Exam_ID}/{Class_ID}/{Section_ID}")
    fun getTeacherRemarksStudents(
        @Path("Teacher_ID") teacher_id: String,
        @Path("Exam_ID") exam_id: String,
        @Path("Class_ID") class_id: String,
        @Path("Section_ID") section_id: String,
    ): Call<List<TeacherRemarks>>

    @POST("SaveStudentRemarks")
    fun saveStudentRemarks(
        @Header("Authorization") Authorization: String,
        //@Body requestBody: JsonObject,
        @Body teacherRequest: TeacherRemarksRequest,
    ): Call<SaveAttendance>

    @FormUrlEncoded
    @POST("deleteStudyMaterials")
    fun deleteStudyMaterials(
        @Header("Authorization") Authorization: String,
        @Field("id") id: String
    ) : Call<SaveAttendance>

}