package softuniBlog.controller;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import softuniBlog.bindingModel.UserBindingModel;
import softuniBlog.entity.ConfirmationToken;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class UserController {

    private final RoleService roleService;

    private final UserService userService;

    private final ConfirmationTokenService confirmationTokenService;

    private final EmailSenderService emailSenderService;

    private final FileService fileService;

    @Autowired
    public UserController(RoleService roleService, UserService userService, ConfirmationTokenService confirmationTokenService,
                          EmailSenderService emailSenderService, FileService fileService) {
        this.roleService = roleService;
        this.userService = userService;
        this.confirmationTokenService = confirmationTokenService;
        this.emailSenderService = emailSenderService;
        this.fileService = fileService;
    }

    @GetMapping("/register")
    public String register(Model model){
        model.addAttribute("view", "user/register");

        return "base-layout";
    }

    @PostMapping("/register")
    public String registerProcess(UserBindingModel userBindingModel,
                                  @RequestParam("fileImage") MultipartFile multipartFile) throws IOException  {

        if(!userBindingModel.getPassword().equals(userBindingModel.getConfirmPassword())){
            return "redirect:/register";
        }

        User user = this.userService.registerUser(userBindingModel.getEmail(),
                userBindingModel.getFullName(), userBindingModel.getPassword());

        fileService.saveFile(user, multipartFile);

        System.out.println(multipartFile.getSize());

        this.userService.saveAndFlushUserData(user);

        return "redirect:/login";
    }

    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("view", "user/login");

        return "base-layout";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        return "redirect:/login?logout";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profilePage(Model model, HttpServletResponse response) throws  IOException{
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userService.findByEmail(principal.getUsername());

        model.addAttribute("user", user);
        model.addAttribute("view", "user/profile");

        return "base-layout";
    }

    @RequestMapping(value="/forgot-password", method=RequestMethod.GET)
    public String displayResetPassword(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("view", "user/forgotPassword");

        return "base-layout";
    }

    @RequestMapping(value="/forgot-password", method=RequestMethod.POST)
    public String forgotUserPassword(Model model, User user) {
        User existingUser = userService.findByEmail(user.getEmail());
        if(existingUser != null) {
            ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);

            confirmationTokenService.save(confirmationToken);

            SimpleMailMessage mailMessage = emailSenderService.createEmail(existingUser, confirmationToken);

            emailSenderService.sendEmail(mailMessage);

            model.addAttribute("message", "Request to reset password received. Check your inbox for the reset link.");
            model.addAttribute("view", "user/SuccessForgotPassword");

        } else {
            model.addAttribute("message", "This email does not exist!");
            model.addAttribute("view", "user/forgotPassword");
        }

        return "base-layout";
    }

    @RequestMapping(value="/confirm-reset", method= {RequestMethod.GET, RequestMethod.POST})
    public String validateResetToken(Model model, @RequestParam("token")String confirmationToken)
    {
        ConfirmationToken token = confirmationTokenService.findByConfirmationToken(confirmationToken);

        if(token != null) {
            User user = userService.findByEmail(token.getUser().getEmail());
            userService.saveAndFlushUserData(user);
            model.addAttribute("user", user);
            model.addAttribute("emailId", user.getEmail());
            model.addAttribute("view", "user/resetPassword");
        }
        else{
            model.addAttribute("message", "The link is invalid or broken!");
            model.addAttribute("view","error/error");
        }
        return "base-layout";
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.POST)
    public String resetUserPassword(Model model, User user) {

        if (user.getEmail() != null) {

            User tokenUser = userService.findByEmail(user.getEmail());
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            tokenUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

            userService.saveAndFlushUserData(tokenUser);
            model.addAttribute("message", "Password successfully reset. You can now log in with the new credentials.");
            model.addAttribute("view", "user/successResetPassword");
        } else {
            model.addAttribute("message", "The link is invalid or broken!");
            model.addAttribute("view", "error/error");
        }

        return "base-layout";
    }
}


