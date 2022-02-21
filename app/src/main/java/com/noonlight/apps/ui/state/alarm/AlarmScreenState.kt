package com.noonlight.apps.ui.state.alarm

data class AlarmScreenState(
    val screenStatus: Status,
    val currentAlarmId: String? = null,
) {

    enum class Status {
        CREATING, ARMING, ARMED, DISARMING
    }
}
