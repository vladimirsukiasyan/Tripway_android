package com.tiparo.tripway.utils

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tiparo.tripway.models.entities.Error
import com.tiparo.tripway.models.repository.ResourceError
import com.tiparo.tripway.models.repository.ResourceErrorDAO
import okhttp3.ResponseBody

class ErrorUtils {
//    val API_KEY_NOT_PROVIDED:Int = 1002

    fun parseError(responseErrorBody: ResponseBody?, code: Int): ResourceErrorDAO {
        val responseString = responseErrorBody?.string() ?: ""

        val error = try {
            Gson().fromJson(responseString, ResourceError::class.java)
        } catch (exception: JsonSyntaxException) {
            ResourceError(responseString)
        }

        return checkErrorCode(error, code)
    }

    private fun checkErrorCode(
        error: ResourceError,
        code: Int
    ): ResourceErrorDAO {

        // if custom error message require
//        when (error.error!!.code) {
//            API_KEY_NOT_VALID -> { error.error!!.message }
//          else -> error
//        }
        return ResourceErrorDAO(Error(error.error, code), error.params)
    }
}