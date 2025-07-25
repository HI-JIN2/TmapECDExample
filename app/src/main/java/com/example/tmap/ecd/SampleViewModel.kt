package com.example.tmap.ecd

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmap.ecd.BuildConfig.API_KEY
import com.example.tmap.ecd.BuildConfig.CLIENT_ID
import com.example.tmap.ecd.BuildConfig.USER_KEY
import com.tmapmobility.tmap.tmapsdk.edc.EDCConst
import com.tmapmobility.tmap.tmapsdk.edc.TmapEDCSDK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private const val TAG = "EDC_TEST_KT"

class SampleViewModel : ViewModel() {

    private val edc: TmapEDCSDK.Companion = TmapEDCSDK.Companion
    private lateinit var edcAuthData: TmapEDCSDK.Companion.EDCAuthData

    private val _clickedText = MutableStateFlow("None")
    val clickedText: StateFlow<String> = _clickedText

    private val _callbackText = MutableStateFlow("")
    val callbackText: StateFlow<String> = _callbackText

    private val _contentsText = MutableStateFlow("")
    val contentsText: StateFlow<String> = _contentsText

    private val _isTargetTest = MutableStateFlow(false)  //FALSE 면 모의주행 못함
    val isTargetTest: StateFlow<Boolean> = _isTargetTest

    private var isRegistered = false

    private val workListener = object : TmapEDCSDK.Companion.EDCWorkListener {
        override fun onAuthAction(bundle: Bundle) {
            val authStatus = bundle.getInt("authStatus")
            val log = "인증상태 $authStatus"
            Log.e(TAG, log)
            viewModelScope.launch {
                _callbackText.value = log
            }
        }

        override fun onRouteStarted(bundle: Bundle) {
            val totalDistanceInMeter = bundle.getInt("totalDistanceInMeter")
            val totalTimeInSec = bundle.getInt("totalTimeInSec")
            val tollFree = bundle.getInt("tollFee")
            val log = "거리 ${totalDistanceInMeter}m / 시간 ${totalTimeInSec}초 / 요금 ${tollFree}원"
            Log.e(TAG, log)
            viewModelScope.launch {
                _callbackText.value = log
            }
        }

        override fun onRouteFinished(bundle: Bundle) {
            val driveDistanceInMeter = bundle.getInt("driveDistanceInMeter")
            val driveTimeInSec = bundle.getInt("driveTimeInSec")
            val log = "주행 거리 ${driveDistanceInMeter}m / 주행 시간 ${driveTimeInSec}초"
            Log.e(TAG, log)
            viewModelScope.launch {
                _callbackText.value = log
            }
        }

        override fun onInitSuccessEDC() {
            Log.e(TAG, "onInitSuccessEDC")
            viewModelScope.launch {
                _callbackText.value = "onInitSuccessEDC"
            }
        }

        override fun onFinishedEDC() {
            Log.e(TAG, "onFinishedEDC")
            viewModelScope.launch {
                _callbackText.value = "onFinishedEDC"
            }
        }

        override fun onHostAppStarted() {
            Log.e(TAG, "onHostAppStarted")
            viewModelScope.launch {
                _callbackText.value = "onHostAppStarted"
            }
        }

        override fun onResult(commandState: EDCConst.CommandState, o: Any) {
            Log.e(
                TAG,
                "onResult / ${commandState.value} / ${commandState.toString()} / ${o.toString()}"
            )
            viewModelScope.launch {
                _contentsText.value = "" // Clear previous contents

                if (commandState == EDCConst.CommandState.COMMAND_GET_INFO) {
                    val data = o as? Bundle
                    _contentsText.value = data?.toString() ?: "No data"
                } else {
                    val callbackTextBuilder = StringBuilder()
                    when (commandState) {
                        EDCConst.CommandState.COMMAND_TMAP_VERSION -> callbackTextBuilder.append("COMMAND_TMAP_VERSION : ")
                            .append(o.toString())

                        EDCConst.CommandState.COMMAND_REG_CALLBACK -> callbackTextBuilder.append("COMMAND_REG_CALLBACK : ")
                            .append(o.toString())

                        EDCConst.CommandState.COMMAND_UNREG_CALLBACK -> callbackTextBuilder.append("COMMAND_UNREG_CALLBACK : ")
                            .append(o.toString())

                        EDCConst.CommandState.COMMAND_IS_RUNNING -> callbackTextBuilder.append("COMMAND_IS_RUNNING : ")
                            .append(o.toString())

                        EDCConst.CommandState.COMMAND_USING_BLACKBOX -> callbackTextBuilder.append("COMMAND_USING_BLACKBOX : ")
                            .append(o.toString())

                        EDCConst.CommandState.COMMAND_DRIVEMODE -> callbackTextBuilder.append("COMMAND_DRIVEMODE : ")
                            .append(o.toString())

                        EDCConst.CommandState.COMMAND_IS_ROUTE -> callbackTextBuilder.append("COMMAND_IS_ROUTE : ")
                            .append(o.toString())

                        EDCConst.CommandState.COMMAND_ADDRESS -> callbackTextBuilder.append("COMMAND_ADDRESS : ")
                            .append(o.toString())

                        EDCConst.CommandState.COMMAND_SET_STATUS -> callbackTextBuilder.append("COMMAND_SET_STATUS : ")
                            .append(o.toString())
                        // Add onAuthAction, onRouteStarted, onRouteFinished cases if you want to display their results in _contentsText,
                        // otherwise they are handled by their specific callbacks
                        else -> callbackTextBuilder.append("Unhandled command: ")
                            .append(commandState.toString()).append(" Value: ").append(o.toString())
                    }
                    _callbackText.value = callbackTextBuilder.toString()
                }
            }
        }

        override fun onFail(commandState: EDCConst.CommandState, i: Int, s: String?) {
            val log = "onFail / ${commandState.value} ${commandState.name} / $i / $s"
            Log.e(TAG, log)
            viewModelScope.launch {
                _callbackText.value = "FAIL: $log"
            }
        }
    }

