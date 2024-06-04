package com.jpa.demo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import com.jpa.demo.domain.Archive;
import com.jpa.demo.domain.ArchiveMeta;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ArchiveRepositoryTest {

    @Autowired
    private ArchiveRepository archiveRepository;

    @Test
    public void testSaveAndFindArchive() {
        // Given
        Archive archiveContent = Archive.builder()
                .content("Test Content".getBytes())
                .build();

        ArchiveMeta archiveMeta = ArchiveMeta.builder()
                .id(10001L)
                .archiveName("Test Archive")
                .clientNum("12345")
                .uploader("uploader")
                .archive(archiveContent)
                .checksum("checksum")
                .size(new BigDecimal("10.00"))
                .extractionDate(LocalDateTime.now())
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        archiveContent.setArchiveMeta(archiveMeta);

        // When
        Archive savedArchive = archiveRepository.save(archiveContent);

        // Then
        Optional<Archive> retrievedArchive = archiveRepository.findById(savedArchive.getId());
        assertThat(retrievedArchive).isPresent();
        assertThat(retrievedArchive.get().getContent()).isEqualTo("Test Content".getBytes());
        assertThat(retrievedArchive.get().getArchiveMeta().getArchiveName()).isEqualTo("Test Archive");
    }

    @Test
    public void testUpdateArchiveContent() {
        // Given
        Archive archiveContent = Archive.builder()
                .content("Original Content".getBytes())
                .build();

        ArchiveMeta archiveMeta = ArchiveMeta.builder()
                .id(10002L)
                .archiveName("Original Archive")
                .clientNum("67890")
                .uploader("originalUploader")
                .archive(archiveContent)
                .checksum("originalChecksum")
                .size(new BigDecimal("20.00"))
                .extractionDate(LocalDateTime.now())
                .createdAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        archiveContent.setArchiveMeta(archiveMeta);

        Archive savedArchiveContent = archiveRepository.save(archiveContent);

        // When
        Optional<Archive> retrievedArchiveContent = archiveRepository.findById(savedArchiveContent.getId());
        assertThat(retrievedArchiveContent).isPresent();
        Archive archiveContentToUpdate = retrievedArchiveContent.get();
        archiveContentToUpdate.setContent("Updated Content".getBytes());

        archiveRepository.save(archiveContentToUpdate);

        // Then
        Optional<Archive> updatedArchiveContent = archiveRepository.findById(archiveContentToUpdate.getId());
        assertThat(updatedArchiveContent).isPresent();
        assertThat(updatedArchiveContent.get().getContent()).isEqualTo("Updated Content".getBytes());
    }

}