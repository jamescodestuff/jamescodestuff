package prereqchecker;

import java.util.ArrayList;

public class Course {
    String name;
    ArrayList<Course> prereqs;
    boolean completed = false;
    boolean marked = false;

    public Course(String a) {
        name = a;
        prereqs = new ArrayList<Course>();
    }

    public void addPrereq(Course a) {
        prereqs.add(a);
    }
}
