package com.example.template.client

import feign.FeignException.NotFound
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.JsonBody
import org.mockserver.verify.VerificationTimes
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus

class GithubClientIntegrationTest : ClientIntegrationTest() {

    @Autowired
    lateinit var githubClient: GithubClient

    @Nested
    inner class GetGithubUserTest {

        @Test
        fun `it should return github user when Github API returns 200 - OK`() {
            val githubUsername = "octocat"
            mockServer
                .`when`(request().withMethod("GET").withPath("/users/$githubUsername"))
                .respond(
                    response()
                        .withStatusCode(HttpStatus.OK.value())
                        .withBody(
                            JsonBody(
                                """
                                {
                                    "login": "octocat",
                                    "id": 1,
                                    "type": "User",
                                    "name": "Github Octocat",
                                    "company": "GitHub",
                                    "blog": "https://github.com/blog",
                                    "location": "San Francisco",
                                    "email": "octocat@github.com",
                                    "hireable": false,
                                    "bio": "There once was...",
                                    "created_at": "2008-01-14T04:33:35Z",
                                    "updated_at": "2008-01-14T04:33:35Z"
                                }
                                """
                            )
                        )
                )

            val actualGithubUser = githubClient.findGithubUser(githubUsername)

            actualGithubUser shouldBe GithubUser(
                login = "octocat",
                name = "Github Octocat",
                company = "GitHub",
                location = "San Francisco",
                bio = "There once was...",
                hireable = false
            )

            mockServer.verify(
                request().withMethod("GET").withPath("/users/$githubUsername"),
                VerificationTimes.exactly(1)
            )
        }

        @Test
        fun `it should raise NotFound when Github API returns 404 - NOT FOUND`() {
            val invalidUsername = "invalid"
            mockServer
                .`when`(request().withMethod("GET").withPath("/users/$invalidUsername"))
                .respond(
                    response()
                        .withStatusCode(HttpStatus.NOT_FOUND.value())
                )

            shouldThrow<NotFound> {
                githubClient.findGithubUser(invalidUsername)
            }

            mockServer.verify(
                request().withMethod("GET").withPath("/users/$invalidUsername"),
                VerificationTimes.exactly(1)
            )
        }
    }
}
