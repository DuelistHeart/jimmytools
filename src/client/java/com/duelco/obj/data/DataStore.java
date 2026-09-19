package com.duelco.obj.data;

import com.duelco.obj.general.TabData;

public class DataStore {
    public TabData tabData;

    public DataStore() {
        this.tabData = new TabData();
    }

    public TabData getTabData() {
        return tabData;
    }
}
