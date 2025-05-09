package cs.vsu.radiomanager.service;

import cs.vsu.radiomanager.util.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    public String saveAudio(MultipartFile file, Long fileId) {
        return saveFile(file, Path.of(audioFilesDir), fileId);
    }

    private String saveFile(MultipartFile file, Path directory, Long fileId) {
        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            validateFilename(originalFilename);
            String filename = generateUniqueFilename(fileId, originalFilename);
            Path filepath = directory.resolve(Objects.requireNonNull(filename)).normalize();
            Files.write(filepath, file.getBytes());
            LOGGER.info("File saved: {}", filepath);
            return filename;
        } catch (IOException e) {
            LOGGER.error("Error saving file: {}", e.getMessage());
            throw new RuntimeException("Error saving file", e);
        }
    }

    private void validateFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename is empty or null");
        }
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\") || filename.contains("%00")) {
            throw new IllegalArgumentException("Invalid filename: contains forbidden characters");
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
            LOGGER.error("File is not an audio file {}", file.getContentType());
            throw new IllegalArgumentException("File is not an audio file");
        }
        double duration = FileUtils.getMp3Duration(file);
        LOGGER.debug("Duration: {}", duration);
        return duration;
    }

    public boolean deleteAudio(String filename) {
        try {
            Path filepath = Path.of(audioFilesDir).resolve(filename);
            Files.deleteIfExists(filepath);
            LOGGER.info("File deleted: {}", filepath);
            return true;
        } catch (IOException e) {
            LOGGER.error("Error deleting file: {}", e.getMessage());
            throw new RuntimeException("Error deleting file", e);
        }
    }

    public String generateUniqueFilename(Long fileId, String originalFilename) {
        return fileId + "_" + originalFilename;
    }

    public List<Pair<LocalDateTime, LocalDateTime>> getTimeFromExcel(MultipartFile file) {
        try {
            LOGGER.info("Starting to read Excel file for time intervals");

            List<Pair<LocalDateTime, LocalDateTime>> timeList = new ArrayList<>();
            DataFormatter dataFormatter = new DataFormatter();
            try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
                Sheet sheet = workbook.getSheetAt(0);
                LOGGER.debug("Opened workbook and retrieved sheet: {}", sheet.getSheetName());

                int rowNum;
                for (Row row : sheet) {
                    if (row == null) {
                        LOGGER.debug("Row is null, skipping...");
                        continue;
                    }

                    rowNum = row.getRowNum();

                    Cell firstCell = row.getCell(0);
                    Cell secondCell = row.getCell(1);


                    if (firstCell == null || secondCell == null) {
                        LOGGER.warn("Row {} skipped because one or both cells are null", rowNum);
                        continue;
                    }

                    String startStr = dataFormatter.formatCellValue(firstCell).trim();
                    String endStr = dataFormatter.formatCellValue(secondCell).trim();

                    if (startStr.isEmpty() || endStr.isEmpty()) {
                        LOGGER.warn("Row {} skipped because one or both cell values are empty", rowNum);
                        continue;
                    }

                    LOGGER.debug("Row {}: Start string = '{}', End string = '{}'", rowNum, startStr, endStr);

                    try {
                        LocalDateTime startTime = LocalDateTime.parse(startStr);
                        LocalDateTime endTime = LocalDateTime.parse(endStr);

                        timeList.add(Pair.of(startTime, endTime));
                    } catch (Exception e) {
                        LOGGER.error("Row {}: Error parsing date/time values." +
                                " Start: '{}', End: '{}'. Error: {}", rowNum, startStr, endStr, e.getMessage());
                    }
                }
            }
            LOGGER.info("Finished reading Excel file. Total valid time intervals read: {}", timeList.size());
            return timeList;

        } catch (Exception e) {
            LOGGER.error("Error reading exel file: {}", e.getMessage());
            throw new RuntimeException("Error reading exel file", e);
        }
    }

}
