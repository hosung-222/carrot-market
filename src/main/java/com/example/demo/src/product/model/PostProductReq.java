package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class PostProductReq {
    private String productTitle;
    private Integer productPrice;
    private String productContents;
    private int priceOffer;
    private int regionIdx;
    private int userIdx;
    private int productCatagoryIdx;
    private List<String> imgUrl;
}
