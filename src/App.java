
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import UserInterface.GUI.Login;
import javafx.application.Application;

public class App {
    public static void main(String[] args) throws Exception {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashed = encoder.encode("1234");
        System.out.println("Contraseña encriptada" + hashed);
        Application.launch(Login.class, args);

    }
}           
