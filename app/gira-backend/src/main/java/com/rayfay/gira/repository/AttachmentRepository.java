package com.rayfay.gira.repository;

import com.rayfay.gira.entity.Attachment;
import com.rayfay.gira.entity.Task;
import com.rayfay.gira.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {
    List<Attachment> findByTask(Task task);

    Page<Attachment> findByTask(Task task, Pageable pageable);

    Page<Attachment> findByUser(User user, Pageable pageable);
}