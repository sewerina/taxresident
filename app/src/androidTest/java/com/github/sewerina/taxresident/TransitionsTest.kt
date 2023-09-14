package com.github.sewerina.taxresident

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.room.Room
import com.github.sewerina.taxresident.data.AppDatabase
import com.github.sewerina.taxresident.data.RecordDao
import com.github.sewerina.taxresident.data.SuggestionDao
import com.github.sewerina.taxresident.ui.SettingsScreenCallbacks
import com.github.sewerina.taxresident.ui.TaxResidentApp
import com.github.sewerina.taxresident.ui.theme.TaxresidentTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class TransitionsTest {
    lateinit var mainViewModel: MainViewModel
    lateinit var recordDao: RecordDao
    lateinit var suggestionDao: SuggestionDao

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setup() {
        val roomDb =
            Room.inMemoryDatabaseBuilder(composeTestRule.activity, AppDatabase::class.java).build()
        recordDao = roomDb.recordDao()
        suggestionDao = roomDb.suggestionDao()
        mainViewModel = MainViewModel(recordDao, suggestionDao)
        composeTestRule.setContent {
            TaxresidentTheme(darkTheme = true, dynamicColor = false) {
                TaxResidentApp(
                    mainViewModel,
                    true,
                    settingsScreenCallbacks = SettingsScreenCallbacks({}, {}, { "User" }, {})
                )
            }
        }
    }

    @Test
    fun transitionToSearchScreen() {
        composeTestRule.waitUntilAtLeastOneExists(hasContentDescription(HomeScreenTest.labelSearchIcon))
        composeTestRule.onNodeWithContentDescription(HomeScreenTest.labelSearchIcon).performClick()

        composeTestRule.onNode(hasText(composeTestRule.activity.getString(R.string.placeholder_search)))
            .assertIsDisplayed()
    }

    @Test
    fun transitionToSettingsScreen() {
        composeTestRule.waitUntilAtLeastOneExists(hasContentDescription(HomeScreenTest.labelMoreIcon))
        composeTestRule.onNodeWithContentDescription(HomeScreenTest.labelMoreIcon).performClick()
        composeTestRule.onNode(hasText(composeTestRule.activity.getString(R.string.menuItem_settings)))
            .performClick()

        composeTestRule.onNodeWithTag("переключатель темы").assertIsDisplayed().assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun transitionToAboutScreen() {
        composeTestRule.waitUntilAtLeastOneExists(hasContentDescription(HomeScreenTest.labelMoreIcon))
        composeTestRule.onNodeWithContentDescription(HomeScreenTest.labelMoreIcon).performClick()
        composeTestRule.onNode(hasText(composeTestRule.activity.getString(R.string.menuItem_about)))
            .performClick()

        composeTestRule.onNodeWithTag("WebView").assertIsDisplayed()
    }

    @Test
    fun transitionToRecordScreen() {
        composeTestRule.waitUntilAtLeastOneExists(hasContentDescription(HomeScreenTest.labelAddIcon), 1500)
        composeTestRule.onNodeWithContentDescription(HomeScreenTest.labelAddIcon).performClick()

        composeTestRule.onNode(hasText(composeTestRule.activity.getString(R.string.title_record)))
            .assertIsDisplayed()
    }
}