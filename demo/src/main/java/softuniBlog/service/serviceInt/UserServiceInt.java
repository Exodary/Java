package softuniBlog.service.serviceInt;

import softuniBlog.entity.Role;
import softuniBlog.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserServiceInt {

    User findByEmail(String email);

    User saveAndFlushUserData(User user);

    User registerUser(String email, String fullName, String password);

    List<User> findAllUser();

    void deleteUser(User user);

    Optional<User> findUserById(Integer id);

    boolean passwordsAreEqual(String password, String confirmPassword);

    User setEncodedPassword(User user, String password);

   User editUser(User user, String email, String fullName, List<Integer> roles);

}
