package com.iratepro.retrofit

import android.content.Context
import android.net.Uri
import com.codeinger.ppc_app.model.MemberShipDetailModel
import com.codeinger.ppc_app.model.MemberShipPlanModel
import com.codeinger.ppc_app.retrofit.FileUtils
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


    @GET("Membership/GetMembershipList")
    fun getMembershipPlan(): Call<List<MemberShipPlanModel>>


    @POST("Membership/CreateMembership")
    fun createMemberShip(@Body body: RequestBody): Call<Object>


    @GET("Membership/GetMembershipDetails")
    fun getMembershipDetail(): Call<MemberShipDetailModel>


    @POST("Membership/EditMembership")
    fun editMemberShipss(@Body body: RequestBody): Call<Object>





   /* @GET("bill-categories")
    fun getCablesCat(@HeaderMap header: Map<String?, String?>?, @Query("cables") internet: String?): Call<CableModel?>?
    */



}