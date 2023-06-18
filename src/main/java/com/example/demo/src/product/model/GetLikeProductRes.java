package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetLikeProductRes {
    private String catagoryName;
    private String productTitle;
    private int productPrice;
    private String regionName;
    private Timestamp updatedAt;
    private String imgUrl;
    private int cntLike;
}
