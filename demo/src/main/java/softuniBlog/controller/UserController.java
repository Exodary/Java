package softuniBlog.controller;

import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import softuniBlog.Utils.FileUploadUtil;
import softuniBlog.bindingModel.UserBindingModel;
import softuniBlog.entity.Article;
import softuniBlog.entity.ConfirmationToken;
import softuniBlog.entity.Role;
import softuniBlog.entity.User;
import softuniBlog.repository.ArticleRepository;
import softuniBlog.repository.ConfirmationTokenRepository;
import softuniBlog.repository.RoleRepository;
import softuniBlog.repository.UserRepository;
import softuniBlog.service.EmailSenderService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Controller
public class UserController {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ArticleRepository articleRepository;

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;

    @Autowired
    EmailSenderService emailSenderService;

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

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        User user = new User(
                userBindingModel.getEmail(),
                userBindingModel.getFullName(),
                bCryptPasswordEncoder.encode(userBindingModel.getPassword())
        );

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        user.setPhoto(fileName);

        String uploadDir = "user-photos/" + user.getFullName();

        Path uploadPath = Paths.get(uploadDir);

        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);

        try{
            InputStream inputStream = multipartFile.getInputStream();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e){

            throw new IOException("Couldn't save uploaded file: " + fileName);
        }


        Role userRole = this.roleRepository.findByName("ROLE_USER");

        user.addRole(userRole);

        this.userRepository.saveAndFlush(user);

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
    public String profilePage(Model model){
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        User user = this.userRepository.findByEmail(principal.getUsername());

        List<Article> articles = this.articleRepository.findAll();

        model.addAttribute("user", user);
        model.addAttribute("view", "user/profile");

        return "base-layout";
    }

    /**
     * Display the forgot password page and form
     */

    @RequestMapping(value="/forgot-password", method=RequestMethod.GET)
    public String displayResetPassword(Model model, User user) {
        model.addAttribute("user", user);
        model.addAttribute("view", "user/forgotPassword");

        return "base-layout";
    }

    @RequestMapping(value="/forgot-password", method=RequestMethod.POST)
    public String forgotUserPassword(Model model, User user) {
        User existingUser = userRepository.findByEmail(user.getEmail());
        if(existingUser != null) {
            // create token
            ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);

            // save it
            confirmationTokenRepository.save(confirmationToken);

            // create the email
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(existingUser.getEmail());
            mailMessage.setSubject("Complete Password Reset!");
            mailMessage.setFrom("Satagon123@mail.bg");
            mailMessage.setText("To complete the password reset process, please click here: "
                    +"http://localhost:8080/confirm-reset?token="+confirmationToken.getConfirmationToken());

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
        ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);

        if(token != null) {
            User user = userRepository.findByEmail(token.getUser().getEmail());
            userRepository.save(user);
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
            // use email to find user
            User tokenUser = userRepository.findByEmail(user.getEmail());
            BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
            tokenUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            // System.out.println(tokenUser.getPassword());
            userRepository.save(tokenUser);
            model.addAttribute("message", "Password successfully reset. You can now log in with the new credentials.");
            model.addAttribute("view", "user/successResetPassword");
        } else {
            model.addAttribute("message", "The link is invalid or broken!");
            model.addAttribute("view", "error/error");
        }

        return "base-layout";
    }
}


