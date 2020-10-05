package softuniBlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.serviceInt.UserServiceInt;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService implements UserServiceInt {

    private final UserRepository userRepository;

    private final RoleService roleService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleService roleService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public User findByEmail(String email) {
        return this.userRepository.findByEmail(email);
    }

    @Override
    public User saveAndFlushUserData(User user) {
       return this.userRepository.saveAndFlush(user);
    }

    @Override
    public User registerUser(String email, String fullName, String password) {

        String encodedPassword = bCryptPasswordEncoder.encode(password);

        Role userRole = this.roleService.findByName("ROLE_USER");

        User user = new User(email, fullName, encodedPassword);

        user.addRole(userRole);

        return user;
    }

    @Override
    public List<User> findAllUser() {
        return this.userRepository.findAll();
    }

    @Override
    public void deleteUser(User user) {
         this.userRepository.delete(user);
    }

    @Override
    public Optional<User> findUserById(Integer id) {
        return this.userRepository.findById(id);
    }

    @Override
    public boolean passwordsAreEqual(String password, String confirmPassword) {
        if(password.equals(confirmPassword)){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public User setEncodedPassword(User user, String password) {
        user.setPassword(bCryptPasswordEncoder.encode(password));

        return user;
    }

    @Override
    public User editUser(User user, String email, String fullName, List<Integer> rolesId) {

       user.setFullName(fullName);
       user.setEmail(email);

       Set<Role> roles = new HashSet<>();

       for(Integer roleId : rolesId){
           roles.add(this.roleService.findRoleById(roleId).orElse(null));
       }

       user.setRoles(roles);

       return user;
    }

}
