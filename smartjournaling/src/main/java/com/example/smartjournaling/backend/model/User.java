package com.example.smartjournaling.backend.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User {

    @Id
    private String email;
    private String displayName;
    private String password;

}
