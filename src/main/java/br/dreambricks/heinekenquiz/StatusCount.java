package br.dreambricks.heinekenquiz;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

public class StatusCount {
    String status;
    long count;

    public StatusCount() {
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}