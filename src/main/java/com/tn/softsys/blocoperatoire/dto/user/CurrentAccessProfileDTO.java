package com.tn.softsys.blocoperatoire.dto.user;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrentAccessProfileDTO {
    private String userId;
    private String email;
    private String displayName;
    private List<String> roles;
    private List<String> permissionCodes;
}
