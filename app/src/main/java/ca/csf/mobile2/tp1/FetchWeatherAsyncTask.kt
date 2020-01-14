package ca.csf.mobile2.tp1

import android.os.AsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request
import ca.csf.mobile2.tp1.web.NetworkError
import ca.csf.mobile2.tp1.web.Promise
import okio.IOException
import java.net.HttpURLConnection


class FetchWeatherAsyncTask(
    val onSuccess: OnSuccess,
    val onError: OnError)
    : AsyncTask<String, Unit, Promise<CityWeather, NetworkError>>() {

    private val WEB_SERVICE_URL = "http://localhost:8080/api/v1/weather/"

    override fun doInBackground(vararg params: String): Promise<CityWeather, NetworkError> {

        val httpClient = OkHttpClient()
        val request = Request.Builder().url(WEB_SERVICE_URL + params).build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (response.code == HttpURLConnection.HTTP_NOT_FOUND) {

                    return Promise.err(NetworkError.NotFound)

                } else if (!response.isSuccessful) {

                    return Promise.err(NetworkError.Server)

                } else {
                    //jackson
                    return Promise.ok(CityWeather(response.body.toString()))
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Promise.err(NetworkError.Connectivity)
        }
    }

}

typealias OnSuccess = (CityWeather) -> Unit

typealias OnError = () -> Unit