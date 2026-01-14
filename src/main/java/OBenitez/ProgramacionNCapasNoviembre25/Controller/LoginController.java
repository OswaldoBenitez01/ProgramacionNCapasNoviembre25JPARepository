package OBenitez.ProgramacionNCapasNoviembre25.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

public class LoginController {

    @RequestMapping("/login")
    public String login(@RequestParam(value = "logout", required = false) String logout){
        return "login";
    }
}
