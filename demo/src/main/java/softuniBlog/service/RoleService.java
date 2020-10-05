package softuniBlog.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuniBlog.entity.Role;
import softuniBlog.repository.RoleRepository;
import softuniBlog.service.serviceInt.RoleServiceInt;

import javax.swing.text.html.Option;
import java.awt.font.OpenType;
import java.util.List;
import java.util.Optional;

@Service
public class RoleService implements RoleServiceInt {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByName(String string) {
        return this.roleRepository.findByName(string);
    }

    @Override
    public List<Role> findAllRoles() {
        return this.roleRepository.findAll();
    }

    @Override
    public Optional<Role> findRoleById(Integer id) {
        return this.roleRepository.findById(id);
    }


}
