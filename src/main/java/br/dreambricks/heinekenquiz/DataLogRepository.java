package br.dreambricks.heinekenquiz;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public interface DataLogRepository extends MongoRepository<DataLog, String> {
    Page<DataLog> findByBarName(String barName, Pageable pageable);
    Page<DataLog> findByTimePlayedBetween(@DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, Pageable pageable);
    Page<DataLog> findByBarNameAndTimePlayedBetween(String barName, @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate, @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate, Pageable pageable);

}
