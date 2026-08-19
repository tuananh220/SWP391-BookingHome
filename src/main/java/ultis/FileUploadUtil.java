/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ultis;

/**
 *
 * @author Admin
 */


import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class FileUploadUtil {
    private static final int MAX_IMAGES = 5;
    private static final long MAX_FILE_SIZE = 5L * 1024L * 1024L;

    private FileUploadUtil() {
    }

    public static List<String> saveReviewImages(
            Collection<Part> parts, ServletContext servletContext
    ) throws IOException {
        List<String> imageUrls = new ArrayList<String>();
        String realFolder = servletContext.getRealPath("/uploads/reviews");
        if (realFolder == null) {
            throw new IOException("Không xác định được thư mục upload.");
        }

        Path uploadFolder = Paths.get(realFolder);
        Files.createDirectories(uploadFolder);

        for (Part part : parts) {
            if (!"images".equals(part.getName()) || part.getSize() == 0) {
                continue;
            }
            if (imageUrls.size() >= MAX_IMAGES) {
                throw new IllegalArgumentException(
                        "Chỉ được tải lên tối đa 5 ảnh."
                );
            }
            if (part.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException(
                        "Mỗi ảnh không được vượt quá 5 MB."
                );
            }

            String contentType = part.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException(
                        "Tệp tải lên phải là hình ảnh."
                );
            }

            String extension = getExtension(part.getSubmittedFileName());
            if (!isAllowedExtension(extension)) {
                throw new IllegalArgumentException(
                        "Chỉ chấp nhận ảnh JPG, JPEG, PNG hoặc WEBP."
                );
            }

            String fileName = UUID.randomUUID().toString() + extension;
            Path target = uploadFolder.resolve(fileName);
            try (InputStream inputStream = part.getInputStream()) {
                Files.copy(
                        inputStream,
                        target,
                        StandardCopyOption.REPLACE_EXISTING
                );
            }
            imageUrls.add("uploads/reviews/" + fileName);
        }
        return imageUrls;
    }

    private static String getExtension(String fileName) {
        if (fileName == null) {
            return "";
        }
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) {
            return "";
        }
        return fileName.substring(dotIndex).toLowerCase(Locale.ROOT);
    }

    private static boolean isAllowedExtension(String extension) {
        return ".jpg".equals(extension)
                || ".jpeg".equals(extension)
                || ".png".equals(extension)
                || ".webp".equals(extension);
    }
}

