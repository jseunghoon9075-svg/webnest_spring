package com.app.webnest.domain.dto;

import lombok.*;

@Getter @Setter
@ToString
@NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class QuizResponseDTO {
    private Long quizId;
    private Long userId;
    private String userEmail;
    private String code;
    private String className;
    private boolean isBookmark;
    private boolean isSolve;
}
