package softwareeng;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Faculty extends User {
    static Scanner sc = new Scanner(System.in);

    static DaoI dao = new Dao();

    static String query = "";
    static ResultSet rs;
    static int x;

    public static void main() throws Exception {

        
        query = "Select * from logs;";
        rs = dao.readquery(query);  

        String email = "", role = "", logged_in = "";

        while (rs.next()) {
            email = rs.getString("email");
            role = rs.getString("role");
            logged_in = rs.getString("logged_in");
        }

        int option = 0;
        while (true) {

            System.out.println("\n==================================================");

            System.out.println("Select Operation : ");
            System.out.println("1. Register a Course Offering");
            System.out.println("2. Deregister a Course Offering");
            System.out.println("3. View Course Catalog");
            System.out.println("4. Upload Grades");
            System.out.println("5. View Grades");
            System.out.println("6. Update Profile");
            System.out.println("7. Logout");

            option = sc.nextInt();
            sc.nextLine();

            System.out.println(option);

            if (option == 1) {
                System.out.println("================= COURSE OFFERING REGISTRATION =================");
                register_course_offering();
            } else if (option == 2) {
                System.out.println("================= COURSE OFFERING DE-REGISTRATION =================");
                deregister_course_offering();
            } else if (option == 3) {
                System.out.println("================= COURSE CATALOG =================");
                view_catalog();
            } else if (option == 4) {
                System.out.println("================= UPLOAD GRADES =================");
                upload_grades();
            } else if (option == 5) {
                System.out.println("================= VIEW GRADES =================");
                view_grades();
            } else if (option == 6) {
                update_profile(sc);
            } else if (option == 7) {
                Timestamp logged_out = new Timestamp(System.currentTimeMillis());

                query = "update logs set logged_out = '" + logged_out + "' where email = '" + email + "' and role = '"
                        + role
                        + "' and logged_in = '" + logged_in + "';";
                x = dao.updatequery(query);
                return;
            } else {
                System.out.println("Select a valid option \n");
            }

            System.out.println("\n**************************************************");
        }

    }

    public static void register_course_offering() throws Exception {

        String query = "";
        Statement st;
        ResultSet rs;

        // Fetch Calendar

        int current_start_acad_year = 0;
        int current_semester = 0;

        query = "Select * from calendar where status='RUNNING' ;";

        rs = dao.readquery(query);

        while (rs.next()) {
            current_start_acad_year = Integer.parseInt(rs.getString("start_acad_year"));
            current_semester = Integer.parseInt(rs.getString("semester"));
        }

        // Fetch Email

        String email = "";
        query = "Select email from logs;";

        rs = dao.readquery(query);

        rs.last();
        email = rs.getString("email");

        // Department Check

        String course_dept = "", faculty_dept = "";
        query = "Select department from auth where email='" + email + "';";
        
        rs = dao.readquery(query);

        while (rs.next()) {
            faculty_dept = rs.getString("department");
        }

        System.out.println("Enter the course code you want to offer");
        String course_code = sc.nextLine();

        query = "Select department from course_catalog where course_code='" + course_code + "';";
        rs = dao.readquery(query);

        while (rs.next()) {
            course_dept = rs.getString("department");
        }

        if (!course_dept.equalsIgnoreCase(faculty_dept) && !course_dept.equalsIgnoreCase("ALL")) {
            System.out.println("Please offer a course from your department only");
        } else {
            query = String.format(
                    "Insert into course_offering(course_code,start_acad_year,semester,instructor_email,offering_dept,status) values ('%s','%d','%d','%s','%s','%s');",
                    course_code, current_start_acad_year, current_semester, email, course_dept, "RUNNING");

            x = dao.updatequery(query);

            System.out.println(
                    "Enter Restrictions (Offered Department,Batch,Minimum CGPA,Type) \n ");
            String offered_dept = "";
            int batch = 0;
            String type = "";
            Float min_cgpa = (float) 0;
            System.out.println(
                    "\n====================================================================================\n\nIf there are multiple restrictions, please enter one by one for each offered department (Batch also has to be specified 2019 CSE is different from 2020 CSE, so multiple restrictions.)\nIf the course is open to all departments in a batch and there is a common cgpa criteria , then you can enter 'ALL' as offered department and specify the batch and cgpa criteria. \n\n====================================================================================\n");
            while (true) {

                System.out.println("\n NEW RESTRICTION \n");

                System.out.println(
                        "Enter offered Department ('CSE' , 'MA' , 'EE' , 'ME' , 'CE' , 'CH' , 'MME' , 'HS' , 'PH' , 'BME') ");
                offered_dept = sc.nextLine();

                System.out.println("Enter corresponding offered department's batch");
                batch = sc.nextInt();
                sc.nextLine();

                System.out.println("Enter Minimum CGPA ");
                min_cgpa = sc.nextFloat();
                sc.nextLine();

                System.out.println("Enter course type (PC/PE) ");
                type = sc.nextLine();

                query = String.format(
                        "insert into offered_to (course_code,start_acad_year, semester, offered_dept,batch,min_cgpa,type) values('%s',%d,%d,'%s',%d,%f,'%s');",
                        course_code, current_start_acad_year, current_semester, offered_dept, batch, min_cgpa, type);

                System.out.println(query);
                x = dao.updatequery(query);

                String confirmation = "";
                System.out.println("Do you want to add another restriction ? (Yes/No)");
                confirmation = sc.nextLine();

                if (confirmation.equalsIgnoreCase("No")) {
                    break;
                }
            }

            System.out.println("Course offering successfully registered !");

            System.out.println(
                    "\n====================================================================================\n");

        }

    }

    public static void deregister_course_offering() throws Exception {

        // Fetch Calendar

        int current_start_acad_year = 0;
        int current_semester = 0;

        query = "Select * from calendar where status='RUNNING' ;";

        rs = dao.readquery(query);

        while (rs.next()) {
            current_start_acad_year = Integer.parseInt(rs.getString("start_acad_year"));
            current_semester = Integer.parseInt(rs.getString("semester"));
        } 

        // Fetch Email

        String email = "";
        query = "Select email from logs;";

        rs = dao.readquery(query);

        rs.last();
        email = rs.getString("email");

        // Department Check

        System.out.println("Enter the course code you want to deregister");
        String course_code = sc.nextLine();
        System.out.println(course_code);

        query = String.format(
                "select * from course_offering where course_code = '%s' and start_acad_year = %d and semester = %d and instructor_email = '%s';",
                course_code, current_start_acad_year, current_semester, email);

        rs = dao.readquery(query);

        if (!rs.isBeforeFirst()) {
            System.out.println("Course offering not registered ! Course Offering De-Registration Failed");
        } else {

            query = String.format(
                    "Delete from course_offering where course_code='%s' and start_acad_year=%d and semester=%d and instructor_email='%s';Delete from offered_to where course_code='%s' and start_acad_year=%d and semester=%d;",
                    course_code, current_start_acad_year, current_semester, email, course_code, current_start_acad_year,
                    current_semester);

            x = dao.updatequery(query);

            query = String.format(
                    "update enrollments set status = 'INSTRUCTOR WITHDREW' where course_code = '%s' and start_acad_year = %d and semester = %d and status = 'RUNNING';",
                    course_code, current_start_acad_year, current_semester);

            x = dao.updatequery(query);

            System.out.println("Course Offering De-Registered Successfully !");

        }

    }

    public static void view_catalog() throws Exception {

        query = "SELECT * FROM pre_reqs,course_catalog where course_catalog.course_code=pre_reqs.course_code;";
        
        rs = dao.readquery(query);

        Formatter fmt = new Formatter();
        fmt.format("\n %20s | %20s | %20s | %20s \n", "COURSE CODE", "L-T-P-C", "PRE-REQUISITES", "DEPARTMENT");

        while (rs.next()) {
            String course_code = rs.getString("course_code");
            String l = rs.getString("l");
            String t = rs.getString("t");
            String p = rs.getString("p");
            String credits = rs.getString("credits");
            String department = rs.getString("department");
            String pre_req = rs.getString("pre_req");

            String ltpc = l + "-" + t + "-" + p + "-" + credits;
            fmt.format("\n %20s | %20s | %20s  | %20s\n", course_code, ltpc, pre_req, department);

        }

        System.out.println(fmt);

    }

    public static void upload_grades() throws Exception {

        HashMap<String, Integer> grade_map = new HashMap<String, Integer>();
        grade_map.put("A", 10);
        grade_map.put("A-", 9);
        grade_map.put("B", 8);
        grade_map.put("B-", 7);
        grade_map.put("C", 6);
        grade_map.put("C-", 5);
        grade_map.put("D", 4);
        grade_map.put("E", 2);
        grade_map.put("F", 0);

        // Fetch Calendar

        int current_start_acad_year = 0;
        int current_semester = 0;

        query = "Select * from calendar where status='RUNNING';";

        rs = dao.readquery(query);

        while (rs.next()) {
            current_start_acad_year = Integer.parseInt(rs.getString("start_acad_year"));
            current_semester = Integer.parseInt(rs.getString("semester"));
        }

        // Fetch Email

        String email = "";
        query = "Select email from logs;";

        rs = dao.readquery(query);

        rs.last();
        email = rs.getString("email");

        String line = "";

        String course_code = "";
        String filepath = "";

        System.out.println("Enter the course code for which you want to upload grades");
        course_code = sc.nextLine();

        query = "select instructor_email from course_offering where course_code = '" + course_code
                + "' and start_acad_year = "
                + current_start_acad_year + " and semester = " + current_semester + " and instructor_email = '" + email
                + "';";

        rs = dao.readquery(query);

        String check_email = "";

        while (rs.next()) {
            check_email = rs.getString("instructor_email");
        }

        if (!check_email.equals(email)) {
            System.out.println("You are not the instructor for this course offering !");
        } else {

            System.out.println("Enter the Absolute filepath of the CSV file");
            filepath = sc.nextLine();

            BufferedReader br = new BufferedReader(new FileReader(filepath));
            while ((line = br.readLine()) != null) // returns a Boolean value
            {
                String[] buffer = line.split(","); // use comma as separator

                String entry_no = buffer[0];
                String grade = buffer[1];

                if (grade_map.get(grade) > 4) {

                    query = String.format(
                            "update enrollments set grade = '%s', status = 'PASSED' where course_code = '%s' and start_acad_year = %d and semester = %d and entry_no = '%s';",
                            grade, course_code, current_start_acad_year, current_semester, entry_no);
                } else {
                    query = String.format(
                            "update enrollments set grade = '%s', status = 'FAILED' where course_code = '%s' and start_acad_year = %d and semester = %d and entry_no = '%s';",
                            grade, course_code, current_start_acad_year, current_semester, entry_no);
                }

                x= dao.updatequery(query);

                System.out.println("Grades Uploaded Successfully !");
            }
        }

    }

    public static void view_grades() throws Exception {

        while (true) {

            System.out.println("\n Select Operation : ");
            System.out.println("1. View grades of all students in their enrollments");
            System.out.println("2. View grades of students in a particular course");
            System.out.println("3. View grades of a particular student ");
            System.out.println("4. Go Back");

            int option = 0;
            option = sc.nextInt();
            sc.nextLine();

       
            if (option == 1) {
                query = "select * from enrollments where status != 'RUNNING' and status != 'INSTRUCTOR WITHDREW' and status!='DROPPED'";

            } else if (option == 2) {
                String course_code;

                System.out.println("Enter Course Code  \n");
                course_code = sc.nextLine();

                query = "select * from enrollments where course_code = '" + course_code + "'";

            } else if (option == 3) {
                String entry_no;

                System.out.println("Enter student entry no.  \n");
                entry_no = sc.nextLine();

                query = "select * from enrollments where entry_no = '" + entry_no + "'";

            } else {
                return;
            }

            ResultSet rs = dao.readquery(query);

            Formatter fmt = new Formatter();
            fmt.format("\n %30s | %30s | %30s | %30s\n", "ENTRY_NO", "COURSE_CODE", "GRADE", "STATUS");

            while (rs.next()) {
                String entry_no = rs.getString("entry_no");
                String course_code = rs.getString("course_code");
                String grade = rs.getString("grade");
                String status = rs.getString("status");

                fmt.format("\n %30s | %30s | %30s | %30s", entry_no, course_code, grade, status);
            }

            System.out.println(fmt);
        }

    }

}
