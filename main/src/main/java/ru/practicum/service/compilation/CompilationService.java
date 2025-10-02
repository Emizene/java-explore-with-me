package ru.practicum.service.compilation;

import ru.practicum.compilationDto.CompilationDto;
import ru.practicum.compilationDto.NewCompilationDto;
import ru.practicum.compilationDto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);

    CompilationDto getCompilationById(Long compId);

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateRequest);
}
