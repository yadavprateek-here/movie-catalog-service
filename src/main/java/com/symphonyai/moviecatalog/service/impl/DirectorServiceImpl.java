package com.symphonyai.moviecatalog.service.impl;

import com.symphonyai.moviecatalog.dto.request.DirectorRequest;
import com.symphonyai.moviecatalog.dto.response.DirectorResponse;
import com.symphonyai.moviecatalog.entity.Director;
import com.symphonyai.moviecatalog.exception.ResourceNotFoundException;
import com.symphonyai.moviecatalog.mapper.EntityMapper;
import com.symphonyai.moviecatalog.repository.DirectorRepository;
import com.symphonyai.moviecatalog.service.DirectorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DirectorServiceImpl implements DirectorService {

    private final DirectorRepository directorRepository;

    public DirectorServiceImpl(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    @Override
    public DirectorResponse createDirector(DirectorRequest request) {
        Director director = new Director(
                request.getName(),
                request.getNationality(),
                request.getBirthYear()
        );
        Director saved = directorRepository.save(director);
        return EntityMapper.toDirectorResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DirectorResponse> getAllDirectors() {
        return directorRepository.findAll().stream()
                .map(EntityMapper::toDirectorResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DirectorResponse getDirectorById(Long id) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Director not found with id: " + id));
        return EntityMapper.toDirectorResponse(director);
    }

    @Override
    public DirectorResponse updateDirector(Long id, DirectorRequest request) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Director not found with id: " + id));

        director.setName(request.getName());
        director.setNationality(request.getNationality());
        director.setBirthYear(request.getBirthYear());

        Director updated = directorRepository.save(director);
        return EntityMapper.toDirectorResponse(updated);
    }

    @Override
    public void deleteDirector(Long id) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Director not found with id: " + id));
        directorRepository.delete(director);
    }
}
