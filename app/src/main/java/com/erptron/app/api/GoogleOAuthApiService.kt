package com.positron.teachers.api

import com.positron.teachers.model.AccessTokenResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GoogleOAuthApiService {
    @FormUrlEncoded
    @POST("token")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
      //  @Field("code") code: String,
       // @Field("redirect_uri") redirectUri: String,
       // @Field("grant_type") grantType: String = "authorization_code"
    ): Call<AccessTokenResponse>
}