package com.nexters.ticktock.autocomplete

import android.os.Parcel
import android.os.Parcelable

class SearchPubTransPath(val type: Int, val path: Path, val subPathList: ArrayList<SubPath>) : Parcelable {
    /*
     * 1-9-1 type:          1-지하철, 2-버스, 3-버스+지하철
     * 1-9-2 path:          경로 요약 정보
     * 1-9-3 subPathList:   이동 교통수단 정보 리스트
     */

    class Path(val totalWalk: Int, val totalTime: Int, val payment: Int, val walkCount: Int, var totalHour: Int, var totalMinute: Int) : Parcelable {
    /*
     *         totalwalk:   총 도보시간
     * 1-9-2-3 totalTime:   총 소요시간
     * 1-9-2-4 payment:     총 요금
     *         walkCount:   환승간 도보 횟수
     *         totalHour:   시
     *         totalMinute: 분
     */

        init {
            totalHour = totalTime / 60
            totalMinute = totalTime % 60
        }

        constructor(source: Parcel) : this(
                source.readInt(),
                source.readInt(),
                source.readInt(),
                source.readInt(),
                source.readInt(),
                source.readInt()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeInt(totalWalk)
            writeInt(totalTime)
            writeInt(payment)
            writeInt(walkCount)
            writeValue(totalHour)
            writeValue(totalMinute)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Path> = object : Parcelable.Creator<Path> {
                override fun createFromParcel(source: Parcel): Path = Path(source)
                override fun newArray(size: Int): Array<Path?> = arrayOfNulls(size)
            }
        }
    }

    class SubPath(val trafficType: Int, val sectionTime: Int, val lane: Lane, val startName: String, val endName: String) : Parcelable {
    /*
     * 1-9-3-1 trafficType: 이동수단 종류 1-지하철, 2-버스, 3-도보
     * 1-9-3-3 sectionTime: 이동 소요시간
     * 1-9-3-5 lane:        교통수단 정보
     * 1-9-3-6 startName:   승차 정류장/역명
     * 1-9-3-9 endName:     하차 정류장/역명
     */

        constructor(source: Parcel) : this(
                source.readInt(),
                source.readInt(),
                source.readParcelable<Lane>(Lane::class.java.classLoader),
                source.readString(),
                source.readString()
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeInt(trafficType)
            writeInt(sectionTime)
            writeParcelable(lane, 0)
            writeString(startName)
            writeString(endName)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SubPath> = object : Parcelable.Creator<SubPath> {
                override fun createFromParcel(source: Parcel): SubPath = SubPath(source)
                override fun newArray(size: Int): Array<SubPath?> = arrayOfNulls(size)
            }
        }
    }

    class Lane(val name: String?, val busNo: String?, val type: Int?, val subwayCode: Int?) : Parcelable {
    /*
     * 1-9-3-5-1 name:      지하철 노선명 (지하철인 경우에만 필수)
     * 1-9-3-5-2 busNo:     버스번호 (버스인 경우에만 필수)
     * 1-9-3-5-3 type:      버스타입 (버스인 경우에만 필수)
     * 1-9-3-5-5 subwayCode 지하철 노선 번호(지하철인 경우에만 필수)
     */

        constructor(source: Parcel) : this(
                source.readString(),
                source.readString(),
                source.readValue(Int::class.java.classLoader) as Int?,
                source.readValue(Int::class.java.classLoader) as Int?
        )

        override fun describeContents() = 0

        override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
            writeString(name)
            writeString(busNo)
            writeValue(type)
            writeValue(subwayCode)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<Lane> = object : Parcelable.Creator<Lane> {
                override fun createFromParcel(source: Parcel): Lane = Lane(source)
                override fun newArray(size: Int): Array<Lane?> = arrayOfNulls(size)
            }
        }
    }


    constructor(source: Parcel) : this(
            source.readInt(),
            source.readParcelable<Path>(Path::class.java.classLoader),
            source.createTypedArrayList(SubPath.CREATOR)
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeInt(type)
        writeParcelable(path, 0)
        writeTypedList(subPathList)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<SearchPubTransPath> = object : Parcelable.Creator<SearchPubTransPath> {
            override fun createFromParcel(source: Parcel): SearchPubTransPath = SearchPubTransPath(source)
            override fun newArray(size: Int): Array<SearchPubTransPath?> = arrayOfNulls(size)
        }
    }
}