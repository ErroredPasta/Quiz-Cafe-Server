package com.project.quizcafe.common.exception

open class ApiException(message: String, val statusCode: Int) : RuntimeException(message)

class BadRequestException(message: String) : ApiException(message, 400)
class AuthenticationException(message: String) : ApiException(message, 401)
class NotFoundException(message: String) : ApiException(message, 404)
class ConflictException(message: String) : ApiException(message, 409)
class InternalServerErrorException(message: String) : ApiException(message, 500)
