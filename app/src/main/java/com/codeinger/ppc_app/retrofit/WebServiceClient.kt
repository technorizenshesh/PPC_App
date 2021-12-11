package  com.iratepro.retrofit

import com.codeinger.ppc_app.retrofit.Constant
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit


object WebServiceClient {

    val client: Retrofit
        get() {
            val gson = GsonBuilder()
                .setLenient()
                .create()
           var interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
           var okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .followRedirects(true)
                .followSslRedirects(true)
                .retryOnConnectionFailure(true)
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .writeTimeout(300, TimeUnit.SECONDS)
                .cache(null)
                .build()

         //   if (retrofit == null) {
             var retrofit = Retrofit.Builder()
                    .baseUrl(Constant.AppUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build()
          //  }
            return retrofit!!
        }

    val client1: Retrofit
        get() {
            val gson = GsonBuilder()
                    .setLenient()
                    .create()
            var interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            var okHttpClient = OkHttpClient.Builder()
                    .addInterceptor(object : Interceptor {
                        @Throws(IOException::class)
                        override fun intercept(chain: Interceptor.Chain): Response? {
                            val original: Request = chain.request()
                            val requestBuilder: Request.Builder = original.newBuilder()
                                    .addHeader("content-type", "application/json")
                                    .addHeader("accept", "text/plain")
                                    .addHeader(Constant.token, "160aef07-ff0b-4e4d-800c-05b474e76a1a")
                            val request: Request = requestBuilder.build()
                            return chain.proceed(request)
                        }
                    })
                    .followRedirects(true)
                    .followSslRedirects(true)
                    .retryOnConnectionFailure(true)
                    .connectTimeout(300, TimeUnit.SECONDS)
                    .readTimeout(300, TimeUnit.SECONDS)
                    .writeTimeout(300, TimeUnit.SECONDS)
                    .cache(null)
                    .build()

            //   if (retrofit == null) {
            var retrofit = Retrofit.Builder()
                    .baseUrl(Constant.AppUrl)
                   // .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
            //  }
            return retrofit!!


        /*    val httpClient = OkHttpClient.Builder()
            httpClient.addInterceptor(object : Interceptor() {
                @Throws(IOException::class)
                fun intercept(chain: Chain): Response? {
                    val original: Request = chain.request()
                    val requestBuilder: Request.Builder = original.newBuilder()
                            .addHeader("ContentType", "application/x-www-form-urlencoded")
                    val request: Request = requestBuilder.build()
                    return chain.proceed(request)
                }
            })
            val client = httpClient.build()*/






        }


}