package kr.hhplus.be.ecommerce.user.presentation;

import java.time.LocalDateTime;

public class UserDto {

    public record UserResponse(Long userId,
                               String userName,
                               Long balance,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {}

}
