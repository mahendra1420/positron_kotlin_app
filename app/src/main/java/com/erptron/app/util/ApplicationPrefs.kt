package com.positron.teachers.util

import android.content.Context
import android.content.SharedPreferences
import com.positron.teachers.model.StudentsInfo
import com.google.gson.Gson

class ApplicationPrefs(private val context: Context) {

    private lateinit var prefs: ApplicationPrefs
    private val PREF_NAME = "Carmel"
    private val FCM_TOKEN = "frcm_token"
    private val IS_LOGGED_IN = "is_logged_in"
    private val USER_DETAILS = "user_detail"
    private val ADMISSION_NUMBER = "admission_number"
    private val INSERT_ID = "insert_id"
    private val IMAGE  = "image"
    private val EMAIL = "email"
    private val NAME = "name"
    private val TEACHER_ID ="teacher_id"
    private val CONTACT_NO ="contact_no"
    private val Authorization ="authorization"

    private val CLASSTEACHERCLASS_ID = "getClassTeacherclassid"
    private val CLASSTEACHERCLASSNAME = "getClassTeacherclassname"
    private val TEACHERCLASSSECTION_ID = "getClassTeacherSectionid"
    private val TEACHERCLASSSECTION_NAME = "getClassTeacherSectionname"
    private val SUBJECTTEACHERCLASS_ID = "getSubjectTeacherclassid"
    private val SUBJECTTEACHERCLASS_NAME = "getSubjectTeacherclassname"
    private val SUBJECTTEACHERSECTION_ID = "getSubjectTeacherSectionid"
    private val SUBJECTTEACHERSECTION_NAME = "getSubjectTeacherSectionname"
    private val SUBJECT_CATEGORY_ID = "subject_category_id"
    private val SUBJECT_CATEGORY_NAME = "subject_category_name"




    private val EXAM_ID = "exam_id"
    private val EXAM_NAME = "exam_name"

    private val SUBJECT_ID = "subject_id"
    private val SUBJECT_NAME = "subject_name"

    private val SCHOOL_CODE = "school_code"
    private val SCHOOL_NAME = "school_name"
    private val ERP_URL = "erp_url"
    private val ADDRESS_LINE_1 = "address_line_1"
    private val schoolId = "SchoolId"
    private val ADDRESS_LINE_2 = "address_line_2"
    private val ADDRESS_LINE_3 = "address_line_3"
    private val SCHOOL_LOGO = "school_logo"
    private val SCHOOL_TAGLINE = "school_tagline"
    private val subscribeTopicName = "subscribeTopicName"
    private val employCode = "employCode"

    private val FORMAT_ID = "format_id"
    private val FORMAT_NAME = "format_name"

    private val UPDATED_STUDENT_ID = "updated_student_id"

    fun setUpdatedStudentId(id: String?) {
        setPreferencesData(UPDATED_STUDENT_ID, id)
    }

    fun setSubSubjectCategoryName(id: String?) {
        setPreferencesData(SUBJECT_CATEGORY_NAME, id)
    }

    fun getSubSubjectCategoryName(): String? {
        return getPreferenceData(SUBJECT_CATEGORY_NAME, "")
    }

    fun setSubjectCategoryId(id: String?) {
        setPreferencesData(SUBJECT_CATEGORY_ID, id)
    }

    fun getSubjectCategoryId(): String? {
        return getPreferenceData(SUBJECT_CATEGORY_ID, "")
    }
    fun setUserDetails(userDetail : StudentsInfo) {
        val gson = Gson()
        setPreferencesData(USER_DETAILS, gson.toJson(userDetail))
    }

    fun getUserDetails(): StudentsInfo {
        val gson = Gson()
        return gson.fromJson(getPreferenceData(USER_DETAILS, ""), StudentsInfo::class.java)
    }

    fun clear(){
        getPrefsEditor().clear().commit()
    }

    fun getInstance(context: Context): ApplicationPrefs {
        if (!this::prefs.isInitialized) {
            prefs = ApplicationPrefs(context)
        }
        return prefs
    }

    fun setFcmToken(userToken: String?) {
        setPreferencesData(FCM_TOKEN, userToken)
    }

    fun getFcmToken(): String? {
        return getPreferenceData(FCM_TOKEN, "")
    }

    fun setLoggedIn(name: Boolean) {
        setPreferencesData(IS_LOGGED_IN, name)
    }

    fun isLoggedIn(): Boolean {
        return getPreferenceData(IS_LOGGED_IN, false)
    }

