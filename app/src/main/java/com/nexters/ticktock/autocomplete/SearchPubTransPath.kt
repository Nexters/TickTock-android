package com.nexters.ticktock.autocomplete

class SearchPubTransPath(val type: Int, val path: Path, val subPathList: ArrayList<SubPath>) {
    /*
     * 1-9-1 type:          1-지하철, 2-버스, 3-버스+지하철
     * 1-9-2 path:          경로 요약 정보
     * 1-9-3 subPathList:   이동 교통수단 정보 리스트
     */

    class Path(val totalWalk: Int, val totalTime: Int, val payment: Int, val walkCount: Int)
    /*
     *         totalwalk:   총 도보시간
     * 1-9-2-3 totalTime:   총 소요시간
     * 1-9-2-4 payment:     총 요금
     *         walkCount:   환승간 도보 횟수
     */

    class SubPath(val trafficType: Int, val sectionTime: Int, val lane: Lane, val startName: String, val endName: String)
    /*
     * 1-9-3-1 trafficType: 이동수단 종류 1-지하철, 2-버스, 3-도보
     * 1-9-3-3 sectionTime: 이동 소요시간
     * 1-9-3-5 lane:        교통수단 정보
     * 1-9-3-6 startName:   승차 정류장/역명
     * 1-9-3-9 endName:     하차 정류장/역명
     */

    class Lane(val name: String?, val busNo: String?)
    /*
     * 1-9-3-5-1 name:      지하철 노선명 (지하철인 경우에만 필수)
     * 1-9-3-5-2 busNo:     버스번호 (버스인 경우에만 필수)
     *
     * * * 버스 타입별 분류도 가능하면 시도 할 것
     */
}