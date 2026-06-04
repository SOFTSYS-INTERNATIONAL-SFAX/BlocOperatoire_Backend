package com.tn.softsys.blocoperatoire.dto.preanesthesie;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreAnesthesieReportDownload {

    private String fileName;
    private byte[] content;
}
