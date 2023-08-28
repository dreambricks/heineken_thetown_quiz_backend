package br.dreambricks.heinekenquiz;

import com.opencsv.CSVWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/datalogs")
public class DataLogController {

    @Autowired
    DataLogService dataLogService;

    @Autowired
    DataLogRepository dataLogRepository;

    @PostMapping("/upload")
    public DataLog uploadFile(@RequestParam("barName") String barName, @RequestParam(required = false, value = "hits") String hits, @RequestParam(required = false, value = "miss") String miss, @RequestParam("status") String status, @RequestParam("timePlayed") String timePlayed, @RequestParam(required = false, value = "latitude") String latitude, @RequestParam(required = false, value = "longitude") String longitude) throws ParseException {
        return dataLogService.saveDataLog(barName, hits, miss, status, timePlayed, latitude, longitude);
    }

    @GetMapping
    public List<DataLog> getAllDataLog() {
        return this.dataLogRepository.findAllByOrderByTimePlayedDesc();
    }

    @GetMapping("/{id}")
    public DataLog getDataLogById(@PathVariable String id) {
        return dataLogRepository.findById(id).orElse(null);
    }

    @GetMapping("/paged")
    public Page<DataLog> getDataLogsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timePlayed") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection,
            @RequestParam(required = false) String barName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam(required = false) String status) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);

        if (barName != null && startDate != null && endDate != null) {
            return dataLogRepository.findByBarNameAndTimePlayedBetween(barName, startDate, endDate, pageable);
        } else if (barName != null) {
            return dataLogRepository.findByBarName(barName, pageable);
        } else if (status != null && startDate != null && endDate != null) {
            return dataLogRepository.findByStatusAndTimePlayedBetween(status, startDate, endDate, pageable);
        } else if (startDate != null && endDate != null) {
            return dataLogRepository.findByTimePlayedBetween(startDate, endDate, pageable);
        } else if (status != null) {
            return dataLogRepository.findByStatus(status, pageable);
        } else {
            return dataLogRepository.findAll(pageable);
        }
    }


    @GetMapping("/geolocated")
    public Page<DataLog> getDataLogsByGeolocation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "timePlayed") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDirection,
            @RequestParam double latitude,
            @RequestParam double longitude,
            @RequestParam(defaultValue = "10") double maxDistance) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, direction, sortBy);

        Point location = new Point(longitude, latitude);
        Distance distance = new Distance(maxDistance, Metrics.KILOMETERS);

        return dataLogRepository.findByLocationNear(location, distance, pageable);
    }


    @GetMapping("/status/count")
    public List<StatusCount> getStatusCountsByBarName(@RequestParam(required = false) String barName,
                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        if (barName != null && startDate != null && endDate != null) {
            return dataLogRepository.countStatusOccurrencesByBarNameAndTimePlayedBetween(barName,startDate,endDate);
        }else if (barName !=null){
            return dataLogRepository.countStatusOccurrencesByBarName(barName);
        }else{
            return dataLogRepository.countStatusOccurrences();
        }
    }

    @GetMapping("/downloaddata")
    public ResponseEntity<InputStreamResource> downloadData() throws IOException {
        List<DataLog> dataLogs = dataLogRepository.findAll();

        // Create CSV file
        File csvFile = new File("data.csv");
        CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFile));
        String[] header = {"barName", "uploadedData", "timePlayed", "status", "hits", "miss"};
        csvWriter.writeNext(header);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (DataLog dataLog : dataLogs) {
            String formattedTimePlayed = dateFormat.format(dataLog.getTimePlayed());
            String formattedUploadedData = dateFormat.format(dataLog.getUploadedData());

            String[] row = {dataLog.getBarName(),formattedUploadedData, formattedTimePlayed,dataLog.getStatus(), dataLog.getHits(), dataLog.getMiss()};
            csvWriter.writeNext(row);
        }
        csvWriter.close();

        // Create ZIP file
        File zipFile = new File("data.zip");
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zipOut = new ZipOutputStream(fos);

        FileInputStream fis = new FileInputStream(csvFile);
        ZipEntry zipEntry = new ZipEntry(csvFile.getName());
        zipOut.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
        zipOut.closeEntry();

        zipOut.close();
        fos.close();

        // Prepare response entity
        InputStreamResource resource = new InputStreamResource(new FileInputStream(zipFile));

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=data.zip")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(zipFile.length())
                .body(resource);
    }

    @GetMapping("/latest-uploaded")
    public ResponseEntity<Date> getLatestUploadedDataByBarName(@RequestParam String barName) {
        List<DataLog> dataLog = dataLogRepository.findLatestUploadedDataByBarName(barName);
        Date mostRecentUploadedData = dataLog.get(0).uploadedData;

        if (mostRecentUploadedData == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mostRecentUploadedData);
    }

    @GetMapping("/latest-uploaded-total")
    public ResponseEntity<Date> getLatestUploadedData() {
        List<DataLog> dataLog = dataLogRepository.findLatestUploadedData();
        Date mostRecentUploadedData = dataLog.get(0).uploadedData;

        if (mostRecentUploadedData == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mostRecentUploadedData);
    }

}