    private val tmapDataListener = object : TmapEDCSDK.Companion.TMAPDataListener {
        override fun onReceive(bundle: Bundle?) {
            viewModelScope.launch {
                if (bundle != null) {
                    // Bundle에서 TmapNavigationData 객체 생성
                    val navData = TmapNavigationData.fromBundle(bundle)

                    if (navData != null) {

                        val navigationInfo = StringBuilder("네비게이션 정보:\n")
                        navigationInfo.append("  목적지: ${navData.destinationName}\n")
                        navigationInfo.append("  제한 속도: ${navData.limitSpeed} km/h\n")
                        navigationInfo.append("  남은 시간: ${navData.remainTimeToDestinationInSec} 초\n")
                        navigationInfo.append("  남은 거리: ${navData.remainDistanceToDestinationInMeter} m\n")
                        navigationInfo.append("  현재 속도: ${navData.speedInKmPerHour} km/h\n")
                        navigationInfo.append("  현재 도로명: ${navData.currentRoadName}\n")
                        navigationInfo.append("  현재 위치 (매칭): (${navData.matchedLatitude}, ${navData.matchedLongitude})\n")
                        navigationInfo.append("  GPS 상태: ${navData.gpsState}\n")
                        navigationInfo.append("  야간 모드: ${navData.isNightMode}\n")
                        navigationInfo.append("  주의 구간: ${navData.isCaution}\n")


                        // 첫 번째 TBT 정보 (다음 안내 정보)
                        navData.firstTBTInfo?.let { tbt ->
                            navigationInfo.append("--- 다음 안내 (First TBT) ---\n")
                            navigationInfo.append("  교차로: ${tbt.szCrossName}\n")
                            navigationInfo.append("  안내 문구: ${tbt.szTBTMainText}\n")
                            navigationInfo.append("  다음 턴까지 거리: ${tbt.nTBTDist} m\n")
                            navigationInfo.append("  회전 유형: ${tbt.nTBTTurnType}\n")
                            navigationInfo.append("  안내 도로명: ${tbt.szRoadName}\n")
                            navigationInfo.append("----------------------------\n")
                        }

                        // 두 번째 TBT 정보 (그 다음 안내 정보, 있을 경우)
                        navData.secondTBTInfo?.let { tbt ->
                            navigationInfo.append("--- 그 다음 안내 (Second TBT) ---\n")
                            navigationInfo.append("  교차로: ${tbt.szCrossName}\n")
                            navigationInfo.append("  안내 문구: ${tbt.szTBTMainText}\n")
                            navigationInfo.append("  다음 턴까지 거리: ${tbt.nTBTDist} m\n")
                            navigationInfo.append("  회전 유형: ${tbt.nTBTTurnType}\n")
                            navigationInfo.append("  안내 도로명: ${tbt.szRoadName}\n")
                            navigationInfo.append("---------------------------------\n")
                        }

                        // 첫 번째 SDI 정보 (과속 단속 정보)
                        navData.firstSDIInfo?.let { sdi ->
                            navigationInfo.append("--- 과속 단속 정보 (First SDI) ---\n")
                            navigationInfo.append("  단속 거리: ${sdi.nSdiDist} m\n")
                            navigationInfo.append("  단속 제한 속도: ${sdi.nSdiSpeedLimit} km/h\n")
                            navigationInfo.append("  단속 유형: ${sdi.nSdiType}\n")
                            navigationInfo.append("  구간 단속: ${sdi.bSdiBlockSection}\n")
                            navigationInfo.append("----------------------------------\n")
                        }

                        // IntArray 필드들은 직접 출력하면 [I@hashcode 형태로 나오므로,
                        // 필요에 따라 Arrays.toString() 등으로 변환하여 출력할 수 있습니다.
                        navData.laneAvailableInfo?.let {
                            navigationInfo.append("  차선 이용 가능 정보: ${it.contentToString()}\n")
                        }
                        navData.laneEtcInfo?.let {
                            navigationInfo.append("  차선 기타 정보: ${it.contentToString()}\n")
                        }
                        navData.laneTurnInfo?.let {
                            navigationInfo.append("  차선 회전 정보: ${it.contentToString()}\n")
                        }

                        _contentsText.value = navigationInfo.toString()
                        Log.d(TAG, "네비게이션 정보: ${navigationInfo.toString()}") // Logcat으로도 출력
                    } else {
                        _contentsText.value = "Failed to parse navigation data."
                        Log.e(TAG, "TmapNavigationData.fromBundle returned null.")
                    }
                } else {
                    _contentsText.value = "No navigation data received (bundle is null)"
                    Log.d(TAG, "Null bundle in TMAPDataListener")
                }
            }
        }
    }

