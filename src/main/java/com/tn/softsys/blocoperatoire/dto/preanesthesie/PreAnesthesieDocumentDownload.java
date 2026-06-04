package com.tn.softsys.blocoperatoire.dto.preanesthesie;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class PreAnesthesieDocumentDownload {

    private String fileName;
    private String mimeType;
    private long sizeBytes;
    private Resource resource;
}
