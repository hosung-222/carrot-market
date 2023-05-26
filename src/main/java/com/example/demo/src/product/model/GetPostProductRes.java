package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GetPostProductRes {
    private String productTitle;
    private int productPrice;
    private String productContents;
    private int viewCnt;
    private String priceOffer;
    private int productStatus;
    private Timestamp updatedAt;
    private String userName;
    private Double userDegree;
    private String regionName;
    private String catagoryName;
    private List<String> imgUrls;
    private int likeProductCnt;
}
