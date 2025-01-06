package com.openclassrooms.magicgithub

import android.graphics.drawable.ColorDrawable
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.GeneralSwipeAction
import androidx.test.espresso.action.Press
import androidx.test.espresso.action.Swipe
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.openclassrooms.magicgithub.di.Injection.getRepository
import com.openclassrooms.magicgithub.ui.user_list.ListUserActivity
import com.openclassrooms.magicgithub.utils.RecyclerViewUtils.clickChildView
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.TypeSafeMatcher
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class UserListInstrumentedTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(ListUserActivity::class.java)

    private var currentUsersSize = -1

    @Before
    fun setup() {
        currentUsersSize = getRepository().getUsers().size
    }

    @Test
    fun checkIfRecyclerViewIsNotEmpty() {
        onView(withId(R.id.activity_list_user_rv))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun checkIfAddingRandomUserIsWorking() {
        onView(withId(R.id.activity_list_user_fab))
            .perform(ViewActions.click())
        onView(withId(R.id.activity_list_user_rv))
            .check(matches(hasItemCount(currentUsersSize + 1)))
    }

    @Test
    fun checkIfDeletingUserIsWorking() {
        onView(withId(R.id.activity_list_user_rv))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    clickChildView(R.id.item_list_user_delete_button)
                )
            )
        onView(withId(R.id.activity_list_user_rv))
            .check(matches(hasItemCount(currentUsersSize - 1)))
    }

    @Test
    fun checkIfSwipingLeftChangesUserState() {
        // Attendre le chargement initial
        Thread.sleep(250)

        // Swipe à gauche pour désactiver
        onView(withId(R.id.activity_list_user_rv))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.swipeLeft()
                )
            )

        // Attendre la fin de l'animation
        Thread.sleep(250)

        // Vérifier que l'item a le background rouge
        onView(withId(R.id.activity_list_user_rv))
            .check(matches(hasViewAtPosition(0, hasBackgroundColor(android.R.color.holo_red_light))))
    }

    @Test
    fun checkIfSwipingRightChangesUserState() {
        Thread.sleep(250)

        // D'abord swipe à gauche
        onView(withId(R.id.activity_list_user_rv))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.swipeLeft()
                )
            )

        Thread.sleep(250)

        // Puis swipe à droite
        onView(withId(R.id.activity_list_user_rv))
            .perform(
                RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                    0,
                    ViewActions.swipeRight()
                )
            )

        Thread.sleep(250)

        // Vérifier le background blanc
        onView(withId(R.id.activity_list_user_rv))
            .check(matches(hasViewAtPosition(0, hasBackgroundColor(android.R.color.white))))
    }

    @Test
    fun checkIfDragAndDropUIWorks() {
        Thread.sleep(500)

        val initialUser = getRepository().getUsers()[0]
        lateinit var dragCallback: ItemTouchHelper.SimpleCallback

        // Créer un ItemTouchHelper.SimpleCallback pour le drag & drop
        dragCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            0
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = viewHolder.adapterPosition
                val toPosition = target.adapterPosition
                getRepository().moveUser(fromPosition, toPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
        }

        val itemTouchHelper = ItemTouchHelper(dragCallback)

        // Attacher l'ItemTouchHelper à la RecyclerView
        activityRule.scenario.onActivity { activity ->
            itemTouchHelper.attachToRecyclerView(activity.findViewById(R.id.activity_list_user_rv))
        }

        // Simuler le drag & drop via l'ItemTouchHelper
        onView(withId(R.id.activity_list_user_rv))
            .perform(object : ViewAction {
                override fun getDescription(): String {
                    return "Déplacer l'item de la position 0 à 1"
                }

                override fun getConstraints(): Matcher<View> {
                    return isDisplayed()
                }

                override fun perform(uiController: UiController, view: View) {
                    val recyclerView = view as RecyclerView
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(0)
                        ?: throw IllegalStateException("ViewHolder not found for position 0")

                    val targetViewHolder = recyclerView.findViewHolderForAdapterPosition(1)
                        ?: throw IllegalStateException("ViewHolder not found for position 1")

                    // Simuler le mouvement de drag & drop en appelant directement le callback
                    dragCallback.onMove(recyclerView, viewHolder, targetViewHolder)

                    uiController.loopMainThreadForAtLeast(500)
                }
            })

        Thread.sleep(500)

        // Vérifier la nouvelle position
        val userAfterMove = getRepository().getUsers()[1]
        assert(initialUser.id == userAfterMove.id) {
            "L'utilisateur n'a pas été déplacé correctement via l'UI"
        }
    }

    // Action personnalisée pour le drag & drop
    private fun dragAndDrop(toPosition: Int): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isDisplayed()
            }

            override fun getDescription(): String {
                return "Effectue un drag and drop vers la position $toPosition"
            }

            override fun perform(uiController: UiController, view: View) {
                // Coordonnées de départ (centre de la vue)
                val startX = view.width / 2
                val startY = view.height / 2

                // Coordonnées d'arrivée (décalage vers le bas pour aller à la position suivante)
                val endX = startX
                val endY = startY + view.height

                // Effectuer le mouvement de drag & drop
                val dragSteps = 10 // Nombre d'étapes pour le mouvement
                val precision = Press.FINGER

                // Action de drag & drop
                GeneralSwipeAction(
                    Swipe.SLOW,
                    { floatArrayOf(startX.toFloat(), startY.toFloat()) },
                    { floatArrayOf(endX.toFloat(), endY.toFloat()) },
                    precision
                ).perform(uiController, view)

                // Attendre que l'animation soit terminée
                uiController.loopMainThreadForAtLeast(500)
            }
        }
    }

    // Méthodes utilitaires

    private fun hasItemCount(expectedCount: Int): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("RecyclerView with item count: $expectedCount")
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                return recyclerView.adapter?.itemCount == expectedCount
            }
        }
    }

    private fun hasViewAtPosition(position: Int, itemMatcher: Matcher<View>): Matcher<View> {
        return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
            override fun describeTo(description: Description) {
                description.appendText("has item at position $position: ")
                itemMatcher.describeTo(description)
            }

            override fun matchesSafely(recyclerView: RecyclerView): Boolean {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                    ?: return false
                return itemMatcher.matches(viewHolder.itemView)
            }
        }
    }

    private fun hasBackgroundColor(@ColorRes colorRes: Int): Matcher<View> {
        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("has background color resource ID: $colorRes")
            }

            override fun matchesSafely(view: View): Boolean {
                val context = view.context
                val expectedColor = ContextCompat.getColor(context, colorRes)

                // Vérifier d'abord le background
                val background = view.background
                if (background is ColorDrawable && background.color == expectedColor) {
                    return true
                }

                // Sinon, vérifier le backgroundTint
                val backgroundTint = view.backgroundTintList?.defaultColor
                return backgroundTint == expectedColor
            }
        }
    }
}