/**
 * POJO representing a single student record.
 */
public class Student {
    private int id;
    private String rollNo;
    private String name;
    private String course;
    private int year;
    private String email;
    private String phone;

    public Student() {}

    public Student(String rollNo, String name, String course, int year, String email, String phone) {
        this.rollNo = rollNo;
        this.name = name;
        this.course = course;
        this.year = year;
        this.email = email;
        this.phone = phone;
    }

    public Student(int id, String rollNo, String name, String course, int year, String email, String phone) {
        this(rollNo, name, course, year, email, phone);
        this.id = id;
    }

    // --- Getters and Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
