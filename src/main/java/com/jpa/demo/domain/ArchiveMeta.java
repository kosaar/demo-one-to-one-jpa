package com.jpa.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArchiveMeta {

    @Id
    private Long id;

    @Column(nullable = false)
    private String archiveName;

    @Column(nullable = false)
    private String clientNum;

    @Column(nullable = false)
    private String uploader;

    @OneToOne
    @MapsId
    private Archive archive;

    private String etVersion;

    @Column(nullable = false)
    private String checksum;

    @Column(precision = 10, scale = 2)
    private BigDecimal size;

    @Column(nullable = false)
    private LocalDateTime extractionDate;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Version
    private int version;

}
