package ch.zhaw.karateaicoach.model;

import java.util.List;

import org.springframework.data.annotation.Id;

public class TrainingsplanStatusAggregationDTO {

    @Id
    private String id;

    private int count;
    private List<String> trainingsplanIds;

    public TrainingsplanStatusAggregationDTO() {}

    public String getId() {
        return id;
    }

    public int getCount() {
        return count;
    }

    public List<String> getTrainingsplanIds() {
        return trainingsplanIds;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setTrainingsplanIds(List<String> trainingsplanIds) {
        this.trainingsplanIds = trainingsplanIds;
    }
}