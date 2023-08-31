package sqlDataEntry;

import org.json.JSONArray;
import org.json.JSONObject;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentDataManage {
    public static void main(String[] args) {
        try {
            List<Student> students = readingStudentDataFromExcel(".\\dataSet\\Book2.xlsx");
            creatingJSONFile(students, "students.json");
            createDatabase();
            saveingDataToDatabase(students);
            searchingAndPrint();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    public static void createDatabase() {
        String url = "jdbc:mysql://localhost:3306/";
        String dbName = "dataStudents";
        String username = "root";
        String password = "Akh!l2000";

        try {
            // Create a connection to the MySQL server
            Connection connection = DriverManager.getConnection(url, username, password);

            // Create a new database
            Statement statement = connection.createStatement();
            String createDbQuery = "CREATE DATABASE IF NOT EXISTS " + dbName;
            statement.executeUpdate(createDbQuery);

            // Use the newly created database
            String useDbQuery = "USE " + dbName;
            statement.executeUpdate(useDbQuery);

            // Create a table to store student data
            String createTableQuery = "CREATE TABLE IF NOT EXISTS dataStudents (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "student_name VARCHAR(255)," +
                    "physics INT," +
                    "chemistry INT," +
                    "maths INT" +
                    ")";
            statement.executeUpdate(createTableQuery);
            System.out.println("Database and table created successfully!");

            // Close the resources
            statement.close();
            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
            }
        }
    	  /* public static void createDatabaseDemo () {
    	      Connection con=null;
    	      Statement stmt=null;
    	      String yourDatabaseName="studentdetails";
    	      try {
    	         con=DriverManager.getConnection("jdbc:mysql://localhost:3306/studentdetails?useSSL=false",
    	         "root","Akh!l2000");
    	         stmt = con.createStatement();
    	         int status = stmt.executeUpdate("CREATE DATABASE "+yourDatabaseName);
    	         if(status > 0) {
    	            System.out.println("Database is created successfully !!!");
    	         }
    	      }
    	      catch(Exception e) {
    	         e.printStackTrace();
    	      }} */
    	   
    public static List<Student> readingStudentDataFromExcel(String filePath) throws IOException {
        List<Student> studentsdata = new ArrayList<>();

        FileInputStream fileInputStream = new FileInputStream(filePath);
        Workbook workbook = new XSSFWorkbook(fileInputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            String name = row.getCell(0).getStringCellValue();
            int physics = (int) row.getCell(1).getNumericCellValue();
            int chemistry = (int) row.getCell(2).getNumericCellValue();
            int maths = (int) row.getCell(3).getNumericCellValue();

            studentsdata.add(new Student(name, physics, chemistry, maths));
        }

        workbook.close();
        fileInputStream.close();

        return studentsdata;
    }

    public static void creatingJSONFile(List<Student> studentsdata, String filePath) throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (Student studentdata : studentsdata) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", studentdata.name);
            jsonObject.put("physics", studentdata.physics);
            jsonObject.put("chemistry", studentdata.chemistry);
            jsonObject.put("maths", studentdata.maths);
            jsonArray.put(jsonObject);
        }

        try (FileWriter fileWriter = new FileWriter(filePath)) {
        	fileWriter.write(jsonArray.toString());
        }
    }

    public static void saveingDataToDatabase(List<Student> studentsdata) throws SQLException {
        String url = "jdbc:mysql://localhost:3306/dataStudents";
        String username = "root";
        String password = "Akh!l2000";

        Connection connection = DriverManager.getConnection(url, username, password);

        for (Student studentdata : studentsdata) {
            String insertQuery = "INSERT INTO students (name, physics, chemistry, maths) VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
            preparedStatement.setString(1, studentdata.name);
            preparedStatement.setInt(2, studentdata.physics);
            preparedStatement.setInt(3, studentdata.chemistry);
            preparedStatement.setInt(4, studentdata.maths);
            preparedStatement.executeUpdate();
        }

        connection.close();
    }

    public static void searchingAndPrint() throws SQLException {
    	 String url = "jdbc:mysql://localhost:3306/dataStudents";
         String username = "root";
         String password = "Akh!l2000";

         Connection connection = DriverManager.getConnection(url, username, password);

         Scanner scanner = new Scanner(System.in);
         System.out.print("Enter student name: ");
         String inputdata = scanner.nextLine();

         String currentQuery = "SELECT * FROM students WHERE name = ?";
         PreparedStatement searchStatement = connection.prepareStatement(currentQuery);
         searchStatement.setString(1, inputdata);

         ResultSet resultDataSet = searchStatement.executeQuery();
         JSONArray foundStudents = new JSONArray();

         while (resultDataSet.next()) {
             JSONObject foundStudent = new JSONObject();
             foundStudent.put("name", resultDataSet.getString("name"));
             foundStudent.put("physics", resultDataSet.getInt("physics"));
             foundStudent.put("chemistry", resultDataSet.getInt("chemistry"));
             foundStudent.put("maths", resultDataSet.getInt("maths"));
             
             foundStudents.put(foundStudent);
             break;
                }

         connection.close();

         if (foundStudents.length() > 0) {
             System.out.println("Found Students in JSON Format:");
             System.out.println(foundStudents.toString(2));
         } else {
             System.out.println("No matching student found.");
         }
     }}
