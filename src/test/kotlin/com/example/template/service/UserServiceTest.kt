package com.example.template.service

import com.example.template.client.GithubClient
import com.example.template.client.GithubUser
import com.example.template.exception.ResourceNotFoundException
import com.example.template.model.User
import com.example.template.repository.UserRepository
import com.example.template.util.DatetimeProvider
import com.example.template.util.UuidProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class UserServiceTest {
    private val uuidProvider: UuidProvider = mockk()
    private val dateTimeProvider: DatetimeProvider = mockk()
    private val userRepository: UserRepository = mockk()
    private val githubClient: GithubClient = mockk()

    private val userService = UserService(uuidProvider, dateTimeProvider, userRepository, githubClient)

    @BeforeEach
    fun beforeEach() {
        every { uuidProvider.randomUuid() } returns userId
        every { dateTimeProvider.currentTime() } returns currentTime
    }

    @AfterEach
    fun afterEach() {
        confirmVerified(userRepository, githubClient)
    }

    @Nested
    inner class CreateUserTest {

        @Test
        fun `it should create new user`() {
            every { userRepository.findUserByGithubUsername(any()) } returns null
            every { githubClient.findGithubUser(any()) } returns githubUser
            every { userRepository.insertUser(any()) } returns 1

            val actualUser = userService.createUser(githubUsername)

            actualUser shouldBe user
            verify {
                userRepository.findUserByGithubUsername(githubUsername)
                githubClient.findGithubUser(githubUsername)
                userRepository.insertUser(user)
            }
        }

        @Test
        fun `it should return existing user if the user already exists`() {
            every { userRepository.findUserByGithubUsername(any()) } returns user

            val actualUser = userService.createUser(githubUsername)

            actualUser shouldBe user
            verify { userRepository.findUserByGithubUsername(githubUsername) }
            verify(exactly = 0) { userRepository.insertUser(any()) }
        }

        @Test
        fun `it should raise IllegalArgumentException if user is not found on Github`() {
            every { userRepository.findUserByGithubUsername(any()) } returns null
            every { githubClient.findGithubUser(any()) } returns null

            shouldThrow<IllegalArgumentException> {
                userService.createUser(githubUsername)
            }

            verify {
                userRepository.findUserByGithubUsername(githubUsername)
                githubClient.findGithubUser(githubUsername)
            }
            verify(exactly = 0) { userRepository.insertUser(any()) }
        }
    }

    @Nested
    inner class GetUserTest {

        @Test
        fun `it should return user`() {
            every { userRepository.findUserByGithubUsername(any()) } returns user

            val actualUser = userService.getUser(githubUsername)

            actualUser shouldBe user
            verify { userRepository.findUserByGithubUsername(githubUsername) }
        }

        @Test
        fun `it should raise ResourceNotFoundException if user is not found`() {
            every { userRepository.findUserByGithubUsername(any()) } returns null

            assertThrows<ResourceNotFoundException> {
                userService.getUser(githubUsername)
            }

            verify { userRepository.findUserByGithubUsername(githubUsername) }
        }
    }

    @Nested
    inner class DeleteUserTest {
        @Test
        fun `it should delete user`() {
            every { userRepository.deleteUserByGithubUsername(any()) } returns 1

            assertDoesNotThrow { userService.deleteUser(githubUsername) }

            verify { userRepository.deleteUserByGithubUsername(githubUsername) }
        }
    }

    companion object {
        private const val githubUsername = "testuser"
        private val userId = UUID.fromString("95408c28-fcd6-427d-8f52-be64e5186b9a")
        private val currentTime = LocalDateTime.parse("2022-03-01T13:00:00Z", DateTimeFormatter.ISO_DATE_TIME)
        private val githubUser = GithubUser(
            login = githubUsername,
            name = "Test User",
            company = "Github",
            location = "Berlin, Germany",
            bio = "Sotware Engineer",
            hireable = true
        )
        private val user = User(
            id = userId,
            githubUsername = githubUsername,
            name = githubUser.name,
            company = githubUser.company,
            location = githubUser.location,
            bio = githubUser.bio,
            isAvailableForHiring = githubUser.hireable,
            createdAt = currentTime,
            updatedAt = currentTime
        )
    }
}
