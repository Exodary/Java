package softuniBlog.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import softuniBlog.entity.User;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.serviceInt.FileServiceInt;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService implements FileServiceInt {


    private final UserRepository userReposity;

    public FileService(UserRepository userReposity) {
        this.userReposity = userReposity;
    }

    @Override
    public void saveFile(User user, MultipartFile multipartFile) throws IOException {
        try {

            Byte[] byteObjects = new Byte[multipartFile.getBytes().length];

            int i = 0;

            for (byte b : multipartFile.getBytes()){
                byteObjects[i++] = b;
            }

            user.setPhoto(byteObjects);

            userReposity.save(user);
        } catch (IOException e) {
            //todo handle better
            System.out.println("Error occurred");

            e.printStackTrace();
        }
    }
}
