package InuCapstone.Server.domain.key;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthKey {

    @Id
    @GeneratedValue
    @Column(name = "key_id")
    private Long keyId;

    //User와 column이름이 중복되는데 어떡하지?
    @Column(name = "email",nullable = false)
    private String email;

    @Column(name = "authKey",nullable = false)
    private String authKey;

    @Column(name = "created_time",nullable = false)
    private LocalDateTime createdTime;

    @Builder
    public AuthKey(String email, String authKey, LocalDateTime createdTime){
        this.email=email;
        this.authKey = authKey;
        this.createdTime=createdTime;
    }
}