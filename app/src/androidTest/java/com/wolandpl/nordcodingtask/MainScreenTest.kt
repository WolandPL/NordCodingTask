package com.wolandpl.nordcodingtask

import androidx.activity.compose.setContent
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.wolandpl.nordcodingtask.ui.compose.MainScreen
import com.wolandpl.nordcodingtask.ui.compose.SERVER_ADDRESS_TAG
import com.wolandpl.nordcodingtask.ui.compose.START_STOP_BUTTON_TAG
import com.wolandpl.nordcodingtask.ui.compose.theme.NordCodingTaskTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val permissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.INTERNET,
        android.Manifest.permission.ACCESS_NETWORK_STATE,
        android.Manifest.permission.READ_CONTACTS,
        android.Manifest.permission.READ_PHONE_STATE,
        android.Manifest.permission.READ_CALL_LOG
    )

    @Before
    fun setup() {
        composeTestRule.activity.setContent {
            NordCodingTaskTheme {
                MainScreen()
            }
        }
    }

    @Test
    fun basicElements_areDisplayed_afterStartup() {

        with(composeTestRule) {
            onNodeWithTag(START_STOP_BUTTON_TAG)
                .assertIsDisplayed()

            onNodeWithTag(SERVER_ADDRESS_TAG)
                .assertIsDisplayed()
        }
    }

    @Test
    fun stopServer_hides_serverAddress() {

        with(composeTestRule) {
            onNodeWithTag(START_STOP_BUTTON_TAG).performClick()

            onNodeWithTag(START_STOP_BUTTON_TAG).assertTextContains(
                value = "start",
                substring = true,
                ignoreCase = true
            )

            onNodeWithTag(SERVER_ADDRESS_TAG)
                .assertDoesNotExist()
        }
    }
}
