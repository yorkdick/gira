package com.rayfay.gira.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "attachments")
@Getter
@Setter
public class Attachment extends BaseEntity {
    @Column(nullable = false)
    private String filename;

    @Column(nullable = false)
    private String contentType;

    private Long size;

    @Column(nullable = false)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}