package com.bad.bigad.config;

import lombok.Data;

import java.security.Principal;

@Data
public class UserPrincipal implements Principal {
    private final String name;
    private String nick_name;

    public UserPrincipal(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
