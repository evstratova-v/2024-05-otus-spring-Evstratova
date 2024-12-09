package ru.otus.project.achievement.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.project.achievement.dto.AchievementDto;
import ru.otus.project.achievement.dto.CreateAchievementRequest;
import ru.otus.project.achievement.exceptions.AchievementNotFoundException;
import ru.otus.project.achievement.models.Achievement;
import ru.otus.project.achievement.repositories.AchievementRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AchievementServiceImpl implements AchievementService {

    private final AchievementRepository achievementRepository;

    @Override
    public Optional<AchievementDto> findById(long achievementId) {
        return achievementRepository.findById(achievementId).map(AchievementDto::toDto);
    }

    @Override
    public List<AchievementDto> findAllByGameIdAndNotHidden(long gameId) {
        return achievementRepository.findAllByGameIdAndIsHiddenFalse(gameId).stream().map(AchievementDto::toDto)
                .toList();
    }

    @Override
    public int countByGameIdIsHiddenTrue(long gameId) {
        return achievementRepository.countByGameIdAndIsHiddenTrue(gameId);
    }

    @PreAuthorize("hasAuthority('SCOPE_DEVELOPER')")
    @Transactional
    @Override
    public AchievementDto insert(CreateAchievementRequest createAchievementRequest) {
        Achievement achievement = new Achievement(0, createAchievementRequest.getGameId(),
                createAchievementRequest.getName(), createAchievementRequest.getDescription(),
                createAchievementRequest.isHidden());
        return AchievementDto.toDto(achievementRepository.save(achievement));
    }

    @PreAuthorize("hasAuthority('SCOPE_DEVELOPER')")
    @Transactional
    @Override
    public AchievementDto update(AchievementDto achievementDto) {
        long achievementId = achievementDto.getId();
        if (!achievementRepository.existsById(achievementDto.getId())) {
            throw new AchievementNotFoundException("Achievement with id %s not found".formatted(achievementId));
        }

        Achievement achievement = AchievementDto.toDomainObject(achievementDto);
        return AchievementDto.toDto(achievementRepository.save(achievement));
    }

    @PreAuthorize("hasAuthority('SCOPE_DEVELOPER')")
    @Override
    public void deleteById(long achievementId) {
        achievementRepository.deleteById(achievementId);
    }

    @PreAuthorize("hasAuthority('SCOPE_DEVELOPER')")
    @Override
    public void deleteByGameId(long gameId) {
        achievementRepository.deleteAllByGameId(gameId);
    }
}
