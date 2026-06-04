package com.tn.softsys.blocoperatoire.dto.compterendu;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.io.Resource;

@Getter
@AllArgsConstructor
public class CompteRenduAudioDownload {
    private String fileName;
    private String mimeType;
    private Long sizeBytes;
    private Resource resource;
}
