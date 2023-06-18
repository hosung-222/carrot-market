package com.example.demo.src.user.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.w3c.dom.Text;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostReviewReq {
    private int sendUserIdx;
    private String review;
}
