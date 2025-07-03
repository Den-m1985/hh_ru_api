package com.example.dto.user;

import com.example.enums.Gender;
import com.example.model.RoleEnum;
import com.example.model.User;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserInfoDto implements Serializable {
    private Integer id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private Gender gender;
    private RoleEnum role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserInfoDto() {}

    public UserInfoDto(User user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.middleName = user.getMiddleName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.role = user.getRole();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
