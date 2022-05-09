package com.example.template.repository

import com.example.template.db.tables.records.UsersRecord
import com.example.template.db.tables.references.USERS
import com.example.template.model.User
import io.kotest.matchers.shouldBe
import org.jooq.DSLContext
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class UserRepositoryIntegrationTest : RepositoryIntegrationTest() {

    @Autowired
    lateinit var userRepository: UserRepository

    @Nested
    inner class InsertTest {

        @Test
        fun `it should insert user`() {
            val actualResult = userRepository.insert(user)

            actualResult shouldBe 1
            dslContext.findUserRecord(user.id) shouldBe userRecord
        }
    }

    @Nested
    inner class FindByGithubUsernameTest {

        @Test
        fun `it should find user by github username`() {
            dslContext.insertUserRecord(userRecord)

            val actualResult = userRepository.findByGithubUsername(userRecord.githubUsername!!)

            actualResult shouldBe user
        }

        @Test
        fun `it should return null if record is not found`() {
            val actualResult = userRepository.findByGithubUsername("invalid")

            actualResult shouldBe null
        }
    }

    @Nested
    inner class DeleteByGithubUsernameTest {

        @Test
        fun `it should delete user by github username`() {
            dslContext.insertUserRecord(userRecord)

            val actualResult = userRepository.deleteByGithubUsername(userRecord.githubUsername!!)

            actualResult shouldBe 1
            dslContext.findUserRecord(user.id) shouldBe null
        }
    }

    fun DSLContext.insertUserRecord(usersRecord: UsersRecord) {
        val result = dslContext.insertInto(USERS).set(usersRecord).execute()
        result shouldBe 1
    }

    fun DSLContext.findUserRecord(userId: UUID): UsersRecord? =
        dslContext.selectFrom(USERS).where(USERS.ID.eq(userId)).fetchOne()

    companion object {
        private val userId = UUID.fromString("95408c28-fcd6-427d-8f52-be64e5186b9a")
        private val currentTime = LocalDateTime.parse("2022-03-01T13:00:00Z", DateTimeFormatter.ISO_DATE_TIME)
        private val user = User(
            id = userId,
            githubUsername = "testuser",
            name = "Test User",
            company = "Github",
            location = "Berlin, Germany",
            bio = "Software Engineer",
            isAvailableForHiring = true,
            createdAt = currentTime,
            updatedAt = currentTime
        )
        private val userRecord = UsersRecord(
            id = userId,
            githubUsername = "testuser",
            name = "Test User",
            company = "Github",
            location = "Berlin, Germany",
            bio = "Software Engineer",
            isAvailableForHiring = true,
            createdAt = currentTime,
            updatedAt = currentTime
        )
    }
}
