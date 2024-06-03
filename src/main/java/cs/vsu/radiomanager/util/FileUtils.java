package cs.vsu.radiomanager.util;

import org.mp4parser.IsoFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
public class FileUtils {

    public static boolean isAudioFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("audio/mp4");
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

    public static double getMp4Duration(MultipartFile file) throws IOException {

        Path tempFile = Files.createTempFile(null, ".mp4");
        Files.copy(file.getInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

        IsoFile isoFile = new IsoFile(tempFile.toFile().getAbsolutePath());
        double lengthInSeconds = (double) isoFile.getMovieBox().getMovieHeaderBox().getDuration() /
                isoFile.getMovieBox().getMovieHeaderBox().getTimescale();
        isoFile.close();

        Files.delete(tempFile);

        return lengthInSeconds;
    }

}
