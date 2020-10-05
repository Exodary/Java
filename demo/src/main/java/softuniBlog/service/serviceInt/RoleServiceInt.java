package softuniBlog.service.serviceInt;

import softuniBlog.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleServiceInt {

    Role findByName(String string);

    List<Role> findAllRoles();

    Optional<Role> findRoleById(Integer id);
}
