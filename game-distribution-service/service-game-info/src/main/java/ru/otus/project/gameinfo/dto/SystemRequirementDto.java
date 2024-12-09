package ru.otus.project.gameinfo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.otus.project.gameinfo.models.SystemRequirement;

@AllArgsConstructor
@Data
public class SystemRequirementDto {

    private String os;

    private String cpu;

    private String ram;

    private String gpu;

    private String storage;

    public static SystemRequirementDto toDto(SystemRequirement systemRequirement) {
        return new SystemRequirementDto(
                systemRequirement.getOs(),
                systemRequirement.getCpu(),
                systemRequirement.getRam(),
                systemRequirement.getGpu(),
                systemRequirement.getStorage());
    }

    public static SystemRequirement toDomainObject(SystemRequirementDto systemRequirementDto) {
        return new SystemRequirement(systemRequirementDto.getOs(), systemRequirementDto.getCpu(),
                systemRequirementDto.getRam(), systemRequirementDto.getGpu(), systemRequirementDto.getStorage());
    }
}
