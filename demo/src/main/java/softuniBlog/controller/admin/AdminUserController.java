package softuniBlog.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import softuniBlog.bindingModel.UserEditBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.service.ArticleService;
import softuniBlog.service.RoleService;
import softuniBlog.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final UserService userService;

    private final ArticleService articleService;

    private final RoleService roleService;

    @Autowired
    public AdminUserController(UserService userService, ArticleService articleService, RoleService roleService) {
        this.userService = userService;
        this.articleService = articleService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String listUsers(Model model){
        List<User> users = this.userService.findAllUser();

        model.addAttribute("users", users);
        model.addAttribute("view", "admin/user/list");

        return "base-layout";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model){
        if(this.userService.findUserById(id).orElse(null) == null){
            return "redirect:/admin/users/";
        }

        User user = this.userService.findUserById(id).orElse(null);
        List<Role> roles = this.roleService.findAllRoles();

        model.addAttribute("user", user);
        model.addAttribute("roles", roles);
        model.addAttribute("view", "admin/user/edit");

        return "base-layout";
    }

    @PostMapping("/edit/{id}")
    public String editProcess(@PathVariable Integer id, UserEditBindingModel userBindingModel){

        if(this.userService.findUserById(id).orElse(null) == null){
            return "redirect:/admin/users/";
        }

        User user = this.userService.findUserById(id).orElse(null);

        if(!StringUtils.isEmpty(userBindingModel.getPassword())
        && !StringUtils.isEmpty(userBindingModel.getConfirmPassword())){

            if(userService.passwordsAreEqual(userBindingModel.getPassword(), userBindingModel.getConfirmPassword())){
                this.userService.setEncodedPassword(user, userBindingModel.getPassword());
            }
            else{
                return "redirect:/admin/users/";
            }
        }

        this.userService.editUser(user, userBindingModel.getEmail(), userBindingModel.getFullName(),
                userBindingModel.getRoles());

        this.userService.saveAndFlushUserData(user);

        return "redirect:/admin/users/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id, Model model){
        if(this.userService.findUserById(id).orElse(null) == null){
            return "redirect:/admin/users/";
        }

        User user = this.userService.findUserById(id).orElse(null);

        model.addAttribute("user", user);
        model.addAttribute("view", "admin/user/delete");

        return "base-layout";
    }

    @PostMapping("/delete/{id}")
    public String deleteProcess(@PathVariable Integer id){
        if(this.userService.findUserById(id).orElse(null) == null){
            return "redirect:/admin/users/";
        }

        User user = this.userService.findUserById(id).orElse(null);

       this.articleService.deleteAllArticlesForUser(user);

        this.userService.deleteUser(user);

        return "redirect:/admin/users/";
    }
}
