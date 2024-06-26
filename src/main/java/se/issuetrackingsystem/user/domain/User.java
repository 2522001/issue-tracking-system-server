package se.issuetrackingsystem.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role")
@Getter
@NoArgsConstructor
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private LocalDateTime createdDate;

    abstract boolean canManageProject(); // Admin
    abstract boolean canManageComment(); // PL, Dev, Tester
    abstract boolean canManageIssue(); // Tester
    abstract boolean canSetAssignee(); // PL
    abstract boolean canChangeIssueStateAssignedToFixed(); // Dev
    abstract boolean canChangeIssueStateFixedToResolved(); // Tester
    abstract boolean canChangeIssueStateResolvedToClosed(); // PL

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.createdDate = LocalDateTime.now();
    }

    public String getRole() {
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }
}
