package com.example.template.repository

import com.example.template.db.tables.records.UsersRecord
import com.example.template.model.User

fun UsersRecord.toUser() = User(
    id = this.id!!,
    githubUsername = this.githubUsername!!,
    name = this.name,
    company = this.company,
    location = this.location,
    bio = this.bio,
    isAvailableForHiring = this.isAvailableForHiring,
    createdAt = this.createdAt!!,
    updatedAt = this.updatedAt!!
)

fun User.toUsersRecord() = UsersRecord(
    id = this.id,
    githubUsername = this.githubUsername,
    name = this.name,
    company = this.company,
    location = this.location,
    bio = this.bio,
    isAvailableForHiring = this.isAvailableForHiring,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt
)
