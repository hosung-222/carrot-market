package com.example.demo.src.user.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetUserRes {
    private String userName;
    private Double userDegree;
    private int retradeRate;
    private int responseRate;
    private int cntProduct;
    private List<String> userRegions;
}
