package cn.edu.hnust.hnusteasyweibo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileVO {
    private Long id;
    private String username;
    private String email;
    private String bio;
    private String avatar;
    private String role;
    private int followerCount;
    private int followingCount;
    private int postCount;
    private boolean isFollowing;
}
