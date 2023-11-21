package com.example.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.text.TextUtils
import android.util.Log
import java.io.IOException
import java.util.Locale

public class FetchAddressTask(private val mContext: Context, var location: Location) {
    private val TAG = FetchAddressTask::class.java.simpleName
    var resultMessage = ""

    fun fetchAddress() {
        val geocoder = Geocoder(mContext, Locale.getDefault())

        // Get the passed in location
        //Location location = locations[0]; //main address stored in the location 0
        var addresses: List<Address>? = null
        resultMessage = ""
        try {
            addresses = geocoder.getFromLocation(
                location.latitude, location.longitude,
                1
            )
        } catch (ioException: IOException) {
            // Catch network or other I/O problems

            resultMessage = "Service not available"
            Log.e(TAG, resultMessage, ioException)
        } catch (illegalArgumentException: IllegalArgumentException) {
            // Catch invalid latitude or longitude values
            resultMessage = "Invalid coordinates used"
            Log.e(
                TAG, resultMessage + ". " +
                        "Latitude = " + location.latitude +
                        ", Longitude = " +
                        location.longitude, illegalArgumentException
            )
        }

        // If no addresses found, print an error message.
        if (addresses == null || addresses.size == 0) {
            if (resultMessage.isEmpty()) {
                resultMessage = "No address found"
                Log.e(TAG, resultMessage)
            }
        } else {
            // If an address is found, read it into resultMessage
            val address = addresses.first()
            val addressParts = ArrayList<String?>()

            // Fetch the address lines using getAddressLine,
            // join them, and send them to the thread
            for (i in 0..address.maxAddressLineIndex) {
                addressParts.add(address.getAddressLine(i))
            }
            resultMessage = TextUtils.join(
                "\n",
                addressParts
            )
        }
    }
}
