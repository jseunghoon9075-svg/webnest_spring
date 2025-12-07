package com.app.webnest.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.Date;

@Getter @Setter
@ToString
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class QuizResponseDTO {
    @Schema(description = "해당 문제의 아이디", example = "1", required = true)
    private Long quizId;
    @Schema(description = "유저의 아이디", example = "1", required = true)
    private Long userId;
    @Schema(required = true, description = "사용자 입력 코드", example = "js : console.log('hello, wolrd'), java : System.out.println('hello,wolrd'), sql : SELECT * FROM MEMBER")
    private String quizSubmitCode;
    private String quizSubmitResultCode;
    private String quizSubmitResult;
    private Date quizSubmitCreateAt;
    private String className;
}
