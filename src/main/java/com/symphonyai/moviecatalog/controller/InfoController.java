package com.symphonyai.moviecatalog.controller;

import com.symphonyai.moviecatalog.dto.response.AppInfoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/info")
@Tag(name = "Application Info", description = "Application metadata and versioning")
public class InfoController {

    private final String appName;
    private final String appVersion;

    public InfoController(@Value("${app.name}") String appName,
                          @Value("${app.version}") String appVersion) {
        this.appName = appName;
        this.appVersion = appVersion;
    }

    @GetMapping
    @Operation(summary = "Get application name and version metadata")
    public ResponseEntity<AppInfoResponse> getAppInfo() {
        return ResponseEntity.ok(new AppInfoResponse(appName, appVersion));
    }
}
