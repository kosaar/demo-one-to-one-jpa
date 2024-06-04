package com.jpa.demo.service;

import com.jpa.demo.domain.Archive;
import com.jpa.demo.domain.ArchiveMeta;
import com.jpa.demo.repository.ArchiveRepository;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ArchiveService {


    @Autowired
    private ArchiveRepository archiveRepository;

    public List<ArchiveMeta> findAllMetadata() {
        return archiveRepository.findAll().stream()
                .map(Archive::getArchiveMeta)
                .collect(Collectors.toList());
    }

    public Optional<ArchiveMeta> findMetadataById(Long id) {
        return archiveRepository.findById(id)
                .map(Archive::getArchiveMeta);
    }

    public Optional<Archive> findById(Long id) {
        return archiveRepository.findById(id);
    }

    public Archive save(Archive archive) {
        return archiveRepository.save(archive);
    }

    public void deleteById(Long id) {
        archiveRepository.deleteById(id);
    }

    public Archive saveArchiveWithMeta(MultipartFile file) throws IOException {
        Archive archive = new Archive();
        archive.setContent(file.getBytes());

        ArchiveMeta archiveMeta = extractMetadata(file);
        archive.setArchiveMeta(archiveMeta);
        archiveMeta.setArchive(archive);

        return save(archive);
    }

    public Archive updateArchiveWithMeta(Long id, MultipartFile file) throws IOException {
        Optional<Archive> existingArchiveOpt = findById(id);
        if (existingArchiveOpt.isEmpty()) {
            throw new IOException("Archive not found with id: " + id);
        }

        Archive existingArchive = existingArchiveOpt.get();
        existingArchive.setContent(file.getBytes());

        ArchiveMeta archiveMeta = extractMetadata(file);
        existingArchive.setArchiveMeta(archiveMeta);
        archiveMeta.setArchive(existingArchive);

        return save(existingArchive);
    }

    private ArchiveMeta extractMetadata(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        ArchiveMeta archiveMeta = new ArchiveMeta();

        assert fileName != null;
        if (fileName.endsWith(".zip")) {
            try (ZipInputStream zis = new ZipInputStream(file.getInputStream())) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    // Example: extract some metadata from zip entries
                    // Here, you would set the metadata based on the contents of the zip file
                    archiveMeta.setArchiveName(fileName);
                    archiveMeta.setClientNum("12345");  // Placeholder
                    archiveMeta.setUploader("uploader");  // Placeholder
                    archiveMeta.setChecksum("checksum");  // Placeholder
                    archiveMeta.setSize(BigDecimal.valueOf(file.getSize()));
                    archiveMeta.setExtractionDate(LocalDateTime.now());
                    archiveMeta.setCreatedAt(OffsetDateTime.now());
                }
            }
        } else if (fileName.endsWith(".tar.gz")) {
            try (TarArchiveInputStream tais = new TarArchiveInputStream(
                    new GzipCompressorInputStream(new ByteArrayInputStream(file.getBytes())))) {
                TarArchiveEntry entry;
                while ((entry = tais.getNextTarEntry()) != null) {
                    // Example: extract some metadata from tar.gz entries
                    // Here, you would set the metadata based on the contents of the tar.gz file
                    archiveMeta.setArchiveName(fileName);
                    archiveMeta.setClientNum("12345");  // Placeholder
                    archiveMeta.setUploader("uploader");  // Placeholder
                    archiveMeta.setChecksum("checksum");  // Placeholder
                    archiveMeta.setSize(BigDecimal.valueOf(file.getSize()));
                    archiveMeta.setExtractionDate(LocalDateTime.now());
                    archiveMeta.setCreatedAt(OffsetDateTime.now());
                }
            }
        } else {
            throw new IOException("Unsupported file type: " + fileName);
        }

        return archiveMeta;
    }
}
