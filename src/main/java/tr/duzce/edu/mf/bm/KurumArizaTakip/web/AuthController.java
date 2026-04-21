package tr.duzce.edu.mf.bm.KurumArizaTakip.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tr.duzce.edu.mf.bm.KurumArizaTakip.service.AuthService;

@Controller
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@RequestParam("firstName") String firstName,
                           @RequestParam("lastName") String lastName,
                           @RequestParam("email") String email,
                           @RequestParam("password") String password,
                           Model model) {
        try {
            authService.register(firstName, lastName, email, password);
            model.addAttribute("email", email);
            model.addAttribute("info", "Doğrulama kodu email adresinize gönderildi.");
            return "auth/verify";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            return "auth/register";
        }
    }

    @GetMapping("/verify")
    public String verifyPage(@RequestParam(value = "email", required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "auth/verify";
    }

    @PostMapping("/verify")
    public String verify(@RequestParam("email") String email,
                         @RequestParam("code") String code,
                         Model model) {
        try {
            authService.verifyEmail(email, code);
            model.addAttribute("info", "Email doğrulandı. Giriş yapabilirsiniz.");
            model.addAttribute("email", email);
            return "auth/login";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("email", email);
            return "auth/verify";
        }
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "email", required = false) String email, Model model) {
        model.addAttribute("email", email);
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam("email") String email,
                        @RequestParam("password") String password,
                        HttpSession session,
                        Model model) {
        try {
            var user = authService.authenticate(email, password);
            SessionAuth.setUserId(session, user.getId());
            return "redirect:/tickets";
        } catch (RuntimeException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("email", email);
            return "auth/login";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        SessionAuth.clear(session);
        return "redirect:/";
    }
}
