package com.nals.auction.api.v1;

import com.nals.auction.dto.response.FileUploadRes;
import com.nals.auction.service.StorageService;
import com.nals.utils.controller.BaseController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Validator;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/files")
public class FileController
    extends BaseController {

    private final StorageService storageService;

    public FileController(final Validator validator,
                          final StorageService storageService) {
        super(validator);
        this.storageService = storageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") final MultipartFile uploadFile)
        throws IOException {

        return ok(new FileUploadRes(storageService.uploadFile(uploadFile)));
    }
}
