package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.model.Compilation;
import ru.practicum.compilationDto.CompilationDto;
import ru.practicum.compilationDto.NewCompilationDto;
import ru.practicum.compilationDto.UpdateCompilationRequest;

import java.util.List;

@Mapper(componentModel = "spring", uses = {EventMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CompilationMapper {

    @Mapping(source = "events", target = "events")
    CompilationDto toDto(Compilation compilation);

    List<CompilationDto> toDtoList(List<Compilation> compilations);

//    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toEntity(NewCompilationDto newCompilationDto);

//    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Compilation toEntity(UpdateCompilationRequest updateRequest);
}
