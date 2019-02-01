package com.nexters.ticktock

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompletePrediction
import com.google.android.gms.location.places.AutocompletePredictionBuffer
import com.google.android.gms.location.places.Places
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import java.util.concurrent.TimeUnit


class PlaceAutocompleteAdapter(
        private val context: Context,
        private val resource:Int,
        private val googleApiClient: GoogleApiClient,
        private var bounds:LatLngBounds?,
        private var filter:AutocompleteFilter?
): RecyclerView.Adapter<PlaceAutocompleteAdapter.PlaceViewHolder>(), Filterable{

    interface PlaceAutoCompleteInterface {
        fun onPlaceClick(resultList:ArrayList<PlaceAutocomplete> , position:Int)
    }

    private var listener:PlaceAutoCompleteInterface
    private val TAG = "PLACE_AUTOCOMPLETE_ADAPTER"
    private val GPS_PLACE_ID: String = "-1"

    private var resultList:ArrayList<PlaceAutocomplete> = ArrayList()
    public var placeGPS:PlaceAutocomplete = PlaceAutocomplete(GPS_PLACE_ID, "현위치 탐색중", "잠시만 기다려주세요") // 현위치

    init {
        listener = context as PlaceAutoCompleteInterface
    }

    fun clearList() {
        if(resultList.size > 0) {
            resultList.clear()
        }
    }

    override fun getFilter(): Filter {

        return object : Filter() {

            override fun performFiltering(constraint: CharSequence?): FilterResults {

                val results = FilterResults()
                // 제약 조건이 주어지지 않으면 자동 완성 쿼리를 건너 뜁니다.
                if (constraint != null) {
                    //(제약) 검색 문자열에 대한 자동 완성 API를 쿼리하십시오.
                    resultList = getAutocomplete(constraint)

                    if (resultList.size > 0) {
                        //API가 성공적으로 결과를 반환했습니다.
                        results.values = resultList
                        results.count = resultList.size
                    }
                }

                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults?) {

                if (results != null && results.count > 0) {
                    //API가 하나 이상의 결과를 반환하고 데이터를 업데이트합니다.
                    notifyDataSetChanged()
                } else {
                    //API가 결과를 반환하지 않았고 데이터 세트를 무효화
                    //notifyDataSetInvalidated();
                }
            }
        }
    }

    private fun getAutocomplete(constraint: CharSequence): ArrayList<PlaceAutocomplete> {
        if(googleApiClient.isConnected) {
            // 자동 완성 API에 쿼리를 제출하고 PendingResult를 검색합니다.
            // 쿼리가 완료되면 결과를 포함합니다.
            val results:PendingResult<AutocompletePredictionBuffer>
                    = Places
                    .GeoDataApi
                    .getAutocompletePredictions(googleApiClient, constraint.toString(), bounds, filter)

            // 이 메소드는 기본 UI 스레드에서 호출되어야합니다. API의 결과를 차단하고 최대 60 초 동안 기다립니다.
            val autocompletePredictions:AutocompletePredictionBuffer = results.await(60, TimeUnit.SECONDS)

            // 쿼리가 성공적으로 완료되었는지 확인하고, 그렇지 않으면 null을 반환합니다.
            val status:Status = autocompletePredictions.status
            if(!status.isSuccess) {
                Log.e("", "Error getting autocomplete prediction API call: $status")
                autocompletePredictions.release()

                return ArrayList()
            }
            Log.i("", "Query completed. Received " + autocompletePredictions.count + " predictions.")

            // 버퍼를 고정 할 수 없으므로 결과를 자체 데이터 구조에 복사합니다.
            // AutocompletePrediction 객체는 API 응답 (장소 ID 및 설명)을 캡슐화합니다.
            val iterator:Iterator<AutocompletePrediction> = autocompletePredictions.iterator()

            val resultList:ArrayList<PlaceAutocomplete> = ArrayList(autocompletePredictions.count + 1)

            // 현위치 추가
            resultList.add(placeGPS)

            while (iterator.hasNext()) {
                val prediction = iterator.next()
                // 세부 정보를 가져 와서 새로운 PlaceAutocomplete 객체로 복사합니다.
                resultList.add(PlaceAutocomplete(prediction.placeId as CharSequence,
                        prediction.getPrimaryText(null).toString(), prediction.getFullText(null)))
            }
            // 모든 데이터가 복사되었으므로 버퍼를 해제
            autocompletePredictions.release()
            return resultList
        }
        return ArrayList()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): PlaceViewHolder {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val convertView: View = layoutInflater.inflate(resource, viewGroup, false)
        val predictionHolder = PlaceViewHolder(convertView)

        return predictionHolder
    }

    override fun getItemCount(): Int {
        return resultList.size
    }

    override fun onBindViewHolder(predictionHolder: PlaceViewHolder, i: Int) {
        predictionHolder.addressTitle.text = resultList.get(i).title
        if(resultList.get(i).description.substring(0,4).equals("대한민국"))
            predictionHolder.addressDetail.text = resultList.get(i).description.substring(5)
        else
            predictionHolder.addressDetail.text = resultList.get(i).description
        predictionHolder.parentLayout.setOnClickListener({
            listener.onPlaceClick(resultList, i)
        })
    }

    class PlaceViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val parentLayout = itemView.findViewById(R.id.predicted_row) as ConstraintLayout
        val addressTitle = itemView.findViewById(R.id.tv_address_title) as TextView
        val addressDetail = itemView.findViewById(R.id.tv_address_detail) as TextView
    }

    // 지역정보 소유자 데이터 자동완성 API 결과
    class PlaceAutocomplete(var placeId: CharSequence, var title:CharSequence, var description: CharSequence) {

        var latLng:LatLng? = null
        override fun toString(): String {
            return description.toString()
        }
    }
}