    fun setAdmissionNumber(admissionNumber: String?) {
        setPreferencesData(ADMISSION_NUMBER, admissionNumber)
    }

    fun getAdmissionNumber(): String? {
        return getPreferenceData(ADMISSION_NUMBER, "")
    }

    fun setAuthorizationToken(authorization: String?) {
        setPreferencesData(Authorization, authorization)
    }

    fun getAuthorizationToken(): String? {
        return getPreferenceData(Authorization, "")
    }

    fun setEmployCode(EmployCode: String?) {
        setPreferencesData(employCode, EmployCode)
    }

    fun getEmployCode(): String? {
        return getPreferenceData(employCode, "")
    }

    fun setInsertId(admissionNumber: String?) {
        setPreferencesData(INSERT_ID, admissionNumber)
    }

    fun getInsertId(): String? {
        return getPreferenceData(INSERT_ID, "")
    }

    fun setImage(image: String?){
        setPreferencesData(IMAGE, image)
    }

    fun getImage(): String?{
        return getPreferenceData(IMAGE, "")
    }

    fun setEmail(email: String?){
        setPreferencesData(EMAIL, email)
    }

    fun getEmail(): String?{
        return getPreferenceData(EMAIL, "")
    }

    fun setName(name: String?){
        setPreferencesData(NAME, name)
    }

    fun getName(): String?{
        return getPreferenceData(NAME, "")
    }


    fun setTeacherId(tid: String?) {
        setPreferencesData(TEACHER_ID, tid)
    }

    fun getTeacherId(): String? {
        return getPreferenceData(TEACHER_ID, "")
    }

    fun setContactNo(tid: String?) {
        setPreferencesData(CONTACT_NO, tid)
    }

    fun getContactNo(): String? {
        return getPreferenceData(CONTACT_NO, "")
    }

    fun setSubscribeTopicName(subscribeTopicNamee: String?) {
        setPreferencesData(subscribeTopicName, subscribeTopicNamee)
    }

    fun getSubscribeTopicName(): String? {
        return getPreferenceData(subscribeTopicName, "")
    }

    //CLASSTEACHER
    fun setClassTeacherClassId(tid: String?) {
        setPreferencesData(CLASSTEACHERCLASS_ID, tid)
    }

    fun getClassTeacherClassId(): String? {
        return getPreferenceData(CLASSTEACHERCLASS_ID, "")
    }


    fun setClassTeacherClassName(tid: String?) {
        setPreferencesData(CLASSTEACHERCLASSNAME, tid)
    }

    fun getClassTeacherClassName(): String? {
        return getPreferenceData(CLASSTEACHERCLASSNAME, "")
    }

    fun setTeacherClassSectionId(tid: String?) {
        setPreferencesData(TEACHERCLASSSECTION_ID, tid)
    }

    fun getTeacherClassSectionId(): String? {
        return getPreferenceData(TEACHERCLASSSECTION_ID, "")
    }

    fun setTeacherClassSectionName(tid: String?) {
        setPreferencesData(TEACHERCLASSSECTION_NAME, tid)
    }

    fun getTeacherClassSectionName(): String? {
        return getPreferenceData(TEACHERCLASSSECTION_NAME, "")
    }

    //SUBJECTTEACHER

    fun setSubjectTeacherClassId(tid: String?) {
        setPreferencesData(SUBJECTTEACHERCLASS_ID, tid)
    }

    fun getSubjectTeacherClassId(): String? {
        return getPreferenceData(SUBJECTTEACHERCLASS_ID, "")
    }


    fun setSubjectTeacherClassName(tid: String?) {
        setPreferencesData(SUBJECTTEACHERCLASS_NAME, tid)
    }

    fun getSubjectTeacherClassName(): String? {
        return getPreferenceData(SUBJECTTEACHERCLASS_NAME, "")
    }

    fun setSubjectTeacherSectionId(tid: String?) {
        setPreferencesData(SUBJECTTEACHERSECTION_ID, tid)
    }

    fun getSubjectTeacherSectionId(): String? {
        return getPreferenceData(SUBJECTTEACHERSECTION_ID, "")
    }

    fun setSubjectTeacherSectionName(tid: String?) {
        setPreferencesData(SUBJECTTEACHERSECTION_NAME, tid)
    }

    fun getSubjectTeacherSectionName(): String? {
        return getPreferenceData(SUBJECTTEACHERSECTION_NAME, "")
    }

    //Exam
    fun setExamId(tid: String?) {
        setPreferencesData(EXAM_ID, tid)
    }

