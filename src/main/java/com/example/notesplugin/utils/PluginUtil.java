package com.example.notesplugin.utils;

import com.example.notesplugin.perf.ActionR;
import com.example.notesplugin.perf.Perf;
import com.example.notesplugin.perf.Response;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class PluginUtil {

    public static String calculateMD5(@Nullable VirtualFile file)  {
        if (file == null) {
            return "1";
        }
        MessageDigest md  = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        MessageDigest mdFinal = md;
        ActionR<StringBuilder> action = () -> {
            try (InputStream fis = file.getInputStream();
                 DigestInputStream dis = new DigestInputStream(fis, mdFinal)) {
                byte[] buffer = new byte[8192];
                while (dis.read(buffer) != -1) {
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            byte[] hashBytes = mdFinal.digest();
            StringBuilder result1 = new StringBuilder();
            for (byte hashByte : hashBytes) {
                result1.append(String.format("%02x", hashByte));
            }

            return new Response<>("calculateMD5 for [" + file.getName() + "] ", result1);
        };

        StringBuilder result = Perf.perf(null , action);

        return result.toString();
    }

    public static List<Integer> offsetsToLines(List<Integer> offsets , Editor editor) {
        return offsets.stream().map(o -> {
                    int visualLineBeforeSoft_false = editor.offsetToVisualLine(o, false);
                    int visualLineBeforeSoft_true = editor.offsetToVisualLine(o, true);
                    int visualLine_VisualPosition_line = editor.offsetToVisualPosition(o).getLine();
                    int visualLine_LogicalPosition_line = editor.offsetToLogicalPosition(o).line;

                    //System.out.println("" +
//                            "visualLineBeforeSoft_false " +             (visualLineBeforeSoft_false        ) +   "\n" +
//                            "visualLineBeforeSoft_true " +              (visualLineBeforeSoft_true         ) +   "\n" +
//                            "visualLine_VisualPosition_line " +         (visualLine_VisualPosition_line    ) +   "\n" +
//                            "visualLine_LogicalPosition_line " +        (visualLine_LogicalPosition_line   ) +   "\n" +
//                           "");
                    return visualLineBeforeSoft_true  ;
                })
                .collect(Collectors.toList());
    }

    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }

    public static String getFileUrl(String filePath) {
        String systemIndependentPath = FileUtil.toSystemIndependentName(filePath);
        String protocol = "file";
        if (systemIndependentPath.indexOf(".zip!") > 0 || systemIndependentPath.indexOf(".jar!") > 0) {
            protocol = JarFileSystem.PROTOCOL;
        }

        return VirtualFileManager.constructUrl(protocol, (systemIndependentPath));
    }

    public static Document getDocumentFromUrl(String url) {
        VirtualFilePointer myFilePointer = VirtualFilePointerManager.getInstance().create(url, () -> { }, null);
        VirtualFile virtualFile = myFilePointer.getFile();

        Document document = ApplicationManager.getApplication().runReadAction((Computable<Document>) () -> {
            if (virtualFile == null) {
                return null;
            }
            return FileDocumentManager.getInstance().getDocument(virtualFile);
        });
        return document;
    }

    public static VirtualFile getVirtualFileFromUrl(String url) {
        VirtualFilePointer myFilePointer = VirtualFilePointerManager.getInstance().create(url, () -> { }, null);
        VirtualFile virtualFile = myFilePointer.getFile();

        return virtualFile;
    }

    public static Document getDocumentFromVF(VirtualFile virtualFile) {
        Document document = ApplicationManager.getApplication().runReadAction((Computable<Document>) () -> {
            if (virtualFile == null) {
                return null;
            }
            return FileDocumentManager.getInstance().getDocument(virtualFile);
        });
        return document;
    }

}
