package com.dynatrace.storage.controller;

import com.dynatrace.storage.model.Version;
import com.dynatrace.storage.repository.StorageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/version")
public class VersionController {
    @Autowired
    private StorageRepository storageRepository;
    @Value("${service.version}")
    private String svcVer;
    @Value("${service.date}")
    private String svcDate;

    @GetMapping("")
    public Version getVersion() {
        return new Version("storage", svcVer, svcDate, "OK", "Count: " + storageRepository.count());
    }
}
