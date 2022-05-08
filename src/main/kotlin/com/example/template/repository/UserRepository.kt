package com.example.template.repository

import com.example.template.db.tables.records.UsersRecord
import com.example.template.db.tables.references.USERS
import com.example.template.model.User
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val dslContext: DSLContext
) {

    fun insert(user: User): Int =
        dslContext
            .insertInto(USERS)
            .set(user.toUsersRecord())
            .execute()

    fun findByGithubUsername(githubUsername: String): User? =
        dslContext
            .selectFrom(USERS)
            .where(USERS.GITHUB_USERNAME.eq(githubUsername))
            .fetchOne(UsersRecord::toUser)

    fun deleteByGithubUsername(githubUsername: String): Int =
        dslContext
            .deleteFrom(USERS)
            .where(USERS.GITHUB_USERNAME.eq(githubUsername))
            .execute()
}
