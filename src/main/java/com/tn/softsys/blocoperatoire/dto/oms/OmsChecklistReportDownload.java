package com.tn.softsys.blocoperatoire.dto.oms;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OmsChecklistReportDownload {

    private String fileName;
    private byte[] content;
}
