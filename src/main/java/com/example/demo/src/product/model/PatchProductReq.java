package com.example.demo.src.product.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PatchProductReq {
    private String productTitle;
    private int productPrice;
    private String productContents;
    private int priceOffer;
    private int regionIdx;
    private int productCatagoryIdx;
    private List<String> imgUrl;
}
