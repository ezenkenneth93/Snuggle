package com.snuggle.homework.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
  name = "homework",
  uniqueConstraints = @UniqueConstraint(
    name = "uq_homework_user_date",
    columnNames = {"user_id","submitted_date"}
  )
)
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

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Lob
    @Column(name = "user_homework")
    private String userHomework;

    @Lob
    @Column(name = "user_question")
    private String userQuestion;

    @Lob
    @Column(name = "ai_feedback")
    private String aiFeedback;

    @Lob
    @Column(name = "ai_answer")
    private String aiAnswer;

    // 가상 컬럼 매핑 (읽기 전용)
    @Column(name="submitted_date", insertable=false, updatable=false)
    private LocalDate submittedDate;

    @PrePersist
    public void onCreate() {
        this.submittedAt = LocalDateTime.now();
    }
}

