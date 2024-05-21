package se.issuetrackingsystem.issue.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import se.issuetrackingsystem.issue.service.IssueService;
import se.issuetrackingsystem.issue.domain.Issue;
import se.issuetrackingsystem.issue.dto.IssueRequest;
import se.issuetrackingsystem.issue.dto.IssueResponse;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequestMapping("v1/issue/")
@RequiredArgsConstructor
@RestController
public class IssueController {
    private final IssueService issueService;

    @PostMapping
    public void issueCreate(@RequestBody IssueRequest issueRequest, @RequestParam("projectid") Long projectid){
        this.issueService.create(projectid,issueRequest.getTitle(),issueRequest.getDescription(),issueRequest.getUserid());
    }

    @GetMapping
    public List<IssueResponse> issueCheck(@RequestParam("projectid") Long projectid){
        List<Issue> issues;
        issues=this.issueService.getList(projectid);
        List<IssueResponse> responses = new ArrayList<>();
        for(Issue i : issues){
            responses.add(new IssueResponse(i));
        }
        return responses;
    }

    @GetMapping("detail")
    public IssueResponse issueDetail(@RequestParam("issueid") Long issueid){
        Issue issue = this.issueService.getIssue(issueid);
        IssueResponse issueResponse = new IssueResponse(issue);
        return issueResponse;
    }

    @DeleteMapping
    public void issueDelete(@RequestParam("issueid") Long issueid){
        Issue issue = this.issueService.getIssue(issueid);
        this.issueService.delete(issue);
    }

    @PatchMapping
    public void issueModify(@RequestBody IssueRequest issueRequest,@RequestParam("issueid") Long issueid){
        Issue issue = this.issueService.getIssue(issueid);
        this.issueService.modify(issue,issueRequest.getDescription());
    }

    @PostMapping("assignees")
    public void issueSetAssignee(@RequestBody IssueRequest issueRequest,@RequestParam("issueid") Long issueid){
        Issue issue = this.issueService.getIssue(issueid);
        this.issueService.setAssignee(issue,issueRequest.getAssigneeid());
    }

    @GetMapping("{status}")
    public List<IssueResponse> issueCheckByStatus(@PathVariable("status") Issue.Status status,@RequestParam("projectid") Long projectid){
        List<Issue> issues;
        issues=this.issueService.getList(projectid,status);
        List<IssueResponse> responses = new ArrayList<>();
        for(Issue i : issues){
            responses.add(new IssueResponse(i));
        }
        return responses;
    }

    @PatchMapping("status")
    public void issueChangeStatus(@RequestBody IssueRequest issueRequest,@RequestParam("issueid") Long issueid){
        Issue issue = this.issueService.getIssue(issueid);
        this.issueService.changeStatus(issue,issueRequest.getStatus());
    }
}