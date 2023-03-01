package com.stock.quant.service.Util;

import org.w3c.dom.Document;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class CommonUtils {

    public static void unZip(String ZipFilePath, String FilePath) {
        File Destination_Directory = new File(FilePath);
        if (!Destination_Directory.exists()) {
            Destination_Directory.mkdir();
        }
        try {

            ZipInputStream Zip_Input_Stream = new ZipInputStream(new FileInputStream(ZipFilePath));
            ZipEntry Zip_Entry = Zip_Input_Stream.getNextEntry();

            while (Zip_Entry != null) {
                String File_Path = FilePath + File.separator + Zip_Entry.getName();
                if (!Zip_Entry.isDirectory()) {

                    extractFile(Zip_Input_Stream, File_Path);
                } else {

                    File directory = new File(File_Path);
                    directory.mkdirs();
                }
                Zip_Input_Stream.closeEntry();
                Zip_Entry = Zip_Input_Stream.getNextEntry();
            }
            Zip_Input_Stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void extractFile(ZipInputStream Zip_Input_Stream, String File_Path) throws IOException {
        int BUFFER_SIZE = 4096;

        BufferedOutputStream Buffered_Output_Stream = new BufferedOutputStream(new FileOutputStream(File_Path));
        byte[] Bytes = new byte[BUFFER_SIZE];
        int Read_Byte = 0;
        while ((Read_Byte = Zip_Input_Stream.read(Bytes)) != -1) {
            Buffered_Output_Stream.write(Bytes, 0, Read_Byte);
        }
        Buffered_Output_Stream.close();
    }
}