    fun toggleTargetTest() {
        _isTargetTest.value = !_isTargetTest.value
        _clickedText.value = "Target: ${if (_isTargetTest.value) "SAMPLE HOST" else "TMAP"}"
    }

    // Modified initializeSdk to accept a parameter for runHostOption
    fun initializeSdk(context: Context) { // Default to false

        edcAuthData = TmapEDCSDK.Companion.EDCAuthData(
            CLIENT_ID,
            API_KEY,
            context.packageName,
            USER_KEY,
            USER_KEY
        )

        _clickedText.value = "Initialize"
        if (edc.isInitialized) {
            edc.finish()
        }
        Log.e(TAG, "call -- initialize")
//            edc.initializeWithRunHostOption(context, edcAuthData, workListener, _isTargetTest.value, true)
        edc.initialize(context, edcAuthData, workListener, _isTargetTest.value)

        _callbackText.value = "SDK Initializing..."
    }


    fun getVersion() {
        _clickedText.value = "Get Version"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_VERSION_NAME")
            edc.getVersion(EDCConst.GetVersion.TMAP_VERSION_NAME)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun toggleRegistration() {
        _clickedText.value = "Toggle Reg/Unreg Callback"
        if (edc.isInitialized) { // Only allow registration/unregistration if SDK is initialized
            if (isRegistered) {
                Log.e(TAG, "call -- unregisterDataCallback")
                edc.unregisterDataCallback()
                isRegistered = false
                _callbackText.value = "Unregistered callback"
            } else {
                Log.e(TAG, "call -- registerDataCallback")
                edc.registerDataCallback(tmapDataListener)
                isRegistered = true
                _callbackText.value = "Registered callback"
            }
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun getTmapInfo() {
        _clickedText.value = "Get Tmap Info"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- getTMAPInfo")
            edc.getTMAPInfo()
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun getRunningInfo() {
        _clickedText.value = "Get Running Info"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.GetTmapStatus.IS_TMAP_RUNNING")
            edc.getStatus(EDCConst.GetTmapStatus.IS_TMAP_RUNNING)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun getBlackBoxInfo() {
        _clickedText.value = "Get Blackbox Info"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.GetTmapStatus.IS_TMAP_USE_BLACKBOX")
            edc.getStatus(EDCConst.GetTmapStatus.IS_TMAP_USE_BLACKBOX)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun getDriveModeInfo() {
        _clickedText.value = "Get Drive Mode Info"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.GetTmapStatus.IS_TMAP_DRIVE_MODE")
            edc.getStatus(EDCConst.GetTmapStatus.IS_TMAP_DRIVE_MODE)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun getRouteInfo() {
        _clickedText.value = "Get Route Info"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.GetTmapStatus.IS_TMAP_ROUTE")
            edc.getStatus(EDCConst.GetTmapStatus.IS_TMAP_ROUTE)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun getAddressInfo(lon: Double, lat: Double) {
        _clickedText.value = "Get Address"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- getAddress")
            edc.getAddress(lon, lat)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun setDisplayForeground() {
        _clickedText.value = "Display FG"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_DISPLAY_FOREGROUND")
            edc.requestCommand(EDCConst.SetStatus.TMAP_DISPLAY_FOREGROUND)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun setDisplayBackground() {
        _clickedText.value = "Display BG"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_DISPLAY_BACKGROUND")
            edc.requestCommand(EDCConst.SetStatus.TMAP_DISPLAY_BACKGROUND)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun setAudioOn() {
        _clickedText.value = "Audio On"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_AUDIO_ON")
            edc.requestCommand(EDCConst.SetStatus.TMAP_AUDIO_ON)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun setAudioOff() {
        _clickedText.value = "Audio Off"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_AUDIO_OFF")
            edc.requestCommand(EDCConst.SetStatus.TMAP_AUDIO_OFF)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun setReroute() {
        _clickedText.value = "Reroute"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_REROUTE")
            edc.requestCommand(EDCConst.SetStatus.TMAP_REROUTE)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun setZoomIn() {
        _clickedText.value = "Zoom In"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_ZOOM_IN")
            edc.requestCommand(EDCConst.SetStatus.TMAP_ZOOM_IN)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun setZoomOut() {
        _clickedText.value = "Zoom Out"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_ZOOM_OUT")
            edc.requestCommand(EDCConst.SetStatus.TMAP_ZOOM_OUT)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun setVolumeUp() {
        _clickedText.value = "Volume Up"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_VOLUME_UP")
            edc.requestCommand(EDCConst.SetStatus.TMAP_VOLUME_UP)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun setVolumeDown() {
        _clickedText.value = "Volume Down"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_VOLUME_DOWN")
            edc.requestCommand(EDCConst.SetStatus.TMAP_VOLUME_DOWN)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun goHome() {
        _clickedText.value = "Go Home"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_GO_HOME")
            edc.requestCommand(EDCConst.SetStatus.TMAP_GO_HOME)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun goCompany() {
        _clickedText.value = "Go Company"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_GO_OFFICE")
            edc.requestCommand(EDCConst.SetStatus.TMAP_GO_OFFICE)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun goAndo() {
        _clickedText.value = "Go Ando"
        if (edc.isInitialized) {
            Log.e(TAG, "call -- EDCConst.SetStatus.TMAP_GO_ANDO")
            edc.requestCommand(EDCConst.SetStatus.TMAP_GO_ANDO)
        } else {
            _callbackText.value = "SDK not initialized. Click Initialize first."
        }
    }

    fun finishSdk() {
        if (edc.isInitialized) {
            edc.finish()
            Log.e(TAG, "SDK finished from ViewModel.")
            _callbackText.value = "SDK Finished."
        }
    }
}