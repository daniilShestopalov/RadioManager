package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
public class FileService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    private final String audioFilesDir = "uploads/audios";

    public FileService() {
        createDirectory(Path.of(audioFilesDir));
    }

    private void createDirectory(Path directory) {
        try {
            Files.createDirectories(directory);
            LOGGER.info("Created directory: {}", directory);
        } catch (IOException e) {
            LOGGER.error("Error creating directory: {}", directory, e);
            throw new RuntimeException("Error creating directory", e);
        }
    }

    public String saveAudio(MultipartFile file) {
        return saveFile(file, Path.of(audioFilesDir));
    }

    private String saveFile(MultipartFile file, Path directory) {
        try {
            String filename = file.getOriginalFilename();
            Path filepath = directory.resolve(Objects.requireNonNull(filename));
            Files.write(filepath, file.getBytes());
            LOGGER.info("File saved: {}", filepath);
            return filename;
        } catch (IOException e) {
            LOGGER.error("Error saving file: {}", e.getMessage());
            throw new RuntimeException("Error saving file", e);
        }
    }

    private byte[] getFile(Path directory, String filename) {
        try {
            Path filepath = directory.resolve(filename);
            return Files.readAllBytes(filepath);
        } catch (IOException e) {
            LOGGER.error("Error reading file: {}", e.getMessage());
            throw new RuntimeException("Error reading file", e);
        }
    }

    public byte[] getAudio(String filename) {
        return getFile(Path.of(audioFilesDir), filename);
    }

    public double getAudioDuration(MultipartFile file) throws IOException {
        LOGGER.debug("Fetching audio duration from file: {}", file.getOriginalFilename());
        if (!FileUtils.isAudioFile(file)) {
            LOGGER.error("File is not an audio file");
            throw new IllegalArgumentException("File is not an audio file");
        }
        double duration = FileUtils.getMp4Duration(file);
        LOGGER.debug("Duration: {}", duration);
        return duration;
    }

}
