package com.budi.setiawan.storyappbudisetiawan.view.main

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.budi.setiawan.storyappbudisetiawan.R
import com.budi.setiawan.storyappbudisetiawan.resource.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loadStory(){
        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_story)).perform(RecyclerViewActions.scrollToPosition<RecyclerView.ViewHolder>(10))
    }

    @Test
    fun loadCreateStory() {
        onView(withId(R.id.menu1)).perform(click())
        pressBack()
    }

    @Test
    fun loadDetailStory() {
        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_story)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                15,
                click()
            )
        )
    }

    @Test
    fun loadMapStory() {
        onView(withId(R.id.menu4)).perform(click())
        pressBack()
    }
}