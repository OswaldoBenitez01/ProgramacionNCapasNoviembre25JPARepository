package OBenitez.ProgramacionNCapasNoviembre25.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(HttpServletRequest request,
                        @RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {

        if (logout != null) {
            model.addAttribute("logoutMessage", "Sesión cerrada correctamente.");
            return "login";
        }

        if ("forbidden".equals(error)) {
            model.addAttribute("errorType", "forbidden");
            model.addAttribute("message", "No tienes permisos para acceder a ese recurso.");
            return "login";
        }

        if (error != null) {
            model.addAttribute("errorType", "bad_credentials");
            model.addAttribute("message", "Usuario o contraseña incorrectos. Verifica tus datos.");
            return "login";
        }

        String sessionStatus = request.getParameter("session");
        if (sessionStatus != null) {
            if ("expired".equals(sessionStatus)) {
                model.addAttribute("errorType", "session_expired");
                model.addAttribute("message", "Tu sesión ha expirado. Inicia sesión nuevamente.");
            } else if ("invalid".equals(sessionStatus)) {
                model.addAttribute("errorType", "session_invalid");
                model.addAttribute("message", "Tu sesión no es válida. Por favor vuelve a iniciar sesión.");
            }
            return "login";
        }

        return "login";
    }

}
