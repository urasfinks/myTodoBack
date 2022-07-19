package ru.jamsys.servlet;

import org.apache.commons.io.*;
import ru.jamsys.Util;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

@MultipartConfig
@WebServlet(name = "AvatarSet", value = "/avatar-set")
public class AvatarSet extends AbstractHttpServletReader {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String personKey = getPersonKey(request.getHeader("Authorization"));
        if(Util.isUUID(personKey)){
            try {
                Part filePart = request.getPart("avatar");
                String filename = filePart.getSubmittedFileName();
                InputStream inputStream = filePart.getInputStream();
                byte[] bytes = IOUtils.toByteArray(inputStream);
                Files.write(Paths.get("/var/www/jamsys/avatarImg/" + personKey + getFileExtension(filename)), bytes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(String name) {
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // empty extension
        }
        return name.substring(lastIndexOf);
    }
}
