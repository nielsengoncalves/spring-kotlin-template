package com.example.template.controller

import com.example.template.client.GithubUser
import com.example.template.controller.UserController
import com.example.template.exception.ResourceNotFoundException
import com.example.template.model.User
import com.example.template.service.UserService
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID
import org.mockito.Mockito.`when` as whenever

@ExtendWith(SpringExtension::class)
@WebMvcTest(value = [UserController::class])
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var userService: UserService

    @Nested
    inner class CreateUserTest {

        @Test
        fun `it should return 201 when user is created`() {
            val githubUsername = "testuser"
            whenever(userService.createUser(githubUsername)).thenReturn(user)

            mockMvc
                .perform(
                    post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"githubUsername\": \"$githubUsername\"}")
                )
                .andExpect(status().isCreated)
                .andExpect(
                    content().json(
                        """
                        {
                            "id": "${user.id}",
                            "githubUsername": "${user.githubUsername}",
                            "name": "${user.name}",
                            "company": "${user.company}",
                            "location": "${user.location}",
                            "bio": "${user.bio}",
                            "isAvailableForHiring": ${user.isAvailableForHiring}
                        }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `it should return 400 when provided user doesn't exist on github`() {
            val githubUsername = "testuser"
            whenever(userService.createUser(githubUsername)).thenThrow(IllegalArgumentException())

            mockMvc
                .perform(
                    post("/api/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"githubUsername\": \"$githubUsername\"}")
                )
                .andExpect(status().isBadRequest)
        }
    }

    @Nested
    inner class GetUserTest {

        @Test
        fun `it should return 200 when user is found`() {
            val githubUsername = "testuser"
            whenever(userService.getUser(githubUsername)).thenReturn(user)

            mockMvc
                .perform(get("/api/user/$githubUsername"))
                .andExpect(status().isOk)
                .andExpect(
                    content().json(
                        """
                        {
                            "id": "${user.id}",
                            "githubUsername": "${user.githubUsername}",
                            "name": "${user.name}",
                            "company": "${user.company}",
                            "location": "${user.location}",
                            "bio": "${user.bio}",
                            "isAvailableForHiring": ${user.isAvailableForHiring}
                        }
                        """.trimIndent()
                    )
                )
        }

        @Test
        fun `it should return 404 when user is not found`() {
            val githubUsername = "testuser"
            whenever(userService.getUser(githubUsername)).thenThrow(ResourceNotFoundException("User not found"))

            mockMvc
                .perform(get("/api/user/$githubUsername"))
                .andExpect(status().isNotFound)
        }
    }

    @Nested
    inner class DeleteUserTest {

        @Test
        fun `it should return 204 when user is deleted`() {
            val githubUsername = "testuser"

            mockMvc
                .perform(delete("/api/user/$githubUsername"))
                .andExpect(status().isNoContent)
        }
    }

    companion object {
        private val userId = UUID.fromString("95408c28-fcd6-427d-8f52-be64e5186b9a")
        private val currentTime = LocalDateTime.parse("2022-03-01T13:00:00Z", DateTimeFormatter.ISO_DATE_TIME)
        private val githubUsername = "testuser"
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
