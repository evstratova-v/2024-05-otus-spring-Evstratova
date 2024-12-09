package ru.otus.project.gameinfo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GameInfoResponse {

    @JsonProperty("gameInfo")
    private GameDto gameDto;

    @JsonProperty("achievementInfo")
    private AchievementResponse achievementResponse;

    private long price;

    private String rating;
}
