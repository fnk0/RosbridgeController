package com.gabilheri.rosbridgecontroller.app.adapters;

import java.util.ArrayList;
import java.util.List;

public class ListRowGroup {

    public String robotName;
    public final List<String> children = new ArrayList<String>();

    public ListRowGroup(String robotName) {
        this.robotName = robotName;
    }

    public String getRobotName() {
        return robotName;
    }

    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public List<String> getChildren() {
        return children;
    }
}
