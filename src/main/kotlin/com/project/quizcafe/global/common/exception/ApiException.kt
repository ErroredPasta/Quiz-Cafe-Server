package com.project.quizcafe.common.exception

open class ApiException(message: String, val statusCode: Int) : RuntimeException(message)

// 4xx - Client Errors
class BadRequestException(message: String) : ApiException(message, 400)                 // 잘못된 요청
class AuthenticationException(message: String) : ApiException(message, 401)             // 인증 실패
class ForbiddenException(message: String) : ApiException(message, 403)                  // 권한 없음
class NotFoundException(message: String) : ApiException(message, 404)                   // 리소스 없음
class MethodNotAllowedException(message: String) : ApiException(message, 405)           // 지원하지 않는 HTTP 메서드
class ConflictException(message: String) : ApiException(message, 409)                   // 중복 리소스 등 충돌
class UnsupportedMediaTypeException(message: String) : ApiException(message, 415)       // 지원하지 않는 미디어 타입
class TooManyRequestsException(message: String) : ApiException(message, 429)            // 요청 너무 많음 (rate limit)

// 5xx - Server Errors
class InternalServerErrorException(message: String) : ApiException(message, 500)        // 서버 내부 오류
class NotImplementedException(message: String) : ApiException(message, 501)             // 아직 구현되지 않음
class ServiceUnavailableException(message: String) : ApiException(message, 503)         // 서비스 이용 불가 (ex. 유지보수 중)
