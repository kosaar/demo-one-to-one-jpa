package com.jpa.demo.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Archive {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "archive_id_generator")
    @SequenceGenerator(name = "archive_id_generator", sequenceName = "primary_sequence", allocationSize = 1, initialValue = 10000)
    Long id;

    @Lob
    @Column(nullable = false, name = "content")
    private byte[] content;

    @OneToOne(mappedBy = "archive", cascade = CascadeType.ALL)
    private ArchiveMeta archiveMeta;
}

