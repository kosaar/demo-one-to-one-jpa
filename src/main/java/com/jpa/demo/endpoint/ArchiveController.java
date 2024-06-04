package com.jpa.demo.endpoint;

import com.jpa.demo.domain.Archive;
import com.jpa.demo.domain.ArchiveMeta;
import com.jpa.demo.service.ArchiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/archive")
public class ArchiveController {

    @Autowired
    private ArchiveService archiveService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("archives", archiveService.findAllMetadata());
        return "archive/list";
    }

    @GetMapping("/upload")
    public String uploadForm(Model model) {
        return "archive/upload";
    }

    @PostMapping("/upload")
    public String uploadArchive(@RequestParam("file") MultipartFile file, Model model) {
        try {
            Archive archive = archiveService.saveArchiveWithMeta(file);
            model.addAttribute("archive", archive.getArchiveMeta());
            return "redirect:/archive";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to upload file: " + e.getMessage());
            return "archive/upload";
        }
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<ArchiveMeta> archiveMeta = archiveService.findMetadataById(id);
        if (archiveMeta.isPresent()) {
            model.addAttribute("archiveMeta", archiveMeta.get());
            return "archive/form";
        } else {
            return "redirect:/archive";
        }
    }

    @PostMapping("/edit/{id}")
    public String update(@PathVariable Long id, @RequestParam("file") MultipartFile file, Model model) {
        try {
            Archive archive = archiveService.updateArchiveWithMeta(id, file);
            return "redirect:/archive";
        } catch (Exception e) {
            model.addAttribute("error", "Failed to update archive: " + e.getMessage());
            return "archive/form";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        archiveService.deleteById(id);
        return "redirect:/archive";
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        Optional<Archive> archive = archiveService.findById(id);
        return archive.map(value -> ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + value.getArchiveMeta().getArchiveName())
                .body(value.getContent())).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
