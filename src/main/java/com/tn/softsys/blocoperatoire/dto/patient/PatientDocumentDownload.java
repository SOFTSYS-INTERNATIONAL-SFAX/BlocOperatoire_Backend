package com.tn.softsys.blocoperatoire.dto.patient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class PatientDocumentDownload {

    private String fileName;
    private String mimeType;
    private long sizeBytes;
    private Resource resource;
}
