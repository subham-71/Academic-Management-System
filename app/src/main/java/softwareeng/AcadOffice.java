package softwareeng;

import java.io.File;
import java.io.FileWriter;
import java.sql.*;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Scanner;

public class AcadOffice extends User {

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

        while (true) {

            System.out.println("\n==================================================");

            System.out.println("Select Operation : ");
            System.out.println("1. Edit Course Catalog");
            System.out.println("2. View Grade of All Students");
            System.out.println("3. Generate Transcript");
            System.out.println("4. Update Profile");
            System.out.println("5. View Logs");
            System.out.println("6. Update Event Calendar");
            System.out.println("7. Track Graduation of a Student");
            System.out.println("8. Logout");

            int option = 0;

            option = sc.nextInt();
            System.out.println(option);
            sc.nextLine();

            if (option == 1) {
                System.out.println("================= COURSE CATALOG =================");
                catalog();
            } else if (option == 2) {
                System.out.println("================= VIEW GRADES =================");
                view_grades();
            } else if (option == 3) {
                System.out.println("================= GENERATE TRANSCRIPT =================");
                transcript();
            } else if (option == 4) {
                update_profile(sc);
            } else if (option == 5) {
                System.out.println("================= VIEW LOGS =================");
                view_logs();
            } else if (option == 6) {
                System.out.println("================= UPDATE CALENDAR =================");
                update_calendar();
            } else if (option == 7) {
                System.out.println("================= TRACK GRADUATION =================");
                track_grad();
            } else if (option == 8) {
                System.out.println("LOGGING OUT ... ");
                Timestamp logged_out = new Timestamp(System.currentTimeMillis());

                query = "update logs set logged_out = '" + logged_out + "' where email = '" + email + "' and role = '"
                        + role
                        + "' and logged_in = '" + logged_in + "';";

                dao.updatequery(query);

                System.out.println("LOGGED OUT SUCCESSFULLY ");
                return;
            } else {
                System.out.println("Select a valid option \n");
            }

            System.out.println("\n**************************************************");
        }

    }

    public static void catalog() throws Exception {

        while (true) {

            System.out.println("\n Select Operation : ");
            System.out.println("1. View Course Catalog");
            System.out.println("2. Add a course");
            System.out.println("3. Update a course");
            System.out.println("4. Delete a course");
            System.out.println("5. Go Back");

            int option = 0;
            option = sc.nextInt();
            sc.nextLine();

            if (option == 1) {
                String query = "SELECT * FROM pre_reqs,course_catalog where course_catalog.course_code=pre_reqs.course_code;";
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

            else if (option == 2) {

                String course_code, pre_req, department;
                float l, t, p;
                float credits;

                System.out.println("\nEnter Course Code \n");
                course_code = sc.nextLine();

                System.out.println("\nEnter Lecture hours \n");
                l = Float.parseFloat(sc.nextLine());

                System.out.println("\nEnter Tutorial hours \n");
                t = Float.parseFloat(sc.nextLine());

                System.out.println("\nEnter Practical hours \n");
                p = Float.parseFloat(sc.nextLine());

                System.out.println("\nEnter Credits \n");
                credits = Float.parseFloat(sc.nextLine());

                System.out.println(
                        "\nEnter Department ('CSE' , 'MA' , 'EE' , 'ME' , 'CE' , 'CH' , 'MME' , 'HS' , 'PH' , 'BME') \n");
                department = sc.nextLine();

                String query = String.format(
                        "insert into course_catalog (course_code,L,T,P,credits,department) values ('%s', %f, %f , %f , %f , '%s');",
                        course_code, l, t, p, credits, department);

                x = dao.updatequery(query);

                while (true) {
                    System.out.println("\nEnter pre-requisites (Enter NIL if none). Once done, enter q \n");
                    pre_req = sc.nextLine();
                    if (pre_req.equalsIgnoreCase("q")) {
                        break;
                    } else {
                        query = "insert into pre_reqs (course_code,pre_req) values ('"
                                + course_code + "','" + pre_req + "');";
                        x = dao.updatequery(query);
                    }
                }

                System.out.println("Course Added Successfully. \n");

            }

            else if (option == 3) {

                String course_code, pre_req, department;
                Float l, t, p, credits;

                System.out.println("Enter Course Code To Update \n");
                course_code = sc.nextLine();

                String query = "SELECT * FROM pre_reqs,course_catalog where course_catalog.course_code=pre_reqs.course_code and course_catalog.course_code='"
                        + course_code + "' ;";

                rs = dao.readquery(query);

                Formatter fmt = new Formatter();
                fmt.format("\n %20s | %20s | %20s | %20s \n", "COURSE CODE", "L-T-P-C", "PRE-REQUISITES", "DEPARTMENT");

                while (rs.next()) {
                    course_code = rs.getString("course_code");
                    String display_l = rs.getString("l");
                    String display_t = rs.getString("t");
                    String display_p = rs.getString("p");
                    String display_credits = rs.getString("credits");
                    pre_req = rs.getString("pre_req");
                    department = rs.getString("department");

                    String ltpc = display_l + "-" + display_t + "-" + display_p + "-" + display_credits;
                    fmt.format("\n %20s | %20s | %20s  | %20s\n", course_code, ltpc, pre_req, department);

                }

                System.out.println("\nEnter Lecture hours \n");
                l = sc.nextFloat();
                sc.nextLine();

                System.out.println("\nEnter Tutorial hours \n");
                t = sc.nextFloat();
                sc.nextLine();

                System.out.println("\nEnter Practical hours \n");
                p = sc.nextFloat();
                sc.nextLine();

                System.out.println("\nEnter Credits \n");
                credits = sc.nextFloat();
                sc.nextLine();

                System.out.println(
                        "\nEnter Department ('CSE' , 'MA' , 'EE' , 'ME' , 'CE' , 'CH' , 'MME' , 'HS' , 'PH' , 'BME')\n");
                department = sc.nextLine();

                query = "update course_catalog set l=" + l + ", t= " + t + ", p=" + p + ", credits=" + credits
                        + ", department='" + department
                        + "' where course_code='" + course_code + "';";

                x = dao.updatequery(query);

                while (true) {
                    int option_pre = 0;
                    System.out.println("\nSelect Option \n");

                    System.out.println("\n1. Add new pre-requisite \n");
                    System.out.println("\n2. Delete pre-requisite \n");
                    System.out.println("\n3. No change \n");

                    option_pre = sc.nextInt();
                    sc.nextLine();

                    if (option_pre == 1) {
                        System.out.println("\nEnter pre-requisite to be added ");
                        pre_req = sc.nextLine();

                        query = "insert into pre_reqs (course_code,pre_req) values ('"
                                + course_code + "','" + pre_req + "');";

                        x = dao.updatequery(query);

                        System.out.println("Pre-requisite added successfully \n");

                    } else if (option_pre == 2) {
                        System.out.println("\nEnter pre-requisite to be deleted ");
                        pre_req = sc.nextLine();

                        query = "delete from pre_reqs where course_code='" + course_code + "' and pre_req='"
                                + pre_req + "';";

                        x = dao.updatequery(query);

                        System.out.println("Pre-requisite deleted successfully \n");
                    } else {
                        break;
                    }

                }
                System.out.println("Course Updated Successfully. \n");

            } else if (option == 4) {

                String course_code;
                int option_;
                System.out.println("Enter course code to be deleted \n");
                course_code = sc.nextLine();

                System.out.println("Press 1 to confirm deletion \n");
                option_ = sc.nextInt();
                sc.nextLine();

                if (option_ == 1) {
                    query = "delete from pre_reqs where course_code='" + course_code
                            + "'; delete from course_catalog where course_code='" + course_code + "';";

                    x = dao.updatequery(query);

                    System.out.println("Course Deleted Successfully");
                } else {
                    System.out.println("Deletion Cancelled \n");
                }

            } else if (option == 5) {
                return;
            } else {
                System.out.println("Select a valid option \n");
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

            String query = "";
            Statement st;

            if (option == 1) {
                query = "select * from enrollments where status!='RUNNING' and status != 'INSTRUCTOR WITHDREW' and status!='DROPPED'";

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

            rs = dao.readquery(query);

            Formatter fmt = new Formatter();
            fmt.format("\n %30s | %30s | %30s | %30s \n", "ENTRY_NO", "COURSE_CODE", "GRADE", "STATUS");

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

    public static void transcript() throws Exception {

        String entry_no = "";
        int batch = 0;
        String department = "";

        System.out.println("Enter student entry no.  \n");

        entry_no = sc.nextLine();

        query = "select * from auth where entry_no = '" + entry_no + "'";

        rs = dao.readquery(query);

        if (rs.next()) {
            batch = rs.getInt("batch");
            department = rs.getString("department");

            String filename = String.format("transcripts/%s_transcript.txt", entry_no);

            File fileobj = new File(filename);
            fileobj.createNewFile();

            FileWriter writer = new FileWriter(filename);
            writer.write(String.format(
                    "===================================== TRANSCRIPT - %s ===================================== \n\n",
                    entry_no));
            writer.write(
                    "                               INDIAN INSTITUTE OF TECHNOLOGY, ROPAR                            \n\n");
            writer.write(
                    String.format("Entry Number : %s \nBatch : %d \nDepartment : %s \n\n", entry_no, batch,
                            department));
            writer.write(String.format(
                    "========================================== COURSES UNDERTAKEN ========================================== \n\n"));

            query = String.format(
                    "select * from enrollments where entry_no = '%s' and status != 'RUNNING' and status != 'INSTRUCTOR WITHDREW' and status!='DROPPED' ;",
                    entry_no);

            rs = dao.readquery(query);

            String text = String.format("\n %20s | %20s | %20s| %20s | %20s\n", "COURSE CODE", "ACAD YEAR", "SEMESTER",
                    "GRADE", "STATUS");

            while (rs.next()) {
                String course_code = rs.getString("course_code");
                String grade = rs.getString("grade");
                String status = rs.getString("status");
                String acad_year = rs.getString("start_acad_year");
                String semester = rs.getString("semester");

                text = String.format("\n %20s | %20s | %20s | %20s | %20s \n", course_code, acad_year, semester, grade,
                        status);
                writer.write(text);

            }

            float cgpa = get_cgpa(entry_no);
            writer.write("CGPA : " + cgpa);

            // Fetch Calendar

            int current_start_acad_year = 0;
            int current_semester = 0;

            query = "Select * from calendar where status='RUNNING' ;";

            rs = dao.readquery(query);

            while (rs.next()) {
                current_start_acad_year = Integer.parseInt(rs.getString("start_acad_year"));
                current_semester = Integer.parseInt(rs.getString("semester"));
            }

            int prev_acad_year = 0;
            int prev_semester = 0;

            if (current_semester == 2) {
                prev_acad_year = current_start_acad_year;
                prev_semester = 1;
            } else {
                prev_acad_year = current_start_acad_year - 1;
                prev_semester = 2;
            }

            writer.write(
                    String.format("\n\nThis transcript is generated for all courses till YEAR : %d , SEMESTER : %d",
                            prev_acad_year, prev_semester));
            writer.close();

            System.out.println("Transcript generated successfully. \n");
        } else {
            System.out.println("Invalid entry number. \n");
        }

    }

    public static float get_cgpa(String entry_no) throws Exception {

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

        query = String.format(
                "select enrollments.course_code,grade,status,type,credits from enrollments,offered_to,course_catalog where course_catalog.course_code = enrollments.course_code and entry_no = '%s' and enrollments.course_code = offered_to.course_code and enrollments.start_acad_year = offered_to.start_acad_year and enrollments.semester = offered_to.semester and status!='RUNNING' and status!='INSTRUCTOR_WITHDREW' and status!='DROPPED' ;",
                entry_no);

        rs = dao.readquery(query);

        String course_code = "", grade = "", status = "", type = "";
        float total_credits = 0, credits = 0;
        float total_points = 0;

        while (rs.next()) {
            course_code = rs.getString("course_code");
            grade = rs.getString("grade");
            status = rs.getString("status");
            type = rs.getString("type");
            credits = Float.parseFloat(rs.getString("credits"));

            total_credits = total_credits + credits;
            total_points = total_points + credits * (grade_map.get(grade));
        }
        float cgpa = total_points / total_credits;

        return cgpa;

    }

    public static void view_logs() throws Exception {

        query = "Select * from logs;";

        rs = dao.readquery(query);

        String email = "", role = "", logged_in = "", logged_out = "";

        Formatter fmt = new Formatter();
        fmt.format("\n %30s | %15s | %30s | %30s \n", "EMAIL", "ROLE", "LOGGED IN TIME", "LOGGED OUT TIME");

        while (rs.next()) {
            email = rs.getString("email");
            role = rs.getString("role");
            logged_in = rs.getString("logged_in");
            logged_out = rs.getString("logged_out");

            fmt.format("\n %30s | %15s | %30s | %30s \n", email, role, logged_in, logged_out);
        }

        System.out.println(fmt);

    }

    public static void update_calendar() throws Exception {

        query = "select * from calendar;";
        rs = dao.readquery(query);

        String start_acad_year = "", semester = "", status = "";

        Formatter fmt = new Formatter();
        fmt.format("\n %30s | %15s | %20s \n", "ACADEMIC YEAR", "SEMESTER", "STATUS");

        while (rs.next()) {
            start_acad_year = rs.getString("start_acad_year");
            semester = rs.getString("semester");
            status = rs.getString("status");

            int acad_year = Integer.parseInt(start_acad_year);
            int next_acad_year = acad_year + 1;

            String academic_year = start_acad_year + "-" + next_acad_year;

            fmt.format("\n %30s | %15s | %20s \n", academic_year, semester, status);
        }

        System.out.println(fmt);

        System.out.println("Enter new start academic year");
        int new_start_acad_year = sc.nextInt();
        sc.nextLine();

        System.out.println("Enter new semester");
        int new_semester = sc.nextInt();
        sc.nextLine();

        query = String.format(
                "update calendar set status='COMPLETED' where status='RUNNING' ; insert into calendar values (%d,%d,'RUNNING'); ",
                new_start_acad_year, new_semester);

        x = dao.updatequery(query);

        System.out.println("Calendar updated successfully");

    }

    public static void track_grad() throws Exception {

        String entry_no = "";

        System.out.println("Enter student entry no.  \n");
        entry_no = sc.nextLine();

        HashMap<String, Integer> grade_map = new HashMap<String, Integer>();
        grade_map.put("A", 10);
        grade_map.put("A-", 9);
        grade_map.put("B", 8);
        grade_map.put("B-", 7);
        grade_map.put("C", 6);
        grade_map.put("C-", 5);
        grade_map.put("D", 4);

        query = String.format(
                "select enrollments.course_code,grade,status,type,credits from enrollments,offered_to,course_catalog where course_catalog.course_code = enrollments.course_code and entry_no = '%s' and enrollments.course_code = offered_to.course_code and enrollments.start_acad_year = offered_to.start_acad_year and enrollments.semester = offered_to.semester and status!='RUNNING' and status!='INSTRUCTOR_WITHDREW' and status!='DROPPED' ;",
                entry_no);
        rs = dao.readquery(query);

        String course_code = "", grade = "", status = "", type = "";
        float total_credits = 0, credits = 0;
        float total_points = 0;
        float pc_credits = 0;
        float pe_credits = 0;
        float btp_credits = 0;

        Formatter fmt = new Formatter();
        fmt.format("\n %20s | %20s | %20s | %20s \n", "COURSE CODE", "GRADE", "status", "type");

        while (rs.next()) {
            course_code = rs.getString("course_code");
            grade = rs.getString("grade");
            status = rs.getString("status");
            type = rs.getString("type");
            credits = Float.parseFloat(rs.getString("credits"));

            fmt.format("\n %20s | %20s | %20s | %20s \n", course_code, grade, status, type);

            total_credits = total_credits + credits;
            total_points = total_points + credits * (grade_map.get(grade));

            if (course_code.equalsIgnoreCase("CP301") || course_code.equalsIgnoreCase("CP302")) {
                btp_credits = btp_credits + credits;
            }

            if (type.equals("PC")) {
                pc_credits = pc_credits + credits;
            }

            if (type.equals("PE")) {
                pe_credits = pe_credits + credits;
            }
        }
        float cgpa = total_points / total_credits;

        System.out.println(
                " \n ==================================================================================== \n ");

        if (pc_credits >= 70 && pe_credits >= 70 && btp_credits == 6 && cgpa >= 5) {
            System.out.println(
                    "                     STUDENT IS ELIGIBLE FOR GRADUATION !                    ");
        } else {

            System.out.println(
                    "                       NOT ELIGIBLE FOR GRADUATION !                         ");

        }

        System.out.println(
                " \n ==================================================================================== \n ");

        System.out.println("Here is the list of completed courses");
        fmt.format("\n%20s = %f \n", "PE Credits", pe_credits);
        fmt.format("%20s = %f \n", "PC Credits", pc_credits);
        fmt.format("%20s = %f \n", "BTP Credits", btp_credits);
        System.out.println(fmt);

    }

}
