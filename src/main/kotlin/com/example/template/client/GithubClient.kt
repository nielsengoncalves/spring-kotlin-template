package com.example.template.client

import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod

@FeignClient(name = "github", url = "\${feign.client.config.github.url}")
interface GithubClient {

    @RequestMapping(method = [RequestMethod.GET], value = ["/users/{username}"])
    fun findGithubUser(@PathVariable("username") githubUsername: String): GithubUser
}

data class GithubUser(
    val login: String,
    val name: String? = null,
    val company: String? = null,
    val location: String? = null,
    val bio: String? = null,
    val hireable: Boolean? = null
)
