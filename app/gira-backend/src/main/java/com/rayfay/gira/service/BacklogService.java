package com.rayfay.gira.service;

import com.rayfay.gira.dto.IssueDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BacklogService {
    IssueDto createIssue(IssueDto issueDto);

    Page<IssueDto> getIssues(String filter, Pageable pageable);

    IssueDto getIssueById(Long issueId);

    IssueDto updateIssue(Long issueId, IssueDto issueDto);

    void deleteIssue(Long issueId);
}