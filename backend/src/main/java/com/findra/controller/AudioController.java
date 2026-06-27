package com.findra.controller;

import com.findra.dto.audio.AudioExtractionResponse;
import com.findra.service.AudioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    private final AudioService audioService;

    public AudioController(AudioService audioService) {
        this.audioService = audioService;
    }

    @PostMapping("/procesar")
    @ResponseStatus(HttpStatus.OK)
    public AudioExtractionResponse procesar(@RequestParam("file") MultipartFile file) {
        return audioService.procesarAudio(file);
    }
}
