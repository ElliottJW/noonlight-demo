package com.noonlight.apps.ui.component.alarm

import android.content.Context
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import com.noonlight.apps.R
import com.noonlight.apps.ui.state.alarm.AlarmScreenState
import com.noonlight.apps.ui.theme.NoonlightDemoTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AlarmScreenTest {

    @get:Rule
    val rule = createComposeRule()

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
    }

    @Test
    fun showCreateAlarm_when_stateIsNew() {
        val state = AlarmScreenState(
            status = AlarmScreenState.Status.NEW
        )
        rule.setContent {
            NoonlightDemoTheme {
                AlarmScreen(
                    state = state,
                    onCreateAlarmClicked = { /* non-op */ },
                    onCancelAlarmClicked = { /* non-op */ })
            }
        }

        rule.onNodeWithText(text = context.getString(R.string.create_alarm)).assertExists()
        rule.onNodeWithText(text = context.getString(R.string.cancel_alarm)).assertDoesNotExist()
        rule.onNodeWithTag(testTag = AlarmLoadingIndicatorConstants.PROGRESS_TAG).assertDoesNotExist()
    }

    @Test
    fun showLoadingIndicator_when_stateIsLoading() {
        val state = AlarmScreenState(
            status = AlarmScreenState.Status.LOADING
        )
        rule.setContent {
            NoonlightDemoTheme {
                AlarmScreen(
                    state = state,
                    onCreateAlarmClicked = { /* non-op */ },
                    onCancelAlarmClicked = { /* non-op */ })
            }
        }

        rule.onNodeWithText(text = context.getString(R.string.create_alarm)).assertDoesNotExist()
        rule.onNodeWithText(text = context.getString(R.string.cancel_alarm)).assertDoesNotExist()
        rule.onNodeWithTag(testTag = AlarmLoadingIndicatorConstants.PROGRESS_TAG).assertExists()
    }

    @Test
    fun showCancelAlarm_when_stateIsArmed() {
        val state = AlarmScreenState(
            status = AlarmScreenState.Status.ARMED
        )
        rule.setContent {
            NoonlightDemoTheme {
                AlarmScreen(
                    state = state,
                    onCreateAlarmClicked = { /* non-op */ },
                    onCancelAlarmClicked = { /* non-op */ })
            }
        }

        rule.onNodeWithText(text = context.getString(R.string.create_alarm)).assertDoesNotExist()
        rule.onNodeWithText(text = context.getString(R.string.cancel_alarm)).assertExists()
        rule.onNodeWithTag(testTag = AlarmLoadingIndicatorConstants.PROGRESS_TAG).assertDoesNotExist()
    }
}