package com.nexters.ticktock.autocomplete

import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.*
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.io.IOException


class GPSInfo(): Service(), LocationListener {
    
    constructor(ac: AppCompatActivity): this() {
        activity = ac
        geocoder = Geocoder(activity)
        getLocation()
    }

    class Result (var address: String, var latitude: Double, var longitude: Double) : Parcelable {
        constructor(source: Parcel) : this(
                source.readString(),
                source.readDouble(),
                source.readDouble()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(address)
            writeDouble(latitude)
            writeDouble(longitude)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Result> = object : Parcelable.Creator<Result> {
                override fun createFromParcel(source: Parcel): Result = Result(source)
                override fun newArray(size: Int): Array<Result?> = arrayOfNulls(size)
            }
        }
    }

    private lateinit var activity: AppCompatActivity
    private lateinit var geocoder: Geocoder// 좌표 - 주소 변환

    private val TAG:String = "GPSINFO"
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val GPS_PLACE_ID: String = "-1"

    // 현재 GPS 사용유무
    internal var isGPSEnabled = false

    // 네트워크 사용유무
    internal var isNetworkEnabled = false

    // GPS 상태값
    var isGetLocation = false

    internal var location: Location? = null
    internal var lat: Double = 0.0 // 위도
    internal var lon: Double = 0.0 // 경도

    protected var locationManager: LocationManager? = null

    /**
     * 위도값
     */
    val latitude: Double
        get() {
            if (location != null) {
                lat = location!!.getLatitude()
            }
            return lat
        }

    /**
     * 경도값
     */
    val longitude: Double
        get() {
            if (location != null) {
                lon = location!!.getLongitude()
            }
            return lon
        }

    fun getLocation(): Location? {
        try {
            locationManager = activity
                    .getSystemService(LOCATION_SERVICE) as LocationManager

            isGPSEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.GPS_PROVIDER)

            isNetworkEnabled = locationManager!!
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {
            } else {
                this.isGetLocation = true
                if (isNetworkEnabled) {
                    try {
                        locationManager!!.requestLocationUpdates(
                                LocationManager.NETWORK_PROVIDER,
                                MIN_TIME_UPDATES,
                                MIN_DISTANCE_UPDATES, this)

                        if (locationManager != null) {
                            location = locationManager!!
                                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                            if (location != null) {
                                // 위도 경도 저장
                                lat = location!!.getLatitude()
                                lon = location!!.getLongitude()
                            }
                        }
                    } catch (e:SecurityException) {
                        e.printStackTrace()
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        try {
                            locationManager!!
                                    .requestLocationUpdates(
                                            LocationManager.GPS_PROVIDER,
                                            MIN_TIME_UPDATES,
                                            MIN_DISTANCE_UPDATES,
                                            this)
                            if (locationManager != null) {
                                location = locationManager!!
                                        .getLastKnownLocation(LocationManager.GPS_PROVIDER)
                                if (location != null) {
                                    lat = location!!.getLatitude()
                                    lon = location!!.getLongitude()
                                }
                            }
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        }
                    }
                }
                if(lat!=0.0)
                    stopUsingGPS()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return location
    }

    /**
     * GPS 종료
     */
    fun stopUsingGPS() {
        if (locationManager != null) {
            locationManager!!.removeUpdates(this@GPSInfo)
        }
    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    fun isGPSConnected() {
        if (!checkLocationServicesStatus()) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("위치 서비스 비활성화")
            builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n" + "위치 설정을 수정하십시오.")
            builder.setCancelable(true)
            builder.setPositiveButton("설정", { dialogInterface, i ->
                val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                activity.startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
            })
            builder.setNegativeButton("취소", { dialogInterface, i -> dialogInterface.cancel() })
            builder.create().show()
        }
    }

    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLocationChanged(p0: Location?) {
        getLocation()
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {

    }

    override fun onProviderEnabled(p0: String?) {

    }

    override fun onProviderDisabled(p0: String?) {

    }

    companion object {

        // GPS 정보 업데이트 거리 10미터
        private val MIN_DISTANCE_UPDATES: Float = 10f

        // GPS 정보 업데이트 시간 1/1000
        private val MIN_TIME_UPDATES = (1000 * 60 * 1).toLong()
    }

    /*
     * 좌표 주소 변환
     */
    private fun getFromLocationToName(latLng: LatLng): Address? {

        var list: List<Address>? = null
        var address: Address? = null
        try {
            list = geocoder.getFromLocation(
                    latLng.latitude, // 위도
                    latLng.longitude, // 경도
                    1) // 얻어올 값의 개수
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "입출력 오류 - 서버에서 주소변환시 에러발생")
        }

        if (list != null) {
            if (list.isEmpty())
                Log.d(TAG, "해당되는 주소 정보는 없습니다")
            else {
                address = list[0]
                Log.d("tag", list[0].toString())
            }
        }
        return address
    }

    fun getGPSLocation(): PlaceAutocompleteRecyclerAdapter.PlaceAutocomplete {
        lateinit var result: PlaceAutocompleteRecyclerAdapter.PlaceAutocomplete
        if (isGetLocation) {
            var latitude = latitude
            var longitude = longitude

            var latLng = LatLng(latitude, longitude)
            var address = getFromLocationToName(latLng)

            while (address == null) {
                latitude += 0.00000001
                latLng = LatLng(latitude, longitude)
                address = getFromLocationToName(latLng)
            }
            lateinit var description:CharSequence
            if(address.getAddressLine(0).substring(0,4).equals("대한민국"))
                description = address.getAddressLine(0).substring(5)
            else
                description = address.getAddressLine(0)

            /*
             *  현위치 정보 set
             */
            result = PlaceAutocompleteRecyclerAdapter.PlaceAutocomplete(GPS_PLACE_ID, "현위치", description)
            result.latLng = LatLng(latitude, longitude)
            /*
             *  완료
             */
        } else {
            result = PlaceAutocompleteRecyclerAdapter.PlaceAutocomplete(GPS_PLACE_ID, "현위치 탐색중", "잠시만 기다려주세요")
            result.latLng = null
            Log.d(TAG, "cannot find")
        }
        return result
    }

    fun getResult(): Result {
        val result = Result("", 0.0, 0.0)

        if (latitude == 0.0 && longitude == 0.0)
            return result

        if (isGetLocation) {
            var latitude = latitude
            val longitude = longitude

            var latLng = LatLng(latitude, longitude)
            var address = getFromLocationToName(latLng)

            while (address == null) {
                latitude += 0.00000001
                latLng = LatLng(latitude, longitude)
                address = getFromLocationToName(latLng)
            }
            lateinit var description:CharSequence
            if(address.getAddressLine(0).substring(0,4).equals("대한민국"))
                description = address.getAddressLine(0).substring(5)
            else
                description = address.getAddressLine(0)

            /*
             *  현위치 정보 set
             */
            result.address = description.toString()
            result.latitude = latitude
            result.longitude = longitude
            /*
             *  완료
             */
        } else {
            Log.d(TAG, "cannot find")
        }
        return result
    }
}