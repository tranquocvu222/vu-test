package com.nals.auction.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.nals.auction.config.AmazonProperties;
import com.nals.auction.config.ApplicationProperties;
import com.nals.auction.exception.ExceptionHandler;
import com.nals.common.messages.errors.ValidatorException;
import com.nals.utils.helpers.FileHelper;
import com.nals.utils.helpers.RandomHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

import static com.nals.auction.exception.ExceptionHandler.COPY_FAILED;
import static com.nals.auction.exception.ExceptionHandler.DELETE_FAILED;
import static com.nals.auction.exception.ExceptionHandler.DOWNLOAD_FAILED;
import static com.nals.auction.exception.ExceptionHandler.INVALID_EXTENSION;
import static com.nals.auction.exception.ExceptionHandler.INVALID_SIZE;
import static com.nals.auction.exception.ExceptionHandler.NOT_FOUND;
import static com.nals.auction.exception.ExceptionHandler.UPLOAD_FAILED;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Slf4j
@Service
public class StorageService {

    private static final String FILE_NAME_PATTERN = "%s.%s";

    private final AmazonS3 amazonS3;
    private final String bucketName;
    private final String workingDir;
    private final String tempDir;
    private final ExceptionHandler exceptionHandler;
    private final Set<String> allowExtensions;
    private final int maxSizeAllow;
    private final String s3Url;

    public StorageService(final AmazonS3 amazonS3,
                          final AmazonProperties amazonProperties,
                          final ApplicationProperties applicationProperties,
                          final ExceptionHandler exceptionHandler) {
        ApplicationProperties.FileUpload fileUpload = applicationProperties.getFileUpload();
        this.amazonS3 = amazonS3;
        this.exceptionHandler = exceptionHandler;
        allowExtensions = fileUpload.getAllowExtensions();
        maxSizeAllow = fileUpload.getMaxSizeAllow();
        bucketName = amazonProperties.getS3().getBucketName();
        workingDir = amazonProperties.getS3().getWorkingDir();
        tempDir = amazonProperties.getS3().getTempDir();
        s3Url = amazonProperties.getS3().getS3Url();
    }

    public void validateFile(final MultipartFile uploadFile)
        throws IOException {
        if (Objects.isNull(uploadFile)) {
            throw new ValidatorException("file",
                                         exceptionHandler.getMessageCode(NOT_FOUND),
                                         exceptionHandler.getMessageContent(NOT_FOUND));
        }

        validateExtension(uploadFile.getOriginalFilename(), allowExtensions);

        if (uploadFile.isEmpty() || uploadFile.getBytes().length > maxSizeAllow * 1024) {
            throw new ValidatorException("file",
                                         exceptionHandler.getMessageCode(INVALID_SIZE),
                                         exceptionHandler.getMessageContent(INVALID_SIZE));
        }
    }

    private void validateExtension(final String fileName, final Set<String> extensions) {
        String fileExtension = FileHelper.getExtension(fileName);
        if (extensions.stream().noneMatch(s -> s.equalsIgnoreCase(fileExtension))) {
            throw new ValidatorException("file",
                                         exceptionHandler.getMessageCode(INVALID_EXTENSION),
                                         exceptionHandler.getMessageContent(INVALID_EXTENSION));
        }
    }

    /**
     * Download an S3 object.
     *
     * @param uri the object uri
     * @return an input stream which must be closed as soon as possible
     */
    public InputStream downloadFile(final String uri) {
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, uri);
            return s3Object.getObjectContent();
        } catch (Exception exception) {
            throw new ValidatorException("file",
                                         exceptionHandler.getMessageCode(DOWNLOAD_FAILED),
                                         exceptionHandler.getMessageContent(DOWNLOAD_FAILED));
        }
    }

    /**
     * Upload an object into S3.
     *
     * @param uploadFile file to be uploaded
     * @return the uploaded object meta data
     */
    public String uploadFile(final MultipartFile uploadFile)
        throws IOException {
        validateFile(uploadFile);

        var file = FileHelper.convertMultipartToFile(uploadFile);
        var fileName = String.format(FILE_NAME_PATTERN,
                                     RandomHelper.randomString(10),
                                     FileHelper.getExtension(uploadFile.getOriginalFilename()));
        var key = makeObjectRequestKey(tempDir, fileName);

        try {
            amazonS3.putObject(bucketName, key, file);
        } catch (Exception exception) {
            throw new ValidatorException("file",
                                         exceptionHandler.getMessageCode(UPLOAD_FAILED),
                                         exceptionHandler.getMessageContent(UPLOAD_FAILED));
        }
        return fileName;
    }

    public String getFullFileUrl(final String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return EMPTY;
        }

        if (fileName.startsWith("http")) {
            return fileName;
        }

        return FileHelper.concatPath(s3Url, "/", workingDir, fileName.trim());
    }

    public void saveFile(final String fileName) {
        if (invalidFileName(fileName)) {
            return;
        }

        validateExtension(fileName, allowExtensions);
        moveFile(tempDir, workingDir, fileName);
    }

    public void saveFiles(final Collection<String> fileNames) {
        if (CollectionUtils.isEmpty(fileNames)) {
            return;
        }
        fileNames.forEach(this::saveFile);
    }

    public void replaceFile(final String fileName, final String oldFileName) {
        log.debug("Replace file #{} to #{}", oldFileName, fileName);

        if (Objects.equals(fileName, oldFileName) || invalidFileName(fileName)) {
            return;
        }

        if (invalidFileName(fileName)) {
            return;
        }

        saveFile(fileName);
        deleteFile(oldFileName);
    }

    public void deleteFile(final String fileName) {
        log.debug("Delete file #{}", fileName);
        if (invalidFileName(fileName)) {
            return;
        }

        deleteFile(workingDir, fileName);
    }

    private boolean invalidFileName(final String fileName) {
        return StringUtils.isEmpty(fileName) || fileName.startsWith("http");
    }

    private String makeObjectRequestKey(final String dir, final String fileName) {
        var key = StringUtils.isEmpty(dir) ? fileName : FileHelper.concatPath(dir, fileName);
        return key.startsWith("/") ? key.substring(1) : key;
    }

    private void moveFile(final String sourceDir, final String destinationDir, final String fileName) {
        // Copy file from temp dir to working dir
        copyFile(sourceDir, fileName, destinationDir, fileName);

        // Delete temp file after copied to working dir
        deleteFile(tempDir, fileName);
    }

    private void copyFile(final String sourceDir, final String originalPath,
                          final String targetDir, final String destinationPath) {

        try {
            String sourceKey = makeObjectRequestKey(sourceDir, originalPath);
            String destinationKey = makeObjectRequestKey(targetDir, destinationPath);

            var request = new CopyObjectRequest(bucketName, sourceKey, bucketName, destinationKey);
            amazonS3.copyObject(request);
        } catch (Exception e) {
            throw new ValidatorException("file",
                                         exceptionHandler.getMessageCode(COPY_FAILED),
                                         exceptionHandler.getMessageContent(COPY_FAILED));
        }
    }

    private void deleteFile(final String dir, final String fileName) {
        try {
            var key = makeObjectRequestKey(dir, fileName);
            var deleteObjectRequest = new DeleteObjectRequest(bucketName, key);

            amazonS3.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new ValidatorException("file",
                                         exceptionHandler.getMessageCode(DELETE_FAILED),
                                         exceptionHandler.getMessageContent(DELETE_FAILED));
        }
    }
}
