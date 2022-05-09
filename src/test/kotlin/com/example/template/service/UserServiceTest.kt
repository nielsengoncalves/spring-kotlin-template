package com.example.template.service

import com.example.template.client.GithubClient
import com.example.template.client.GithubUser
import com.example.template.exception.ResourceNotFoundException
import com.example.template.model.User
import com.example.template.repository.UserRepository
import com.example.template.util.DatetimeProvider
import com.example.template.util.UuidProvider
import feign.FeignException.NotFound
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
            every { userRepository.findByGithubUsername(any()) } returns null
            every { githubClient.findGithubUser(any()) } returns githubUser
            every { userRepository.insert(any()) } returns 1

            val actualUser = userService.createUser(githubUsername)

            actualUser shouldBe user
            verify {
                userRepository.findByGithubUsername(githubUsername)
                githubClient.findGithubUser(githubUsername)
                userRepository.insert(user)
            }
        }

        @Test
        fun `it should return existing user if the user already exists`() {
            every { userRepository.findByGithubUsername(any()) } returns user

            val actualUser = userService.createUser(githubUsername)

            actualUser shouldBe user
            verify { userRepository.findByGithubUsername(githubUsername) }
            verify(exactly = 0) { userRepository.insert(any()) }
        }

        @Test
        fun `it should raise NotFound when user is not found on Github`() {
            val notFoundException: NotFound = mockk()
            every { userRepository.findByGithubUsername(any()) } returns null
            every { githubClient.findGithubUser(any()) } throws notFoundException

            shouldThrow<NotFound> {
                userService.createUser(githubUsername)
            }

            verify {
                userRepository.findByGithubUsername(githubUsername)
                githubClient.findGithubUser(githubUsername)
            }
            verify(exactly = 0) { userRepository.insert(any()) }
        }
    }

    @Nested
    inner class GetUserTest {

        @Test
        fun `it should return user`() {
            every { userRepository.findByGithubUsername(any()) } returns user

            val actualUser = userService.getUser(githubUsername)

            actualUser shouldBe user
            verify { userRepository.findByGithubUsername(githubUsername) }
        }

        @Test
        fun `it should raise ResourceNotFoundException if user is not found`() {
            every { userRepository.findByGithubUsername(any()) } returns null

            assertThrows<ResourceNotFoundException> {
                userService.getUser(githubUsername)
            }

            verify { userRepository.findByGithubUsername(githubUsername) }
        }
    }

    @Nested
    inner class DeleteUserTest {
        @Test
        fun `it should delete user`() {
            every { userRepository.deleteByGithubUsername(any()) } returns 1

            assertDoesNotThrow { userService.deleteUser(githubUsername) }

            verify { userRepository.deleteByGithubUsername(githubUsername) }
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
