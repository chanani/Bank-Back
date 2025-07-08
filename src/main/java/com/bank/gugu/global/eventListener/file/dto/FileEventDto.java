package com.bank.gugu.global.eventListener.file.dto;

import com.bank.gugu.entity.records.Records;
import com.bank.gugu.entity.user.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record FileEventDto (
        List<MultipartFile> inputFiles,
        User user,
        Records records
){
}
