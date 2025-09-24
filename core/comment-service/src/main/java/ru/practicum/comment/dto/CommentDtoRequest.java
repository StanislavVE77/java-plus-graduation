package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class CommentDtoRequest {

    @Size(min = 1, max = 7000)
    @NotBlank
    private String text;

}
