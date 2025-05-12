package cs.vsu.radiomanager.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Map;

@Component
public class FileUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtils.class);

    public static boolean isAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();
        String extension = (filename != null && filename.contains("."))
                ? filename.substring(filename.lastIndexOf(".") + 1) : "";

        return contentType != null && contentType.equals("audio/mpeg") && extension.equalsIgnoreCase("mp3");
    }

    public static ResponseEntity<Resource> getFileResponse(byte[] fileData, String filename) {
        if (fileData != null) {
            ByteArrayResource resource = new ByteArrayResource(fileData);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.status(404).body(null);
        }
    }

    /*public static double getMp3Duration(MultipartFile file) throws IOException {

        Path tempFile = Files.createTempFile(null, ".mp3");
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        double lengthInSeconds = 0;

        try {
            Mp3File mp3File = new Mp3File(tempFile.toFile().getAbsolutePath());
            lengthInSeconds = mp3File.getLengthInSeconds();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        } finally {
            Files.delete(tempFile);
        }

        return lengthInSeconds;
    }*/

    public static double getMp3Duration(MultipartFile file) throws IOException {
        if (!isAudioFile(file)) {
            throw new IllegalArgumentException(
                    "Invalid file. Expected audio/mpeg with extension .mp3"
            );
        }

        Path tempFile = Files.createTempFile("upload-", ".mp3");
        try {
            Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            AudioFileFormat baseFileFormat = AudioSystem.getAudioFileFormat(tempFile.toFile());
            @SuppressWarnings("unchecked")
            Map<String, Object> props = baseFileFormat.properties();

            Long microseconds = (Long) props.get("duration");
            if (microseconds == null) {
                throw new RuntimeException("Duration property not found in MP3SPI");
            }

            return Math.round(microseconds / 1_000_000.0);
        } catch (UnsupportedAudioFileException e) {
            LOGGER.error("The file is not supported by AudioSystem: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Unsupported audio format", e);
        } finally {
            // Удаляем временный файл
            try {
                Files.deleteIfExists(tempFile);
            } catch (IOException ex) {
                LOGGER.warn("Failed to delete a temporary file: {}", tempFile, ex);
            }
        }
    }


}
