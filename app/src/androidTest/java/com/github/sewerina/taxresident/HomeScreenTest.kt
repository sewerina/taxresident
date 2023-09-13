package com.github.sewerina.taxresident

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isPopup
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.github.sewerina.taxresident.data.RecordEntity
import com.github.sewerina.taxresident.ui.HomeScreen
import com.github.sewerina.taxresident.ui.HomeScreenCallbacks
import com.github.sewerina.taxresident.ui.theme.TaxresidentTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    companion object {
        const val labelSearchIcon = "search icon"
        const val labelMoreIcon = "more icon"
        const val labelAddIcon = "add icon"
        const val labelEmptyListIcon = "empty list icon"
    }

    @Test
    fun homeTopAppBarAndFab() {
        val state = mutableStateOf(MainViewState(emptyList(), false))
        composeTestRule.setContent {
            TaxresidentTheme {
                HomeScreen(state = state, callbacks = HomeScreenCallbacks({}, {}, {}, {}, {}, {}))
            }
        }
        composeTestRule
            .onNode(hasText("Tax Resident")).assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription(labelSearchIcon).assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithContentDescription(labelMoreIcon).assertIsDisplayed()
            .assertHasClickAction()

        composeTestRule.onNodeWithContentDescription(labelAddIcon).assertIsDisplayed()
            .assertHasClickAction()
    }

    @Test
    fun emptyRecordListTest() {
        val state = mutableStateOf(MainViewState(emptyList(), false))
        composeTestRule.setContent {
            TaxresidentTheme {
                HomeScreen(state = state, callbacks = HomeScreenCallbacks({}, {}, {}, {}, {}, {}))
            }
        }

        composeTestRule.onNodeWithTag("used days").assertIsDisplayed().assert(hasText("0"))
        composeTestRule.onNodeWithContentDescription(labelEmptyListIcon).assertIsDisplayed()
        composeTestRule.onNode(hasText("Пока не создано ни одной записи")).assertIsDisplayed()
    }

    @Test
    fun recordListTest() {
        val state = mutableStateOf(
            MainViewState(
                listOf(
                    RecordEntity(0, null, null, "jan"),
                    RecordEntity(1, null, null, "mar"),
                    RecordEntity(2, null, null, "jun")
                ),
                false
            )
        )
        composeTestRule.setContent {
            TaxresidentTheme {
                HomeScreen(state = state, callbacks = HomeScreenCallbacks({}, {}, {}, {}, {}, {}))
            }
        }

        composeTestRule.onNodeWithTag("used days").assertIsDisplayed().assert(hasText("0"))
        composeTestRule.onNodeWithContentDescription(labelEmptyListIcon).assertDoesNotExist()
        composeTestRule.onNode(hasText("Пока не создано ни одной записи")).assertDoesNotExist()
    }

    @Test
    fun showMenuFromTopAppBar() {
        val state = mutableStateOf(MainViewState(emptyList(), false))
        composeTestRule.setContent {
            TaxresidentTheme {
                HomeScreen(state = state, callbacks = HomeScreenCallbacks({}, {}, {}, {}, {}, {}))
            }
        }
        composeTestRule.onNodeWithContentDescription(labelMoreIcon).performClick()
        composeTestRule.onNode(hasTestTag("home popup menu") and isPopup())
    }
}