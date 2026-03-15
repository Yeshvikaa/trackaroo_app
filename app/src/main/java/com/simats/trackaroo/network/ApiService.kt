package com.simats.trackaroo.network

import com.simats.trackaroo.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    // --------------------- STUDENT --------------------- //

    @POST("student_signup.php")   // your PHP file in backend
    fun studentSignup(
        @Body request: StudentSignupRequest
    ): Call<StudentSignupResponse>

    @POST("driver_signup.php")
    fun driverSignup(@Body request: DriverSignupRequest): Call<DriverSignupResponse>

    @POST("admin_signup.php")
    fun adminSignup(@Body request: AdminSignupRequest): Call<AdminSignupResponse>

    @POST("add_route.php")
    fun addRoute(@Body request: AddRouteRequest): Call<AddRouteResponse>

    @POST("manage_routes.php")
    fun getRoutes(): Call<ManageRoutesResponse>


    @POST("delete_route.php")
    fun deleteRoute(
        @Body request: DeleteRouteRequest
    ): Call<DeleteRouteResponse>


    @POST("student_login.php")
    fun studentLogin(@Body request: StudentLoginRequest): Call<AuthResponse>

    @POST("student_dashboard.php")
    fun getStudentRoute(@Body body: Map<String, String>): Call<StudentRouteResponse>

    // --------------------- PARENT --------------------- //
    @POST("parent_signup.php")
    fun parentSignup(@Body request: ParentSignupRequest): Call<ParentSignupResponse>

    @POST("parent_login.php")
    fun parentLogin(@Body request: ParentLoginRequest): Call<ParentLoginResponse>

    @GET("assign_driver_1.php")
    fun getAssignDriverRoutes(): Call<AssignDriver1Response>

    @POST("driver_management.php")
    fun getDrivers(): Call<DriverManagementResponse>

    @GET("student_database.php")
    fun getStudents(): Call<StudentDatabaseResponse>

    // Admin dashboard API
    @GET("admin_dashboard.php")
    fun getAdminDashboard(): Call<AdminDashboardResponse>

    @POST("parent_dashboard.php")
    fun getParentDashboard(@Body request: ParentDashboardRequest): Call<ParentDashboardResponse>

    @POST("driver_navigation.php")
    fun getDriverNavigation(@Body route: Map<String, String>): Call<DriverNavigationResponse>

    // Get contacts from backend
    @GET("contacts.php")
    fun getContacts(): Call<ContactsResponse>

    @GET("driver_contacts.php")
    fun getDriverContacts(): Call<DriverContactsResponse>

    @POST("assign_driver_2.php")
    fun getAssignedDrivers(@Body body: Map<String, String?>): Call<AssignDriver2Response>

    @POST("assign_driver_3.php")
    fun assignDriverToRoute(@Body body: Map<String, String?>): Call<AssignDriver3Response>

    @POST("admin_contacts.php")
    fun addAdminContacts(@Body request: AdminContactsRequest): Call<AdminContactsResponse>

    @POST("send_emergency.php")
    fun sendEmergency(@Body request: EmergencyRequest): Call<EmergencyResponse>

    @POST("driver_login.php")
    fun driverLogin(@Body request: DriverLoginRequest): Call<DriverLoginResponse>

    @POST("driver_account_setup.php")
    fun driverAccountSetup(@Body request: DriverAccountSetupRequest): Call<DriverAccountSetupResponse>

    @POST("driver_forgot_id_email.php")
    fun driverForgotPassword(@Body request: Map<String, String>): Call<DriverForgotPasswordResponse>

    @FormUrlEncoded
    @POST("driver_otp.php")
    fun driverOtp(
        @Field("action") action: String,
        @Field("driver_id") driverId: String,
        @Field("otp") otp: String? = null
    ): Call<DriverOtpResponse>

    @FormUrlEncoded
    @POST("driver_new_password.php")
    fun driverNewPassword(
        @Field("driver_id") driverId: String,
        @Field("new_password") newPassword: String
    ): Call<DriverNewPasswordResponse>

    @POST("student_account_setup.php")
    fun studentAccountSetup(@Body request: StudentAccountSetupRequest): Call<StudentAccountSetupResponse>

    @POST("admin_login.php")
    fun adminLogin(
        @Body request: AdminLoginRequest
    ): Call<AdminLoginResponse>
}

