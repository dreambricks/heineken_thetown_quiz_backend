package br.dreambricks.heinekenquiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class DataLogService {

    @Autowired
    private DataLogRepository dataLogRepository;

    public List<DataLog> getAll(){
        return this.dataLogRepository.findAll();
    }

    public DataLog saveDataLog(String barName, String hits, String miss, String status, String timePlayed) throws ParseException {

        DataLog dataLog = new DataLog();

        dataLog.setBarName(barName);
        dataLog.setHits(Objects.requireNonNullElse(hits, "0"));
        dataLog.setMiss(Objects.requireNonNullElse(miss, "0"));
        dataLog.setStatus(status);

        String dateString = timePlayed;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        Date timePlayedDate = dateFormat.parse(dateString);
        Calendar calendarPlayedDate = Calendar.getInstance();
        calendarPlayedDate.setTime(timePlayedDate);
        calendarPlayedDate.add(Calendar.HOUR_OF_DAY, -3);
        dataLog.setTimePlayed(calendarPlayedDate.getTime());

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -3);
        dataLog.setUploadedData(calendar.getTime());

        dataLogRepository.save(dataLog);

        return dataLog;
    }

}
