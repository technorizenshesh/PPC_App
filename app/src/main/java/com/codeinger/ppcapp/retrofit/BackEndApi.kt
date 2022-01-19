package com.iratepro.retrofit

import android.content.Context
import android.net.Uri
import com.codeinger.ppcapp.model.GetTranscationsModal
import com.codeinger.ppcapp.model.MemberShipDetailModel
import com.codeinger.ppcapp.model.MemberShipPlanModel
import com.codeinger.ppcapp.retrofit.FileUtils
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File


interface BackEndApi {

     companion object {
        fun prepareFilePart(
                context: Context?,
                partName: String?,
                fileUripath: String
        ): MultipartBody.Part {
            return if (fileUripath.isNotEmpty()) {
                val fileUri = Uri.fromFile(File(fileUripath))
                val file: File = FileUtils.getFile(context, fileUri)
                // create RequestBody instance from file
                val requestFile = RequestBody.create(
                        MediaType.parse("multipart/form-data"),
                        file
                )
                MultipartBody.Part.createFormData(partName, file.name, requestFile)
            } else {
                val attachmentEmpty =
                    RequestBody.create(MediaType.parse("text/plain"), "")
                MultipartBody.Part.createFormData("attachment", "", attachmentEmpty)
            }
        }
    }

    @POST("User/Signup")
    fun signupUser(@Body body: RequestBody): Call<Object>

    @POST("User/Login")
    fun login(@Body body: RequestBody): Call<Object>

    @POST("User/SignupGoogle")
    fun userGpluslogin(@Body body: RequestBody): Call<Object>

    @POST("User/SignupFacebook")
    fun userFblogin(@Body body: RequestBody): Call<Object>

    @POST("Payment/AddDonation")
    fun add_donation(@Body body: RequestBody): Call<Object>


    @GET("Membership/GetMembershipList")
    fun getMembershipPlan(): Call<List<MemberShipPlanModel>>

    @GET("Payment/GetTranscationsList?TotalRows=20")
    fun GetTranscationsList(): Call<List<GetTranscationsModal>>


    @POST("Membership/CreateMembership")
    fun createMemberShip(@Body body: RequestBody): Call<Object>


    @GET("Membership/GetMembershipDetails")
    fun getMembershipDetail(): Call<MemberShipDetailModel>


    @POST("Membership/EditMembership")
    fun editMemberShipss(@Body body: RequestBody): Call<Object>


    @GET("User/ForgetPassword/{Email}")
    fun forgotPass(@Path("Email") Email: String): Call<Object>


    @POST("User/ResetPassword")
    fun userResetPass(@Body body: RequestBody): Call<Object>



   /* @GET("bill-categories")
    fun getCablesCat(@HeaderMap header: Map<String?, String?>?, @Query("cables") internet: String?): Call<CableModel?>?
    */
}