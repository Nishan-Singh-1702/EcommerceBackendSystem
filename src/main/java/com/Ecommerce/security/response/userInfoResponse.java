package com.Ecommerce.security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class userInfoResponse {
    private Long userId;
    private String jwtToken;
    private String username;
    private List<String> roles = new ArrayList<>();

    public userInfoResponse(Long userId, String username, List<String> roles) {
        this.userId = userId;
        this.username = username;
        this.roles = roles;
    }
}
