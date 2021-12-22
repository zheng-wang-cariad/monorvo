package film.monorvo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Compressor {
    public static void extract(File destDir, InputStream inputStream){
        try {
            var is = new ZipInputStream(inputStream);
            var zipEntry = is.getNextEntry();
            byte[] buffer = new byte[1024];
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                if (zipEntry.isDirectory()) {
                    if (!newFile.isDirectory() && !newFile.mkdirs()) {
                        throw new RuntimeException("Failed to create directory " + newFile);
                    }
                } else {
                    // fix for Windows-created archives
                    var fileName= newFile.getName().toLowerCase();
                    if (! fileName.startsWith(".") && (fileName.endsWith(".jpg") || fileName.endsWith("png") || fileName.endsWith("jpeg"))) {
                        newFile = new File(destDir, fileName);
                    } else {
                        File parent = newFile.getParentFile();
                        if (!parent.isDirectory() && !parent.mkdirs()) {
                            throw new RuntimeException("Failed to create directory " + parent);
                        }
                    }

                    // write file content
                    System.out.println("Write file: " + newFile.getAbsolutePath());

                    FileOutputStream fos = new FileOutputStream(newFile);
                    int len;
                    while ((len = is.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                }
                zipEntry = is.getNextEntry();
            }
        }catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }
        return destFile;
    }
}
