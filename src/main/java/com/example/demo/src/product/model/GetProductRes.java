package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetProductRes {
    private String productTitle;
    private int productPrice;
    private String regionName;
    private Timestamp updatedAt;
    private String imgUrl;
    private int cntLike;
}
