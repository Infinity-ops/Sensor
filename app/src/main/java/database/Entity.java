package database;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Entity implements Serializable {
    private static final long serialVersionUID = 1428263322645L;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String timestamp;
    public List<DataPoint> getDataPointList() {
        return dataPointList;
    }

    public void setDataPointList(List<DataPoint> dataPointList) {
        this.dataPointList = dataPointList;
    }

    private List<DataPoint> dataPointList = new ArrayList<DataPoint>();

}