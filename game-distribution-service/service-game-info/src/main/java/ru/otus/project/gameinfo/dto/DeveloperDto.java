package ru.otus.project.gameinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.project.gameinfo.models.Developer;

@Data
@AllArgsConstructor
public class DeveloperDto {
    private long id;

    @NotBlank
    private String fullName;

    public static DeveloperDto toDto(Developer developer) {
        return new DeveloperDto(developer.getId(), developer.getFullName());
    }
}
