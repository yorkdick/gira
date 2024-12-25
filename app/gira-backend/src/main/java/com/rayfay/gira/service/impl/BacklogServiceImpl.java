package com.rayfay.gira.service.impl;

import com.rayfay.gira.dto.IssueDto;
import com.rayfay.gira.entity.Issue;
import com.rayfay.gira.exception.ResourceNotFoundException;
import com.rayfay.gira.mapper.IssueMapper;
import com.rayfay.gira.repository.IssueRepository;
import com.rayfay.gira.service.BacklogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BacklogServiceImpl implements BacklogService {

    private final IssueRepository issueRepository;
    private final IssueMapper issueMapper;

    @Override
    @Transactional
    public IssueDto createIssue(IssueDto issueDto) {
        Issue issue = issueMapper.toEntity(issueDto);
        issue = issueRepository.save(issue);
        return issueMapper.toDto(issue);
    }

    @Override
    public Page<IssueDto> getIssues(String filter, Pageable pageable) {
        if (filter != null && !filter.isEmpty()) {
            return issueRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                    filter, filter, pageable).map(issueMapper::toDto);
        }
        return issueRepository.findAll(pageable).map(issueMapper::toDto);
    }

    @Override
    public IssueDto getIssueById(Long issueId) {
        return issueRepository.findById(issueId)
                .map(issueMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));
    }

    @Override
    @Transactional
    public IssueDto updateIssue(Long issueId, IssueDto issueDto) {
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new ResourceNotFoundException("Issue not found with id: " + issueId));

        issueMapper.updateEntity(issueDto, issue);
        issue = issueRepository.save(issue);
        return issueMapper.toDto(issue);
    }

    @Override
    @Transactional
    public void deleteIssue(Long issueId) {
        if (!issueRepository.existsById(issueId)) {
            throw new ResourceNotFoundException("Issue not found with id: " + issueId);
        }
        issueRepository.deleteById(issueId);
    }
}