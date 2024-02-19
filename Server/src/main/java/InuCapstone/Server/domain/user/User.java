package InuCapstone.Server.domain.user;

import InuCapstone.Server.dto.user.UserUpdateRequestDTO;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email",nullable = false)
    private String email;

    @Column(name = "password",nullable = false)
    private String password;

    @Column(name = "phone_number",nullable = false)
    private String phoneNumber;

    @Builder
    public User(String email, String password, String phoneNumber){
        this.email=email;
        this.password=password;
        this.phoneNumber=phoneNumber;
    }

    public void updateUser(UserUpdateRequestDTO dto){
        this.password=dto.getPassword();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Favorite> favorites;
}
