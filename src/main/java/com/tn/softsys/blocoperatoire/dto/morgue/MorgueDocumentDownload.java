package com.tn.softsys.blocoperatoire.dto.morgue;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class MorgueDocumentDownload {

    private String fileName;
    private String mimeType;
    private long sizeBytes;
    private Resource resource;
}
