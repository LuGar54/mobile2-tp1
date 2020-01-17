package ca.csf.mobile2.tp1.weather

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ca.csf.mobile2.tp1.web.NetworkError
import android.app.Activity
import android.view.inputmethod.InputMethodManager
import ca.csf.mobile2.tp1.R
import ca.csf.mobile2.tp1.widget.hide
import ca.csf.mobile2.tp1.widget.onSubmit
import ca.csf.mobile2.tp1.widget.show
import kotlinx.android.synthetic.main.activity_weather.*

class WeatherActivity : AppCompatActivity() {

    private val weatherIcons = mapOf(
        "RAIN" to R.drawable.ic_rain,
        "SUNNY" to R.drawable.ic_sunny,
        "PARTLY_SUNNY" to R.drawable.ic_partly_sunny,
        "CLOUDY" to R.drawable.ic_cloudy,
        "SNOW" to R.drawable.ic_snow
    )

    private val errorMessages = mapOf(
        NetworkError.CONNECTIVITY to R.string.connect_error,
        NetworkError.NOT_FOUND to R.string.not_found,
        NetworkError.SERVER to R.string.server_error
    )

    private var searching = false
    private var weather: CityWeather? = null
    private var error: NetworkError? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        searchBar.onSubmit { onSubmit() }
        retryButton.setOnClickListener { onSubmit() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putString(SEARCH, searchBar.text.toString())

        val hasWeather = weather != null
        val hasError = error != null

        outState.putBoolean(IS_SEARCHING, searching)
        outState.putBoolean(HAS_WEATHER, hasWeather)
        outState.putBoolean(HAS_ERROR, hasError)

        if (hasWeather) {
            outState.putParcelable(WEATHER, weather)
        } else if (hasError) {
            outState.putString(ERROR, error.toString())
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        searchBar.setText(savedInstanceState.getString(SEARCH))

        val hasWeather = savedInstanceState.getBoolean(HAS_WEATHER)
        val hasError = savedInstanceState.getBoolean(HAS_ERROR)

        if(savedInstanceState.getBoolean(IS_SEARCHING)) {
            onSubmit()
            return
        }

        if (hasWeather) {
            showWeatherScreen(savedInstanceState.getParcelable(WEATHER)!!)
        } else if (hasError) {
            showError(NetworkError.valueOf(savedInstanceState.getString(ERROR)!!))
        }
    }

    private fun onSubmit() {
        if (searchBar.text.isBlank())
            return

        weatherGroup.hide()
        errorGroup.hide()
        progressBar.show()

        searching = true

        //https://stackoverflow.com/questions/1109022/close-hide-android-soft-keyboard
        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(searchBar.windowToken, 0)

        FetchWeatherAsyncTask(this::showWeatherScreen, this::showError).execute(searchBar.text.toString())
    }

    private fun showWeatherScreen(cityWeather: CityWeather) {
        progressBar.hide()
        weatherGroup.show()

        searching = false
        weather = cityWeather
        temperature.text = String.format(getString(R.string.degrees), cityWeather.temperatureInCelsius)
        weatherType.setImageResource(weatherIcons[cityWeather.type] ?: error("Missing weather icon"))
        city.text = cityWeather.city
    }

    private fun showError(networkError: NetworkError) {
        progressBar.hide()
        errorGroup.show()

        searching = false
        error = networkError
        errorText.text = getString(errorMessages[networkError] ?: error("Missing network error type"))
    }
}

private const val IS_SEARCHING = "IS_SEARCHING"
private const val SEARCH = "SEARCH"
private const val HAS_WEATHER = "HAS_WEATHER"
private const val HAS_ERROR = "HAS_ERROR"
private const val WEATHER = "WEATHER"
private const val ERROR = "ERROR"