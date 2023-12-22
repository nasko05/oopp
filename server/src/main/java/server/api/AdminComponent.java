package server.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:application.properties")
class AdminComponent {
    @Value("${admin.password}")
    private String password;

    /**
     * Getter for password
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter
     * @param password new password
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
