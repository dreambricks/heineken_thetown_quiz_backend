package br.dreambricks.heinekenquiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/datalogs")
public class DataLogController {

    @Autowired
    DataLogService dataLogService;

    @Autowired
    DataLogRepository dataLogRepository;

    @PostMapping("/upload")
    public DataLog uploadFile(@RequestParam("barName") String barName,@RequestParam(required = false, value="hits") String hits,@RequestParam(required = false, value="miss") String miss,@RequestParam("status") String status,@RequestParam("timePlayed") String timePlayed) throws ParseException {
        return dataLogService.saveDataLog(barName, hits, miss, status, timePlayed);
    }

    @GetMapping
    public List<DataLog> getAllDataLog() {
        return this.dataLogService.getAll();
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
            @RequestParam(defaultValue = "asc") String sortDirection,
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


}