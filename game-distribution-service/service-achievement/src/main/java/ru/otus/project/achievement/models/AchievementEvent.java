package ru.otus.project.achievement.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.OffsetDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity(name = "achievement_events")
public class AchievementEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @CreatedBy
    @Column(name = "user_id")
    private long userId;

    @CreationTimestamp
    @Column(name = "event_time", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime eventTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "achievement_id")
    private Achievement achievement;

    public AchievementEvent(Achievement achievement) {
        this.achievement = achievement;
    }
}
