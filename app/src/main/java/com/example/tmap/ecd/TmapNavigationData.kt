package com.example.tmap.ecd


import android.os.Bundle

/**
 * TMAP EDC SDK에서 제공하는 TBT (Turn-By-Turn) 정보를 담는 데이터 클래스.
 * Bundle 내부에 중첩된 형태로 제공됩니다.
 */
data class TBTInfo(
    val nTBTTime: Int, // 다음 안내까지 남은 시간 (초)
    val szCrossName: String, // 교차로 이름
    val nTBTDist: Int, // 다음 안내까지 남은 거리 (m)
    val nTBTTurnType: Int, // 회전 유형 코드 (SDK 문서 참조하여 해석 필요)
    val hasNvx: Boolean, // NVX 정보 유무
    val nTollFee: Int, // 통행료 (-1이면 정보 없음)
    val szRoadName: String, // 안내 도로명
    val nExtcVoiceCode: Int, // 추가 음성 안내 코드
    val szNearDirName: String, // 가까운 방향 도로명
    val szTBTMainText: String, // 주요 안내 문구 (예: "500m 앞에서 좌회전")
    val szFarDirName: String, // 먼 방향 도로명
    val szMidDirName: String // 중간 방향 도로명
) {
    companion object {
        /**
         * 주어진 Bundle에서 TBTInfo 객체를 생성하는 팩토리 메서드.
         */
        fun fromBundle(bundle: Bundle?): TBTInfo? {
            if (bundle == null) {
                return null
            }
            return TBTInfo(
                nTBTTime = bundle.getInt("nTBTTime", 0),
                szCrossName = bundle.getString("szCrossName", "") ?: "",
                nTBTDist = bundle.getInt("nTBTDist", 0),
                nTBTTurnType = bundle.getInt("nTBTTurnType", -1),
                hasNvx = bundle.getBoolean("hasNvx", false),
                nTollFee = bundle.getInt("nTollFee", -1),
                szRoadName = bundle.getString("szRoadName", "") ?: "",
                nExtcVoiceCode = bundle.getInt("nExtcVoiceCode", 0),
                szNearDirName = bundle.getString("szNearDirName", "") ?: "",
                szTBTMainText = bundle.getString("szTBTMainText", "") ?: "",
                szFarDirName = bundle.getString("szFarDirName", "") ?: "",
                szMidDirName = bundle.getString("szMidDirName", "") ?: ""
            )
        }
    }
}

/**
 * TMAP EDC SDK에서 제공하는 SDI (Speed Detector Info) 정보를 담는 데이터 클래스.
 * Bundle 내부에 중첩된 형태로 제공됩니다.
 */
data class SDIInfo(
    val nSdiDist: Int, // 단속 지점까지 남은 거리 (m)
    val bIsChangeableSpeedType: Boolean, // 가변 속도 제한 구역 여부
    val nSdiSpeedLimit: Int, // 단속 제한 속도 (km/h)
    val nSdiSection: Int, // 단속 구간 정보
    val bIsLimitSpeedSignChanged: Boolean, // 제한 속도 표지판 변경 여부
    val bSdiBlockSection: Boolean, // 구간 단속 여부
    val nTruckLimit: String, // 트럭 제한 속도 (트럭 제한이 없는 경우 0.0)
    val nSdiBlockSpeed: Int, // 구간 단속 시 현재 구간 평균 속도
    val nSdiBlockDist: Int, // 구간 단속 시 남은 구간 거리
    val nSdiBlockTime: Int, // 구간 단속 시 남은 구간 시간
    val nSdiType: Int, // 단속 유형 코드 (SDK 문서 참조하여 해석 필요)
    val nSdiBlockAverageSpeed: Int // 구간 단속 시 현재 구간 평균 속도 (nSdiBlockSpeed와 동일할 수 있음)
) {
    companion object {
        /**
         * 주어진 Bundle에서 SDIInfo 객체를 생성하는 팩토리 메서드.
         */
        fun fromBundle(bundle: Bundle?): SDIInfo? {
            if (bundle == null) {
                return null
            }
            return SDIInfo(
                nSdiDist = bundle.getInt("nSdiDist", 0),
                bIsChangeableSpeedType = bundle.getBoolean("bIsChangeableSpeedType", false),
                nSdiSpeedLimit = bundle.getInt("nSdiSpeedLimit", 0),
                nSdiSection = bundle.getInt("nSdiSection", 0),
                bIsLimitSpeedSignChanged = bundle.getBoolean("bIsLimitSpeedSignChanged", false),
                bSdiBlockSection = bundle.getBoolean("bSdiBlockSection", false),
                nTruckLimit = bundle.getString("nTruckLimit", ""),
                nSdiBlockSpeed = bundle.getInt("nSdiBlockSpeed", 0),
                nSdiBlockDist = bundle.getInt("nSdiBlockDist", 0),
                nSdiBlockTime = bundle.getInt("nSdiBlockTime", 0),
                nSdiType = bundle.getInt("nSdiType", 0),
                nSdiBlockAverageSpeed = bundle.getInt("nSdiBlockAverageSpeed", 0)
            )
        }
    }
}

