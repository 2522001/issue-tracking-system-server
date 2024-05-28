package se.issuetrackingsystem.project.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import se.issuetrackingsystem.common.exception.CustomException;
import se.issuetrackingsystem.common.exception.ErrorCode;
import se.issuetrackingsystem.project.domain.Project;
import se.issuetrackingsystem.project.dto.ProjectRequest;
import se.issuetrackingsystem.project.dto.ProjectResponse;
import se.issuetrackingsystem.project.repository.ProjectRepository;
import se.issuetrackingsystem.user.domain.*;
import se.issuetrackingsystem.user.repository.ProjectContributorRepository;
import se.issuetrackingsystem.user.repository.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class ProjectServiceImplTest {

    @Autowired
    private ProjectServiceImpl projectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectContributorRepository projectContributorRepository;

    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    void createProject() {
        // Given
        Admin admin = new Admin("TestAdmin", passwordEncoder.encode("0000"));
        userRepository.save(admin);

        ProjectRequest request = new ProjectRequest("Test Project", admin.getId(), Collections.emptyList());

        // When
        ProjectResponse response = projectService.createProject(request);

        // Then
        assertEquals("Test Project", response.getTitle());
        assertEquals("TestAdmin", response.getAdminName());
    }

    @Test
    void createProjectWithInvalidAdmin() {
        // Given
        Long invalidAdminId = 999L;
        ProjectRequest request = new ProjectRequest("Test Project", invalidAdminId, Collections.emptyList());

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> projectService.createProject(request));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void addContributors() {

        Admin admin = new Admin("TestAdmin", passwordEncoder.encode("0000"));
        userRepository.save(admin);

        Project project = new Project("Test Project", admin);
        projectRepository.save(project);

        Dev user1 = new Dev("TestDev", passwordEncoder.encode("0000"));
        Tester user2 = new Tester("TestTester", passwordEncoder.encode("0000"));
        userRepository.save(user1);
        userRepository.save(user2);

        List<Long> contributorIds = Arrays.asList(user1.getId(), user2.getId());
        projectService.addContributors(contributorIds, project);

        List<ProjectContributor> projectContributors = projectContributorRepository.findByProject(project);

        assertNotNull(projectContributors);
        assertEquals(2, projectContributors.size());
    }

    @Test
    void getProjects() {
        // Given
        Admin admin = new Admin("TestAdmin", passwordEncoder.encode("0000"));
        userRepository.save(admin);

        Project project = new Project("Test Project", admin);
        projectRepository.save(project);

        // When
        List<ProjectResponse> response = projectService.getProjects(admin.getId());

        // Then
        assertEquals(1, response.size());
        assertEquals("Test Project", response.get(0).getTitle());
        assertEquals("TestAdmin", response.get(0).getAdminName());
    }

    @Test
    void getProjectsForInvalidUser() {
        // Given
        Long invalidUserId = 999L;

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> projectService.getProjects(invalidUserId));
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getProject() {
        // Given
        Admin admin = new Admin("TestAdmin", passwordEncoder.encode("0000"));
        userRepository.save(admin);

        Project project = new Project("Test Project", admin);
        projectRepository.save(project);

        // When
        ProjectResponse response = projectService.getProject(project.getId());

        // Then
        assertNotNull(project);
        assertEquals("Test Project", response.getTitle());
        assertEquals("TestAdmin", response.getAdminName());
    }

    @Test
    void getNonExistingProject() {
        // Given
        Long nonExistingProjectId = 999L;

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> projectService.getProject(nonExistingProjectId));
        assertEquals(ErrorCode.PROJECT_NOT_FOUND, exception.getErrorCode());
    }
}