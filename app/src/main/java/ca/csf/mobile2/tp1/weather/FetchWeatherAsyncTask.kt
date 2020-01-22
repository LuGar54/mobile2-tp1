package ca.csf.mobile2.tp1.weather

import android.os.AsyncTask
import okhttp3.OkHttpClient
import okhttp3.Request
import ca.csf.mobile2.tp1.web.NetworkError
import ca.csf.mobile2.tp1.web.Promise
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.IOException
import java.net.HttpURLConnection


class FetchWeatherAsyncTask(
    val onSuccess: OnSuccess,
    val onError: OnError
) : AsyncTask<String, Unit, Promise<CityWeather, NetworkError>>() {

    //BC : Ceci n'est pas une constante.
    private val WEB_SERVICE_URL = "http://192.168.1.164:8080/api/v1/weather/"

    override fun doInBackground(vararg params: String): Promise<CityWeather, NetworkError> {

        val httpClient = OkHttpClient()
        val request = Request.Builder().url(WEB_SERVICE_URL + params[0]).build()

        try {
            httpClient.newCall(request).execute().use { response ->
                if (response.code == HttpURLConnection.HTTP_NOT_FOUND) {

                    return Promise.err(NetworkError.NOT_FOUND)

                } else if (!response.isSuccessful) {

                    return Promise.err(NetworkError.SERVER)

                } else {

                    val mapper = jacksonObjectMapper()
                    return Promise.ok(mapper.readValue<CityWeather>(response.body!!.string()))
                }

            }
        } catch (e: IOException) {
            e.printStackTrace()
            return Promise.err(NetworkError.CONNECTIVITY)
        }
    }

    override fun onPostExecute(promise: Promise<CityWeather, NetworkError>) {
        if (promise.isSuccessful) {
            onSuccess(promise.result!!)
        } else {
            onError(promise.error!!)
        }
    }

}

typealias OnSuccess = (CityWeather) -> Unit

typealias OnError = (NetworkError) -> Unit