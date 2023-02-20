package softwareeng;

import java.sql.*;
import java.util.Formatter;
import java.util.ResourceBundle;
import java.util.Scanner;

public class AcadOffice {

    public static void main() throws Exception {

        while (true) {

            System.out.println("\n==================================================");

            System.out.println("Select Operation : ");
            System.out.println("1. Edit Course Catalog");
            System.out.println("2. View Grade of All Students");
            System.out.println("3. Generate Transcript");
            System.out.println("4. Logout");

            int option = 0;

            Scanner sc = new Scanner(System.in);
            option = sc.nextInt();
            sc.nextLine();

            if (option == 1) {
                catalog();
            } else if (option == 2) {
                view_grades();
            } else if (option == 3) {
                transcript();
            } else if (option == 4) {
                return;
            } else {
                System.out.println("Select a valid option \n");
            }

            System.out.println("\n==================================================");
        }

    }

    public static void catalog() throws Exception {
        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url"); // localhost:5432
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(url, username, password);

        while (true) {

            System.out.println("\n Select Operation : ");
            System.out.println("1. View Course Catalog");
            System.out.println("2. Add a course");
            System.out.println("3. Update a course");
            System.out.println("4. Delete a course");
            System.out.println("5. Go Back");

            int option = 0;
            Scanner sc = new Scanner(System.in);
            option = sc.nextInt();
            sc.nextLine();

            if (option == 1) {
                String query = "SELECT * FROM pre_reqs,course_catalog where course_catalog.course_code=pre_reqs.course_code;";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                Formatter fmt = new Formatter();
                fmt.format("\n %20s | %20s | %20s \n", "COURSE CODE", "L-T-P-C", "PRE-REQUISITES");

                while (rs.next()) {
                    String course_code = rs.getString("course_code");
                    String l = rs.getString("l");
                    String t = rs.getString("t");
                    String p = rs.getString("p");
                    String credits = rs.getString("credits");
                    String pre_req = rs.getString("pre_req");

                    String ltpc = l + "-" + t + "-" + p + "-" + credits;
                    fmt.format("\n %20s | %20s | %20s \n", course_code, ltpc, pre_req);

                }

                System.out.println(fmt);
            }

            else if (option == 2) {

                String course_code, pre_req;
                int l, t, p, credits;

                System.out.println("\nEnter Course Code \n");
                Scanner sc1 = new Scanner(System.in);
                course_code = sc1.nextLine();

                System.out.println("\nEnter Lecture hours \n");
                l = Integer.parseInt(sc1.nextLine());

                System.out.println("\nEnter Tutorial hours \n");
                t = Integer.parseInt(sc1.nextLine());

                System.out.println("\nEnter Practical hours \n");
                p = Integer.parseInt(sc1.nextLine());

                System.out.println("\nEnter Credits \n");
                credits = Integer.parseInt(sc1.nextLine());

                String query = "insert into course_catalog (course_code,L,T,P,Credits) values ('"
                        + course_code + "'," + l + "," + t + "," + p + "," + credits + ");";

                Statement st = con.createStatement();
                int x = st.executeUpdate(query);

                while (true) {
                    System.out.println("\nEnter pre-requisites (Enter NIL if none). Once done, enter q \n");
                    pre_req = sc.nextLine();
                    if (pre_req.equalsIgnoreCase("q")) {
                        break;
                    } else {
                        String query_ = "insert into pre_reqs (course_code,pre_req) values ('"
                                + course_code + "','" + pre_req + "');";
                        Statement st_ = con.createStatement();
                        x = st_.executeUpdate(query_);
                    }
                }

            }

            else if (option == 3) {
                String course_code, pre_req;
                Float l, t, p, credits;

                System.out.println("Enter Course Code To Update \n");
                Scanner sc1 = new Scanner(System.in);
                course_code = sc1.nextLine();

                String query = "SELECT * FROM pre_reqs,course_catalog where course_catalog.course_code=pre_reqs.course_code and course_catalog.course_code='"
                        + course_code + "' ;";
                Statement st = con.createStatement();
                ResultSet rs = st.executeQuery(query);

                while (rs.next()) {
                    course_code = rs.getString("course_code");
                    String display_l = rs.getString("l");
                    String display_t = rs.getString("t");
                    String display_p = rs.getString("p");
                    String display_credits = rs.getString("credits");
                    pre_req = rs.getString("pre_req");

                    System.out.println("  " + course_code + "  " + "|" + display_l + "-" + display_t + "-" + display_p
                            + "-" + display_credits
                            + "|" + pre_req);
                }

                System.out.println("\nEnter Lecture hours \n");
                l = sc1.nextFloat();
                sc1.nextLine();

                System.out.println("\nEnter Tutorial hours \n");
                t = sc1.nextFloat();
                sc1.nextLine();

                System.out.println("\nEnter Practical hours \n");
                p = sc1.nextFloat();
                sc1.nextLine();

                System.out.println("\nEnter Credits \n");
                credits = sc1.nextFloat();
                sc1.nextLine();

                query = "update course_catalog set l=" + l + ", t= " + t + ", p=" + p + ", credits=" + credits
                        + " where course_code='" + course_code + "';";

                System.out.println(query);

                Statement st1 = con.createStatement();
                int x = st1.executeUpdate(query);

                while (true) {
                    int option_pre = 0;
                    System.out.println("\nSelect Option \n");

                    System.out.println("\n1. Add new pre-requisite \n");
                    System.out.println("\n2. Delete pre-requisite \n");
                    System.out.println("\n3. No change \n");

                    option_pre = sc1.nextInt();
                    sc1.nextLine();

                    if (option_pre == 1) {
                        System.out.println("\nEnter pre-requisite to be added ");
                        pre_req = sc1.nextLine();

                        String query_ = "insert into pre_reqs (course_code,pre_req) values ('"
                                + course_code + "','" + pre_req + "');";
                        Statement st_ = con.createStatement();
                        x = st_.executeUpdate(query_);
                    } else if (option_pre == 2) {
                        System.out.println("\nEnter pre-requisite to be deleted ");
                        pre_req = sc1.nextLine();

                        String query_ = "delete from pre_reqs where course_code='" + course_code + "' and pre_req='"
                                + pre_req + "';";
                        Statement st_ = con.createStatement();
                        x = st_.executeUpdate(query_);
                    } else {
                        break;
                    }

                }
            } else if (option == 4) {
                String course_code;
                int option_;
                System.out.println("Enter course code to be deleted \n");
                Scanner sc1 = new Scanner(System.in);
                course_code = sc1.nextLine();

                System.out.println("Press 1 to confirm deletion \n");
                // Scanner sc1 = new Scanner(System.in);
                option_ = sc1.nextInt();
                sc1.nextLine();

                if (option_ == 1) {
                    String query_ = "delete from pre_reqs where course_code='" + course_code
                            + "'; delete from course_catalog where course_code='" + course_code + "';";
                    Statement st_ = con.createStatement();
                    int x = st_.executeUpdate(query_);

                }

            } else {
                return;
            }
        }

    }

