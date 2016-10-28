package com.vhly.upsys;

import java.io.DataInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: xhzhang
 * Date: 13-11-8
 */
public class AppVersion {
    private DeviceOS os;
    private ScreenType screenType;
    private NetworkType networkType;

    /**
     * 软件唯一ID, 任何一个应用程序，包括 各个版本，都是一个 ID
     */
    private String appId;
    /**
     * 当前软件版本号
     */
    private String version;
    /**
     * 软件市场ID，由服务器控制
     */
    private String marketId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getMarketId() {
        return marketId;
    }

    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public DeviceOS getOs() {
        return os;
    }

    public void setOs(DeviceOS os) {
        this.os = os;
    }

    public ScreenType getScreenType() {
        return screenType;
    }

    public void setScreenType(ScreenType screenType) {
        this.screenType = screenType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void read(DataInputStream din){

    }
}
