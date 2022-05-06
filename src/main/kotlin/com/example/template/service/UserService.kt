package com.example.template.service

import com.example.template.client.GithubClient
import com.example.template.exception.ResourceNotFoundException
import com.example.template.model.User
import com.example.template.repository.UserRepository
import com.example.template.util.DatetimeProvider
import com.example.template.util.UuidProvider
import org.springframework.stereotype.Service

@Service
class UserService(
    private val uuidProvider: UuidProvider,
    private val datetimeProvider: DatetimeProvider,
    private val userRepository: UserRepository,
    private val githubClient: GithubClient
) {

    fun createUser(githubUsername: String): User {
        val existingUser = userRepository.findUserByGithubUsername(githubUsername)
        if (existingUser != null) return existingUser

        val githubUser = githubClient.findGithubUser(githubUsername)
            ?: throw IllegalArgumentException("User `$githubUsername` not found on Github")

        val newUser = User(
            id = uuidProvider.randomUuid(),
            githubUsername = githubUsername,
            name = githubUser.name,
            company = githubUser.company,
            location = githubUser.location,
            bio = githubUser.bio,
            isAvailableForHiring = githubUser.hireable,
            createdAt = datetimeProvider.currentTime(),
            updatedAt = datetimeProvider.currentTime()
        )
        userRepository.insertUser(newUser)

        return newUser
    }

    fun getUser(githubUsername: String): User =
        userRepository.findUserByGithubUsername(githubUsername)
            ?: throw ResourceNotFoundException("User `$githubUsername` not found")

    fun deleteUser(githubUsername: String) {
        userRepository.deleteUserByGithubUsername(githubUsername)
    }
}
