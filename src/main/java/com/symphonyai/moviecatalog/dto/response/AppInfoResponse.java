package com.symphonyai.moviecatalog.dto.response;

public class AppInfoResponse {

    private String name;
    private String version;

    public AppInfoResponse() {
    }

    public AppInfoResponse(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
