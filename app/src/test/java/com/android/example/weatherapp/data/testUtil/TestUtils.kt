package com.android.example.weatherapp.data.testUtil

import com.google.gson.Gson
import io.mockk.every
import io.mockk.mockk
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response

// copy : https://gist.github.com/nwellis/e70737bef504c29ac536aa181fedcc65

object TestUtils {

    fun newMockHttpException(
        statusCode: Int = 400,
        message: String = "Default mock error",
        response: Response<Any>? = mockk<Response<Any>>(relaxed = true)
            .applyMockDefaults<Any, Any>(statusCode = statusCode)
    ) = mockk<HttpException>().applyMockDefaults(statusCode, message, response)

    fun HttpException.applyMockDefaults(
        statusCode: Int = 400,
        message: String = "Default mock error",
        response: Response<Any>? = mockk<Response<Any>>(relaxed = true)
            .applyMockDefaults<Any, Any>(statusCode = statusCode)
    ) = apply {
        every { code() } returns statusCode
        every { message() } returns message
        every { response() } returns response
    }

    fun <Body, Error> Call<Body>.applyMockDefaults(
        statusCode: Int = 200,
        responseBody: Body? = null,
        errorBody: Error? = null,
        error: Throwable? = null
    ) = apply {
        var hasBeenExecuted = false
        if (error != null) {
            every { execute() } throws error
        } else {
            every {
                execute()
            } answers {
                hasBeenExecuted = true
                mockk<Response<Body>>().applyMockDefaults<Body, Error>()
            } andThenThrows IllegalStateException("Call has already been executed")
        }
        every { isExecuted } returns hasBeenExecuted
    }

    fun <Body, Error> Response<Body>.applyMockDefaults(
        statusCode: Int = 200,
        responseBody: Body? = null,
        errorBody: Error? = null
    ) = apply {
        every { isSuccessful } returns (statusCode in 200 until 300)
        every { code() } returns statusCode
        every { body() } returns responseBody.takeIf { isSuccessful }

        val errorJson = Gson().toJson(errorBody) // Will not parse dates correctly, use Koin?
        every { errorBody() } returns mockk<ResponseBody> {
            every { contentType() } returns "application/json; charset=utf-8".toMediaType()
            every { string() } returns errorJson
        }.takeIf { !isSuccessful }
    }
}