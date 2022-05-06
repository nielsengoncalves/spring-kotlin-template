package com.example.template.controller.response

import com.example.template.model.User
import java.util.UUID

data class UserResponse(
    val id: UUID,
    val githubUsername: String,
    val name: String?,
    val company: String?,
    val location: String?,
    val bio: String?,
    val isAvailableForHiring: Boolean?
) {

    companion object {
        fun from(user: User) = UserResponse(
            id = user.id,
            githubUsername = user.githubUsername,
            name = user.name,
            company = user.company,
            location = user.location,
            bio = user.bio,
            isAvailableForHiring = user.isAvailableForHiring
        )
    }
}
