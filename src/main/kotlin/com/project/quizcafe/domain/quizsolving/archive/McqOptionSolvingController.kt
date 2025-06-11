//package com.project.quizcafe.domain.quizsolving.controller
//
//import com.project.quizcafe.common.response.ApiResponse
//import com.project.quizcafe.common.response.ApiResponseFactory
//import com.project.quizcafe.domain.quizsolving.dto.request.CreateMcqOptionSolvingRequest
//import com.project.quizcafe.domain.quizsolving.dto.request.CreateSingleMcqOptionSolvingRequest
//import com.project.quizcafe.domain.quizsolving.dto.request.UpdateMcqOptionSolvingRequest
//import com.project.quizcafe.domain.quizsolving.dto.response.McqOptionSolvingResponse
//import com.project.quizcafe.domain.quizsolving.service.McqOptionSolvingService
//import io.swagger.v3.oas.annotations.Operation
//import io.swagger.v3.oas.annotations.tags.Tag
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import org.springframework.web.bind.annotation.*
//
//@RestController
//@RequestMapping("/quiz/mcq-option-solving")
//@Tag(name = "McqOptionSolving", description = "MCQ 옵션 풀이 관련 API")
//class McqOptionSolvingController(
//    private val mcqOptionSolvingService: McqOptionSolvingService
//) {
//
//    @PostMapping
//    @Operation(summary = "MCQ 옵션 풀이 생성", description = "사용자가 MCQ 옵션 풀이를 생성합니다.")
//    fun createMcqOptionSolving(@RequestBody request: CreateSingleMcqOptionSolvingRequest): ResponseEntity<ApiResponse<Long?>> {
//        val mcqOptionSolving = mcqOptionSolvingService.createMcqOptionSolving(request)
//        return ApiResponseFactory.success(
//            data = mcqOptionSolving.id,
//            message = "MCQ 옵션 풀이 생성 성공",
//            status = HttpStatus.CREATED
//        )
//    }
//
//    @PatchMapping("/{id}")
//    @Operation(summary = "MCQ 옵션 풀이 수정", description = "MCQ 옵션 풀이를 수정합니다.")
//    fun updateMcqOptionSolving(
//        @PathVariable id: Long,
//        @RequestBody request: UpdateMcqOptionSolvingRequest
//    ): ResponseEntity<ApiResponse<Unit?>> {
//        mcqOptionSolvingService.updateMcqOptionSolving(id, request)
//        return ApiResponseFactory.success(
//            message = "MCQ 옵션 풀이 수정 성공"
//        )
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "MCQ 옵션 풀이 삭제", description = "MCQ 옵션 풀이를 삭제합니다.")
//    fun deleteMcqOptionSolving(@PathVariable id: Long): ResponseEntity<ApiResponse<Unit?>> {
//        mcqOptionSolvingService.deleteMcqOptionSolving(id)
//        return ApiResponseFactory.success(
//            message = "MCQ 옵션 풀이 삭제 성공"
//        )
//    }
//
//    @GetMapping("/{quizSolvingId}")
//    @Operation(summary = "퀴즈 풀이 ID로 MCQ 옵션 풀이 조회", description = "특정 퀴즈 풀이에 대한 MCQ 옵션 풀이 목록을 조회합니다.")
//    fun getMcqOptionsByQuizSolvingId(
//        @PathVariable quizSolvingId: Long
//    ): ResponseEntity<ApiResponse<List<McqOptionSolvingResponse>?>> {
//        val mcqOptions = mcqOptionSolvingService.getMcqOptionsByQuizSolvingId(quizSolvingId)
//        return ApiResponseFactory.success(
//            data = mcqOptions,
//            message = "MCQ 옵션 풀이 조회 성공"
//        )
//    }
//}
