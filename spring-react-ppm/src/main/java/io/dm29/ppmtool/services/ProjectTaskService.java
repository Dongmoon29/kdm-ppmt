package io.dm29.ppmtool.services;

import io.dm29.ppmtool.domain.Backlog;
import io.dm29.ppmtool.domain.Project;
import io.dm29.ppmtool.domain.ProjectTask;
import io.dm29.ppmtool.exceptions.ProjectNotFoundException;
import io.dm29.ppmtool.repositories.BacklogRepository;
import io.dm29.ppmtool.repositories.ProjectRepository;
import io.dm29.ppmtool.repositories.ProjectTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectTaskService {

    private final BacklogRepository backlogRepository;
    private final ProjectTaskRepository projectTaskRepository;
    private final ProjectRepository projectRepository;


    public ProjectTask addProjectTask(String projectIdentifier, ProjectTask projectTask) {
        try {
            // PTs to be added to a specific project BL exists
            Backlog backlog = backlogRepository.findByProjectIdentifier(projectIdentifier);

            // set bl to pt
            projectTask.setBacklog(backlog);

            // we want our project sequence to be like IDPRO-1,IDPRO-2 ...100
            Integer backlogSequence = backlog.getPTSequence();

            // Update the BL sequence
            backlogSequence++;

            backlog.setPTSequence(backlogSequence);

            // add sequence to Project task
            projectTask.setProjectSequence(projectIdentifier + "-" + backlogSequence);
            projectTask.setProjectIdentifier(projectIdentifier);

            // Initial priority when priority null
            if (projectTask.getPriority() == null) {
                projectTask.setPriority(3);
            }
            // Initial status when status is null
            if (projectTask.getStatus() == null) {
                projectTask.setStatus("TO_DO");
            }
        } catch (Exception e) {
            throw new ProjectNotFoundException("Not found Project ID '" + projectIdentifier + "'");
        }

        return projectTaskRepository.save(projectTask);
    }

    public Iterable<ProjectTask> findBacklogById(String id) {
        Project project = projectRepository.findByProjectIdentifier(id);
        if (project == null) {
            throw new ProjectNotFoundException("Not found Project ID '" + id + "'");
        }
        return projectTaskRepository.findByProjectIdentifierOrderByPriority(id);
    }

    public ProjectTask findPTByProjectSequence(String backlog_id, String pt_id) {
        // make sure we are searching on the right backlog
        Backlog backlog = backlogRepository.findByProjectIdentifier(backlog_id);
        if (backlog == null) {
            throw new ProjectNotFoundException("Not found Project ID '" + backlog_id + "'");
        }
        // make sure that our task exists
        ProjectTask projectTask = projectTaskRepository.findByProjectSequence(pt_id);
        if (projectTask == null) {
            throw new ProjectNotFoundException("Not found Task ID'" + pt_id + "'");
        }

        //make sure that the backlog/project_id in the path corresponds to the right project
        if (!projectTask.getProjectIdentifier().equals(backlog_id)) {
            throw new ProjectNotFoundException("Project Task '" +
                    pt_id + "' does not exist in project'" + backlog_id + "'");
        }

        return projectTask;
    }

    public ProjectTask updateByProjectSequence(ProjectTask updatedTask, String backlog_id,
                                               String pt_id) {

        findPTByProjectSequence(backlog_id, pt_id);
        ProjectTask projectTask;
        projectTask = updatedTask;

        return projectTaskRepository.save(projectTask);
    }

    public void deletePTByProjectSequence(String backlog_id,
                                          String pt_id) {
        ProjectTask projectTask = findPTByProjectSequence(backlog_id, pt_id);
        projectTaskRepository.delete(projectTask);

    }
}
