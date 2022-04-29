package com.budi.setiawan.storyappbudisetiawan.data.faker
import com.budi.setiawan.storyappbudisetiawan.api.apiinterface.UserInterface
import com.budi.setiawan.storyappbudisetiawan.data.response.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

class UserApiFaker : UserInterface {

    var mustThrowError = false

    override suspend fun login(email: String, password: String): Response<UserLoginResponse> {
        return if (mustThrowError) {
            Response.error(401, "".toResponseBody("text/plain".toMediaType()))
        } else {
            Response.success(
                UserLoginResponse(
                    loginResult = UserLoginResult("name", "id", "token"),
                    error = false,
                    message = "Success"
                )
            )
        }
    }

    override suspend fun register(
        name: String,
        email: String,
        password: String
    ): Response<RegisterResponse> {
        return if (mustThrowError) {
            Response.error(401, "".toResponseBody("text/plain".toMediaType()))
        } else {
            Response.success(
                RegisterResponse(error = false, message = "Success")
            )
        }
    }
}