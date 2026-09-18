package com.duelco.obj.general;

public class TabData {
    private double tps;
    private double ping;
    private int currentServerPlayerCount;
    private int totalPlayerCount;
    private PlotInfo plotInfo;

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public double getPing() {
        return ping;
    }

    public void setPing(double ping) {
        this.ping = ping;
    }

    public int getCurrentServerPlayerCount() {
        return currentServerPlayerCount;
    }

    public void setCurrentServerPlayerCount(int currentServerPlayerCount) {
        this.currentServerPlayerCount = currentServerPlayerCount;
    }

    public int getTotalPlayerCount() {
        return totalPlayerCount;
    }

    public void setTotalPlayerCount(int totalPlayerCount) {
        this.totalPlayerCount = totalPlayerCount;
    }

    public PlotInfo getPlotInfo() {
        return plotInfo;
    }

    public void setPlotInfo(PlotInfo plotInfo) {
        this.plotInfo = plotInfo;
    }
}
