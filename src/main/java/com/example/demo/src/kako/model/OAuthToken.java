package com.example.demo.src.kako.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class OAuthToken {
    private String Access_token;
    private String token_type;
     private String refresh_token;
     private int expires_in;
     private String scope;
     private int refresh_token_expires_in;
}
