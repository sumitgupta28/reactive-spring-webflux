package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {

    @Id
    private String reviewId;
    @NotNull(message = "review.movieInfoId must not be null")
    private Long movieInfoId;
    @NotNull(message = "review.comment must not be null")
    private String comment;
    @Min(value = 0L, message = "rating.negative : please pass a non-negative value")
    private Double rating;
}
