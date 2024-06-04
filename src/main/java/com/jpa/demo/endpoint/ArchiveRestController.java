package com.jpa.demo.endpoint;


import com.jpa.demo.domain.Archive;
import com.jpa.demo.domain.ArchiveMeta;
import com.jpa.demo.service.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/archives")
public class ArchiveRestController {

    @Autowired
    private ArchiveService archiveService;

    @GetMapping
    public List<ArchiveMeta> list() {
        return archiveService.findAllMetadata();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArchiveMeta> getById(@PathVariable Long id) {
        Optional<ArchiveMeta> archiveMeta = archiveService.findMetadataById(id);
        return archiveMeta.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/upload")
    public ResponseEntity<ArchiveMeta> uploadArchive(@RequestParam("file") MultipartFile file) {
        try {
            Archive archive = archiveService.saveArchiveWithMeta(file);
            return ResponseEntity.ok(archive.getArchiveMeta());
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArchiveMeta> update(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            Archive archive = archiveService.updateArchiveWithMeta(id, file);
            return ResponseEntity.ok(archive.getArchiveMeta());
        } catch (IOException e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        archiveService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Optional<Archive> archive = archiveService.findById(id);
        return archive.map(value -> ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + value.getArchiveMeta().getArchiveName())
                .body(value.getContent())).orElseGet(() -> ResponseEntity.notFound().build());
    }
}

