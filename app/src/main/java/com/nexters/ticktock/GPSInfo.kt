package com.nexters.ticktock

import android.os.Bundle
import android.content.Intent
import android.os.IBinder
import android.app.Service
import android.content.Context
import android.location.LocationManager
import android.location.Location
import android.location.LocationListener


class GPSInfo(private val mContext: Context) : Service(), LocationListener {

    // 현재 GPS 사용유무
    internal var isGPSEnabled = false

    // 네트워크 사용유무
    internal var isNetworkEnabled = false

    // GPS 상태값
    var isGetLocation = false

    internal var location: Location? = null
    internal var lat: Double = 0.toDouble() // 위도
    internal var lon: Double = 0.toDouble() // 경도

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

    init {
        getLocation()
    }

    fun getLocation(): Location? {
        try {
            locationManager = mContext
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
}