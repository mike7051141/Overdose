package com.springboot.khtml.dto.myPageDto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestMyPageDto {
    private String userName;

    private String nickName;

    private String gender;

    private String address;

    private String profileUrl;

    private String password;

    private String passwordCheck;
}
