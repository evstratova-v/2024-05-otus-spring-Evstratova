package ru.otus.project.achievement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.project.achievement.dto.AchievementDto;
import ru.otus.project.achievement.dto.AchievementEventDto;
import ru.otus.project.achievement.exceptions.AchievementEventAlreadyExistException;
import ru.otus.project.achievement.models.Achievement;
import ru.otus.project.achievement.models.AchievementEvent;
import ru.otus.project.achievement.repositories.AchievementEventRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AchievementHistoryServiceImpl implements AchievementHistoryService {

    private final AchievementEventRepository achievementEventRepository;

    @Transactional(readOnly = true)
    @Override
    public List<AchievementEventDto> findAchievementHistoryByUserId(long userId) {
        return achievementEventRepository.findAllByUserId(userId).stream().map(AchievementEventDto::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<AchievementEventDto> findAchievementHistoryByUserIdAndGameId(long userId, long gameId) {
        return achievementEventRepository.findAllByUserIdAndAchievementGameId(userId, gameId).stream()
                .map(AchievementEventDto::toDto).toList();
    }

    @PreAuthorize("hasAuthority('SCOPE_USER')")
    @Transactional
    @Override
    public AchievementEventDto insert(long userId, AchievementDto achievementDto) {
        Achievement achievement = AchievementDto.toDomainObject(achievementDto);
        return AchievementEventDto.toDto(save(userId, achievement));
    }

    private AchievementEvent save(long userId, Achievement achievement) {
        if (achievementEventRepository.existsByUserIdAndAchievement(userId, achievement)) {
            throw new AchievementEventAlreadyExistException("User with id %s already has achievement with id %s"
                    .formatted(userId, achievement.getId()));
        }

        return achievementEventRepository.save(new AchievementEvent(achievement));
    }
}
