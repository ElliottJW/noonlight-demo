package com.noonlight.apps.ui.state.alarm

data class AlarmScreenState(val status: Status) {

    enum class Status {
        NEW, LOADING, ARMED
    }
}