    public static void view_grades() throws Exception {
        ResourceBundle rd = ResourceBundle.getBundle("config");
        String url = rd.getString("url"); // localhost:5432
        String username = rd.getString("username");
        String password = rd.getString("password");

        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(url, username, password);

        while (true) {

            System.out.println("\n Select Operation : ");
            System.out.println("1. View grades of all students in their enrollments");
            System.out.println("2. View grades of students in a particular course");
            System.out.println("3. View grades of a particular student ");
            System.out.println("4. Go Back");

            int option = 0;
            Scanner sc = new Scanner(System.in);
            option = sc.nextInt();
            sc.nextLine();

            String query = "";
            Statement st;

            if (option == 1) {
                query = "select * from enrollments where status='COMPLETED'";

            } else if (option == 2) {
                String course_code;

                System.out.println("Enter Course Code  \n");
                sc = new Scanner(System.in);
                course_code = sc.nextLine();

                query = "select * from enrollments where course_code = '" + course_code + "'";

            } else if (option == 3) {
                String entry_no;

                System.out.println("Enter student entry no.  \n");
                sc = new Scanner(System.in);
                entry_no = sc.nextLine();

                query = "select * from enrollments where entry_no = '" + entry_no + "'";

            } else {
                return;
            }

            st = con.createStatement();
            ResultSet rs = st.executeQuery(query);

            System.out.println(" Entry_No | Course_code | grade | status ");
            while (rs.next()) {
                String entry_no = rs.getString("entry_no");
                String course_code = rs.getString("course_code");
                String grade = rs.getString("grade");
                String status = rs.getString("status");

                System.out.println("  " + entry_no + "  " + "|" + course_code + "|" + grade + "|" + status);

            }
        }

    }

    public static void transcript() {

    }

}
