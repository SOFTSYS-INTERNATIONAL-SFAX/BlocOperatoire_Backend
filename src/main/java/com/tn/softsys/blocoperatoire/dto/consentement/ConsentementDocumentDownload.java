package com.tn.softsys.blocoperatoire.dto.consentement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class ConsentementDocumentDownload {

    private String fileName;
    private String mimeType;
    private long sizeBytes;
    private Resource resource;
}
