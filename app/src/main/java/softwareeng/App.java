/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package softwareeng;

import java.sql.*;
import java.util.ResourceBundle;
import java.util.Scanner;

public class App {

    public static void main() throws Exception {

        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url"); // localhost:5432
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(url, username, password);

        System.out.println(
                "========================================================= WELCOME =========================================================");
        Scanner sc = new Scanner(System.in);
        while (true) {

            String role = "";

            Auth.sc = sc;
            role = Auth.main(con);

            if (role.equals("acad")) {
                AcadOffice.sc = sc;
                AcadOffice.main(con);
            } else if (role.equals("faculty")) {
                Faculty.main(con);
            } else if (role.equals("student")) {
                Student.main(con);
            } else if (role.equals("sp5098")) {
                break;
            } else {
                System.out.println("Logged out");
            }
        }

        System.out.println("Thank you for using our application");

    }
}
