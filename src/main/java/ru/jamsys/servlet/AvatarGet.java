package ru.jamsys.servlet;

import javax.imageio.ImageIO;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@WebServlet(name = "AvatarGet", value = "/avatar-get/*")
public class AvatarGet extends AbstractHttpServletReader{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] req = parseFullUrl(request);
        if(isUUID(req[0])){
            System.out.println("/var/www/jamsys/avatarImg/"+req[0]+".jpg");
            File file = new File("/var/www/jamsys/avatarImg/"+req[0]+".jpg");
            BufferedImage image = ImageIO.read(file);
            ImageIO.write(image, "JPG", response.getOutputStream());
        }
    }
}
