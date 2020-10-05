package softuniBlog.service.serviceInt;

import org.springframework.web.multipart.MultipartFile;
import softuniBlog.entity.User;

import java.io.IOException;

public interface FileServiceInt {

    void saveFile(User user, MultipartFile multipartFile) throws IOException;
}