    fun getExamId(): String? {
        return getPreferenceData(EXAM_ID, "")
    }

    fun setExamName(tid: String?) {
        setPreferencesData(EXAM_NAME, tid)
    }

    fun getExamName(): String? {
        return getPreferenceData(EXAM_NAME, "")
    }

    //Subject
    fun setSubjectId(tid: String?) {
        setPreferencesData(SUBJECT_ID, tid)
    }

    fun getSubjectId(): String? {
        return getPreferenceData(SUBJECT_ID, "")
    }

    fun setSubjectName(tid: String?) {
        setPreferencesData(SUBJECT_NAME, tid)
    }

    fun getSubjectName(): String? {
        return getPreferenceData(SUBJECT_NAME, "")
    }

    /* -----------------------school ERP ------------------*/
    fun setSchoolCode(tid: String?) {
        setPreferencesData(SCHOOL_CODE, tid)
    }

    fun getSchoolCode(): String? {
        return getPreferenceData(SCHOOL_CODE, "")
    }

    fun setSchoolName(tid: String?) {
        setPreferencesData(SCHOOL_NAME, tid)
    }

    fun getSchoolName(): String? {
        return getPreferenceData(SCHOOL_NAME, "")
    }

    fun setSchoolLogo(tid: String?) {
        setPreferencesData(SCHOOL_LOGO, tid)
    }

    fun getSchoolLogo(): String? {
        return getPreferenceData(SCHOOL_LOGO, "")
    }

    fun setSchoolTag(tid: String?) {
        setPreferencesData(SCHOOL_TAGLINE, tid)
    }

    fun getSchoolTag(): String? {
        return getPreferenceData(SCHOOL_TAGLINE, "")
    }

    fun setErpUrl(tid: String?) {
        setPreferencesData(ERP_URL, tid)
    }

    fun getErpUrl(): String? {
        return getPreferenceData(ERP_URL, "")
    }

    fun setSchoolId(SchoolId: String?) {
        setPreferencesData(schoolId, SchoolId)
    }

    fun getSchoolId(): String? {
        return getPreferenceData(schoolId, "")
    }

    fun setAddress1(tid: String?) {
        setPreferencesData(ADDRESS_LINE_1, tid)
    }

    fun getAddress1(): String? {
        return getPreferenceData(ADDRESS_LINE_1, "")
    }

    fun setAddress2(tid: String?) {
        setPreferencesData(ADDRESS_LINE_2, tid)
    }

    fun getAddress2(): String? {
        return getPreferenceData(ADDRESS_LINE_2, "")
    }

    fun setAddress3(tid: String?) {
        setPreferencesData(ADDRESS_LINE_3, tid)
    }

    fun getAddress3(): String? {
        return getPreferenceData(ADDRESS_LINE_3, "")
    }


    fun setFormatId(tid: String?) {
        setPreferencesData(FORMAT_ID, tid)
    }

    fun getFormatId(): String? {
        return getPreferenceData(FORMAT_ID, "")
    }

    fun setFormatName(tid: String?) {
        setPreferencesData(FORMAT_NAME, tid)
    }

    fun getFormatName(): String? {
        return getPreferenceData(FORMAT_NAME, "")
    }

    /*
     * Save string data type
     *
     * */
    private fun setPreferencesData(key: String, value: String?) {
        val editor = getPrefsEditor()
        editor.putString(key, value)
        editor.commit()
    }

    /*
     * Save Int data type
     *
     * */
    private fun setPreferencesData(key: String, value: Int) {
        val editor = getPrefsEditor()
        editor.putInt(key, value)
        editor.commit()
    }

    private fun setPreferencesData(key: String, value: Boolean) {
        val editor = getPrefsEditor()
        editor.putBoolean(key, value)
        editor.commit()
    }

    private fun getPrefsEditor(): SharedPreferences.Editor {
        return getPrefs().edit()
    }


    private fun getPrefs(): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }


    /*
    *
    * fetch  method for pref
    *
    * */
    /*
     *  getPreferenceData String data from pref
     * */
    private fun getPreferenceData(key: String, defaultValue: String?): String? {
        return getPrefs().getString(key, defaultValue)
    }

    /*
     *  getPreferenceData int data from pref
     * */
    private fun getPreferenceData(key: String, defaultValue: Int): Int {
        return getPrefs().getInt(key, defaultValue)
    }

    private fun getPreferenceData(key: String, defaultValue: Boolean): Boolean {
        return getPrefs().getBoolean(key, defaultValue)
    }

}
