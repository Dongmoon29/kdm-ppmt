package io.dm29.ppmtool.services;

import io.dm29.ppmtool.domain.Project;
import io.dm29.ppmtool.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    public Project saveOrUpdateProject(Project project) {
        // Logic
        return projectRepository.save(project);
    }

}
