package com.hdu.neurostudent_signalflow.entity;

import lombok.Data;


public class GazepointData extends Signal{
    public String cnt;
    public String fpogx;
    public String fpogy;
    public String fpogs;
    public String fpogd;
    public String fpogid;
    public String fpogv ;
    public String lpogx;
    public String lpogy;
    public String lpogv;
    public String rpogx;
    public String rpogy;
    public String rpogv;

    public GazepointData(String deviceId, String protocolV, String smapeRate, String channels, String type) {
        super(deviceId, protocolV, smapeRate, channels, type);
    }
}
