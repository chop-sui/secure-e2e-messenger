package org.sec.secureapp.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
@Accessors(chain = true)
@Table(name = "users")
public class User {
    @Id
    @SequenceGenerator(name = "users_generator", sequenceName = "users_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_generator")
    private Integer id;

    @NotNull(message = "Username cannot be empty")
    @Column(name = "username", unique = true)
    private String username;

    @NotNull(message = "Password cannot be empty")
    @Length(min = 7, message = "Password should be at least 7 characters long")
    @Column(name = "password")
    private String password;

    @Column(name = "about_me")
    private String aboutMe;

    @ManyToMany(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinTable(
        name="users_roles",
        joinColumns={@JoinColumn(name="user_id", referencedColumnName="id")},
        inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="id")})
    private List<Role> roles = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "friend_relation",
            joinColumns = @JoinColumn(name = "fk_id_user", referencedColumnName = "id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "fk_id_friend", referencedColumnName = "id", nullable = false)
    )
    private List<User> friends;

    @ElementCollection
    @CollectionTable(name = "user_todos", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "todos")
    private List<String> todos;

    public void addFriend(User friend) {
        this.friends.add(friend);
        friend.getFriends().add(this);
    }

    public void removeFriend(Integer friendId) {
        User friend = this.friends.stream().filter(f -> Objects.equals(f.getId(), friendId)).findFirst().orElse(null);
        if (friend != null) {
            this.friends.remove(friend);
            friend.getFriends().remove(this);
        }
    }

    public void addTodo(String todo) {
        this.todos.add(todo);
    }
}
