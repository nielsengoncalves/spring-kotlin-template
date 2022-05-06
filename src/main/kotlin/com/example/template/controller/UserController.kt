package com.example.template.controller

import com.example.template.controller.request.CreateUserRequest
import com.example.template.controller.response.UserResponse
import com.example.template.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class UserController(
    private val userService: UserService
) {

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody createUserRequest: CreateUserRequest): UserResponse {
        val user = userService.createUser(createUserRequest.githubUsername)
        return UserResponse.from(user)
    }

    @GetMapping("/user/{githubUsername}")
    @ResponseStatus(HttpStatus.OK)
    fun getUser(@PathVariable githubUsername: String): UserResponse {
        val user = userService.getUser(githubUsername)
        return UserResponse.from(user)
    }

    @DeleteMapping("/user/{githubUsername}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable githubUsername: String) {
        userService.deleteUser(githubUsername)
    }
}
