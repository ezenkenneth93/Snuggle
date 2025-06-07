package com.snuggle.homework.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "homework")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Homework {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime submittedAt;

    @Lob
    @Column(name = "user_answer")
    private String userAnswer;

    @Lob
    private String feedback;

    @Lob
    @Column(name = "suggested_answer")
    private String suggestedAnswer;

    // insert 직전에 자동으로 호출된다. 현재 시각으로 채워져 저장된다.
    @PrePersist
    public void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }
}
