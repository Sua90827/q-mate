package com.qmate.security;

public record UserPrincipal(Long userId, String email, String role) {
}
