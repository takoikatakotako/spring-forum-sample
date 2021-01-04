package com.swiswiswift.forum;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="post")
@Getter
@Setter
public class Post {
    @Id
    @Column(name = "post_id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "post_datetime", nullable = false)
    private LocalDateTime postDatetime;
}
