package com.example.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import java.io.IOException
import java.util.Locale

class FetchLocationNameTask(private val mContext: Context, var location: Location) {
    private val TAG = FetchLocationNameTask::class.java.simpleName
    var locationName = ""

    fun fetchLocationName() {
        val geocoder = Geocoder(mContext, Locale.getDefault())

        // Get the passed in location
        //Location location = locations[0]; //main address stored in the location 0
        var addresses: List<Address>? = null
        locationName = ""
        try {
            addresses = geocoder.getFromLocation(
                location.latitude, location.longitude,
                1
            )
        } catch (ioException: IOException) {
            // Catch network or other I/O problems

            locationName = "Service not available"
            Log.e(TAG, locationName, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values
            locationName = "Invalid coordinates used"
            Log.e(
                TAG, locationName + ". " +
                        "Latitude = " + location.latitude +
                        ", Longitude = " +
                        location.longitude, illegalArgumentException
            )
        }

        // If no addresses found, print an error message.
        if (addresses == null || addresses.size == 0) {
            if (locationName.isEmpty()) {
                locationName = "No address found"
                Log.e(TAG, locationName)
            }
        } else {
            // If an address is found, read it into locationName
            val address = addresses.first()
            locationName = if (address.featureName != "null") address.featureName else "Unknown location"
        }
    }
}
