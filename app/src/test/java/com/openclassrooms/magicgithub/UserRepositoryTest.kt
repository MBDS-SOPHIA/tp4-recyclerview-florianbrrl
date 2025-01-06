package com.openclassrooms.magicgithub

import com.openclassrooms.magicgithub.api.FakeApiServiceGenerator.FAKE_USERS
import com.openclassrooms.magicgithub.api.FakeApiServiceGenerator.FAKE_USERS_RANDOM
import com.openclassrooms.magicgithub.di.Injection
import com.openclassrooms.magicgithub.model.User
import com.openclassrooms.magicgithub.repository.UserRepository
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserRepositoryTest {
    private lateinit var userRepository: UserRepository

    @Before
    fun setup() {
        userRepository = Injection.getRepository()
    }

    @Test
    fun getUsersWithSuccess() {
        val usersActual = userRepository.getUsers()
        val usersExpected: List<User> = FAKE_USERS
        assertEquals(usersActual, usersExpected)
    }

    @Test
    fun generateRandomUserWithSuccess() {
        val initialSize = userRepository.getUsers().size
        userRepository.addRandomUser()
        val user = userRepository.getUsers().last()
        assertEquals(userRepository.getUsers().size, initialSize + 1)
        assertTrue(FAKE_USERS_RANDOM.any { it == user })
    }

    @Test
    fun deleteUserWithSuccess() {
        val userToDelete = userRepository.getUsers()[0]
        userRepository.deleteUser(userToDelete)
        assertFalse(userRepository.getUsers().contains(userToDelete))
    }

    // Nouveaux tests pour la fonctionnalité d'activation/désactivation

    @Test
    fun userShouldBeActiveByDefault() {
        val user = userRepository.getUsers()[0]
        assertTrue(user.isActive)
    }

    @Test
    fun toggleUserActiveStateShouldChangeState() {
        val user = userRepository.getUsers()[0]
        val initialState = user.isActive

        user.isActive = !user.isActive

        assertNotEquals(initialState, user.isActive)
    }

    @Test
    fun addedRandomUserShouldBeActiveByDefault() {
        userRepository.addRandomUser()
        val newUser = userRepository.getUsers().last()
        assertTrue(newUser.isActive)
    }
}