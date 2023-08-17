package br.dreambricks.heinekenquiz;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection = "datalogs")
public class DataLog {

    @Id
    String id;
    String barName;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date uploadedData;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date timePlayed;
    String status;
    String hits;
    String miss;


    public DataLog() {
    }

    public DataLog(String id, String barName, Date uploadedData, Date timePlayed, String status, String hits, String miss) {
        this.id = id;
        this.barName = barName;
        this.uploadedData = uploadedData;
        this.timePlayed = timePlayed;
        this.status = status;
        this.hits = hits;
        this.miss = miss;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBarName() {
        return barName;
    }

    public void setBarName(String barName) {
        this.barName = barName;
    }

    public Date getUploadedData() {
        return uploadedData;
    }

    public void setUploadedData(Date uploadedData) {
        this.uploadedData = uploadedData;
    }

    public Date getTimePlayed() {
        return timePlayed;
    }

    public void setTimePlayed(Date timePlayed) {
        this.timePlayed = timePlayed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHits() {
        return hits;
    }

    public void setHits(String hits) {
        this.hits = hits;
    }

    public String getMiss() {
        return miss;
    }

    public void setMiss(String miss) {
        this.miss = miss;
    }
}
