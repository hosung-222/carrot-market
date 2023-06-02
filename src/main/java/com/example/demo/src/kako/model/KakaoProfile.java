package com.example.demo.src.kako.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.annotation.Generated;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public class KakaoProfile {
    public Long id;
    public String connected_at;
    public Properties properties;
//    public KakaoAccount kakao_account;

    @Data
    @JsonIgnoreProperties(ignoreUnknown=true)
    public class Properties {
        public String nickname;
    }
//    @Data
//    @JsonIgnoreProperties(ignoreUnknown=true)
//    public class KakaoAccount {
//        public Boolean profile_nickname_needs_agreement;
        @Data
        @JsonIgnoreProperties(ignoreUnknown=true)
        public class Profile {
            public String nickname;
        }
    }

//}