/**
 * TMAP EDC SDK에서 1초마다 제공하는 주행 및 내비게이션 정보를 담는 메인 데이터 클래스.
 */
data class TmapNavigationData(
    val destinationName: String, // 목적지 이름
    val limitSpeed: Int, // 현재 도로의 제한 속도 (km/h)
    val isNightMode: Boolean, // 야간 모드 여부
    val remainTimeToDestinationInSec: Int, // 목적지까지 남은 시간 (초)
    val noLocationSignal: Boolean, // GPS 신호 없음 여부
    val remainViaPointSize: Int, // 남은 경유지 개수
    val gpsState: Int, // GPS 상태 코드 (0: 초기화, 1: 탐색 중, 2: 사용 가능, SDK 문서 참조)
    val secondTBTInfo: TBTInfo?, // 두 번째 턴바이턴 안내 정보
    val currentCourseAngle: Int, // 현재 진행 방향 각도 (0-360)
    val isShadeArea: Boolean, // 음영 지역 여부
    val hipassLaneCount: Int, // 하이패스 차선 수
    val laneAvailableInfo: IntArray?, // 차선 이용 가능 정보 (Int 배열, SDK 문서 참조하여 해석 필요)
    val showLane: Boolean, // 차선 정보 표시 여부
    val isCaution: Boolean, // 주의 구간 여부 (예: 급커브, 낙석 등)
    val remainDistanceToDestinationInMeter: Int, // 목적지까지 남은 총 거리 (m)
    val laneEtcInfo: IntArray?, // 차선 기타 정보 (Int 배열, SDK 문서 참조하여 해석 필요)
    val hasTbtInfo: Boolean, // 턴바이턴 정보 유무
    val laneTurnInfo: IntArray?, // 차선별 회전 정보 (Int 배열, SDK 문서 참조하여 해석 필요)
    val speedInKmPerHour: Int, // 현재 주행 속도 (km/h)
    val laneDistance: Int, // 차선까지의 거리 (정확한 의미는 SDK 문서 참조)
    val firstSDIInfo: SDIInfo?, // 첫 번째 속도 단속 정보
    val averageSpeed: Int, // 현재 구간 평균 속도 (정확한 의미는 SDK 문서 참조)
    val currentRoutePlan: Int, // 현재 경로 계획 번호/유형 (SDK 문서 참조)
    val laneCount: Int, // 현재 도로의 차선 수
    val matchedLatitude: Double, // 맵 매칭된 현재 위도
    val currentRoadName: String, // 현재 주행 중인 도로명
    val firstTBTInfo: TBTInfo?, // 첫 번째 턴바이턴 안내 정보
    val nGoPosDist: Int, // 목적지까지 남은 거리 (remainDistanceToDestinationInMeter와 유사)
    val nGoPosTime: Int, // 목적지까지 남은 시간 (remainTimeToDestinationInSec와 유사)
    val destinationLongitude: Double, // 목적지 경도
    val matchedLongitude: Double, // 맵 매칭된 현재 경도
    val showSDI: Boolean, // 속도 단속 정보 표시 여부
    val destinationLatitude: Double // 목적지 위도
) {
    // IntArray 타입의 필드들은 toString() 시에 [I@hashcode 로 출력되므로,
    // 필요에 따라 데이터를 추출하여 사람이 읽을 수 있는 형태로 변환하는 로직이 필요할 수 있습니다.
    // 여기서는 일단 직접 IntArray를 저장하도록 합니다.

    companion object {
        /**
         * 주어진 Bundle에서 TmapNavigationData 객체를 생성하는 팩토리 메서드.
         */
        fun fromBundle(bundle: Bundle?): TmapNavigationData? {
            if (bundle == null) {
                return null
            }

            // secondTBTInfo (HashMap으로 전달될 가능성)
            val secondTBTData = bundle.getSerializable("secondTBTInfo")
            val secondTBTInfo = if (secondTBTData is HashMap<*, *>) {
                // HashMap을 Bundle로 변환하여 fromBundle에 전달
                val tempBundle = Bundle()
                secondTBTData.forEach { (key, value) ->
                    // Bundle은 모든 타입을 직접 넣을 수 있는 것은 아니므로, 기본 타입만 처리합니다.
                    // TMAP SDK의 TBTInfo 내부 필드가 기본 타입임을 가정합니다.
                    when (key) {
                        is String -> {
                            when (value) {
                                is Int -> tempBundle.putInt(key, value)
                                is String -> tempBundle.putString(key, value)
                                is Boolean -> tempBundle.putBoolean(key, value)
                                is Double -> tempBundle.putDouble(key, value)
                                is Long -> tempBundle.putLong(key, value)
                                // 다른 타입이 있다면 추가
                                else -> { /* Handle other types if necessary or ignore */
                                }
                            }
                        }
                    }
                }
                TBTInfo.fromBundle(tempBundle)
            } else {
                // 여전히 Bundle 형태로 넘어올 수도 있으므로 getBundle 시도
                TBTInfo.fromBundle(bundle.getBundle("secondTBTInfo"))
            }


            // firstSDIInfo (HashMap으로 전달될 가능성)
            val firstSDIData = bundle.getSerializable("firstSDIInfo")
            val firstSDIInfo = if (firstSDIData is HashMap<*, *>) {
                val tempBundle = Bundle()
                firstSDIData.forEach { (key, value) ->
                    when (key) {
                        is String -> {
                            when (value) {
                                is Int -> tempBundle.putInt(key, value)
                                is String -> tempBundle.putString(key, value)
                                is Boolean -> tempBundle.putBoolean(key, value)
                                is Double -> tempBundle.putDouble(key, value)
                                is Long -> tempBundle.putLong(key, value)
                                else -> { /* Handle other types if necessary or ignore */
                                }
                            }
                        }
                    }
                }
                SDIInfo.fromBundle(tempBundle)
            } else {
                SDIInfo.fromBundle(bundle.getBundle("firstSDIInfo"))
            }

            // firstTBTInfo (HashMap으로 전달될 가능성)
            val firstTBTData = bundle.getSerializable("firstTBTInfo")
            val firstTBTInfo = if (firstTBTData is HashMap<*, *>) {
                val tempBundle = Bundle()
                firstTBTData.forEach { (key, value) ->
                    when (key) {
                        is String -> {
                            when (value) {
                                is Int -> tempBundle.putInt(key, value)
                                is String -> tempBundle.putString(key, value)
                                is Boolean -> tempBundle.putBoolean(key, value)
                                is Double -> tempBundle.putDouble(key, value)
                                is Long -> tempBundle.putLong(key, value)
                                else -> { /* Handle other types if necessary or ignore */
                                }
                            }
                        }
                    }
                }
                TBTInfo.fromBundle(tempBundle)
            } else {
                TBTInfo.fromBundle(bundle.getBundle("firstTBTInfo"))
            }


            return TmapNavigationData(
                destinationName = bundle.getString("destinationName", "") ?: "",
                limitSpeed = bundle.getInt("limitSpeed", 0),
                isNightMode = bundle.getBoolean("isNightMode", false),
                remainTimeToDestinationInSec = bundle.getInt("remainTimeToDestinationInSec", 0),
                noLocationSignal = bundle.getBoolean("noLocationSignal", false),
                remainViaPointSize = bundle.getInt("remainViaPointSize", 0),
                gpsState = bundle.getInt("gpsState", 0),
                secondTBTInfo = secondTBTInfo,
                currentCourseAngle = bundle.getInt("currentCourseAngle", 0),
                isShadeArea = bundle.getBoolean("isShadeArea", false),
                hipassLaneCount = bundle.getInt("hipassLaneCount", 0),
                laneAvailableInfo = bundle.getIntArray("laneAvailableInfo"),
                showLane = bundle.getBoolean("showLane", false),
                isCaution = bundle.getBoolean("isCaution", false),
                remainDistanceToDestinationInMeter = bundle.getInt(
                    "remainDistanceToDestinationInMeter",
                    0
                ),
                laneEtcInfo = bundle.getIntArray("laneEtcInfo"),
                hasTbtInfo = bundle.getBoolean("hasTbtInfo", false),
                laneTurnInfo = bundle.getIntArray("laneTurnInfo"),
                speedInKmPerHour = bundle.getInt("speedInKmPerHour", 0),
                laneDistance = bundle.getInt("laneDistance", 0),
                firstSDIInfo = firstSDIInfo,
                averageSpeed = bundle.getInt("averageSpeed", 0),
                currentRoutePlan = bundle.getInt("currentRoutePlan", 0),
                laneCount = bundle.getInt("laneCount", 0),
                matchedLatitude = bundle.getDouble("matchedLatitude", 0.0),
                currentRoadName = bundle.getString("currentRoadName", "") ?: "",
                firstTBTInfo = firstTBTInfo,
                nGoPosDist = bundle.getInt("nGoPosDist", 0),
                nGoPosTime = bundle.getInt("nGoPosTime", 0),
                destinationLongitude = bundle.getDouble("destinationLongitude", 0.0),
                matchedLongitude = bundle.getDouble("matchedLongitude", 0.0),
                showSDI = bundle.getBoolean("showSDI", false),
                destinationLatitude = bundle.getDouble("destinationLatitude", 0.0)
            )
        }
    }
}