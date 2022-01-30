package prereqchecker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Graph {
    HashMap<String, Course> classes = new HashMap<String, Course>();

    public Graph(String file) {
        classes = new HashMap<String, Course>();
        StdIn.setFile(file);
        int nums = StdIn.readInt();
        StdIn.readLine();
        for (int i = 0; i < nums; i++) {
            String txt = StdIn.readLine();
            classes.put(txt, new Course(txt));

        }
        int nums1 = StdIn.readInt();
        StdIn.readLine();
        for (int i = 0; i < nums1; i++) {
            classes.get(StdIn.readString()).addPrereq(classes.get(StdIn.readString()));
        }
    }

    public void print(String file) {
        StdOut.setFile(file);
        for (Course cs : classes.values()) {
            StdOut.print(cs.name + " ");
            for (Course prereq : cs.prereqs) {
                StdOut.print(prereq.name + " ");
            }
            StdOut.println();
        }
    }

    static boolean yes = true;

    public void check(String file1, String file2) {
        StdIn.setFile(file1);
        Course course1 = classes.get(StdIn.readLine());
        Course course2 = classes.get(StdIn.readLine());
        yes = true;
        prereq(course1, course2);
        StdOut.setFile(file2);
        StdOut.println(yes ? "YES" : "NO");
    }

    public void prereq(Course first, Course second) {
        second.completed = true;
        if (second.prereqs.size() > 0) {
            for (Course prereq : second.prereqs) {
                if (!prereq.completed) {
                    prereq(first, prereq);
                }
            }
        }
        if (second.equals(first)) {
            yes = false;
        }

    }

    public void take(String file1, String file2) {
        StdIn.setFile(file1);
        int num = StdIn.readInt();
        StdIn.readLine();
        for (int i = 0; i < num; i++) {
            eli(classes.get(StdIn.readLine()));
        }
        StdOut.setFile(file2);
        for (Course a : classes.values()) {
            if (!a.completed) {
                boolean canDo = true;
                for (Course b : a.prereqs) {
                    if (!b.completed) {
                        canDo = false;
                        break;
                    }
                }
                if (canDo) {
                    StdOut.println(a.name);
                }
            }
        }

    }

    public void eli(Course b) {
        b.completed = true;
        if (b.prereqs.size() > 0) {
            for (Course prereq : b.prereqs) {
                if (!prereq.completed) {
                    eli(prereq);
                }
            }
        }
    }

    public void want(String file1, String file2) {
        StdIn.setFile(file1);
        Course target = classes.get(StdIn.readLine());
        int d = StdIn.readInt();
        StdIn.readLine();
        for (int i = 0; i < d; i++) {
            eli(classes.get(StdIn.readLine()));
        }
        StdOut.setFile(file2);
        target.completed = true;
        goThrough(target);
    }

    public void goThrough(Course a) {
        if (!a.completed) {
            StdOut.println(a.name);
        }
        a.completed = true;
        if (a.prereqs.size() > 0) {
            for (Course prereq : a.prereqs) {
                if (!prereq.completed) {
                    goThrough(prereq);
                }
            }
        }

    }

    public void schedule(String file1, String file2) {
        StdIn.setFile(file1);
        Course find = classes.get(StdIn.readLine());
        int d = StdIn.readInt();
        StdIn.readLine();
        for (int i = 0; i < d; i++) {
            String a = StdIn.readLine();
            eli(classes.get(a));
        }
        HashMap<Course, Integer> planner = new HashMap<Course, Integer>();
        int max = runThrough(planner, -1, find);
        planner.remove(find);
        ArrayList<ArrayList<Course>> diary = new ArrayList<ArrayList<Course>>();
        for (int i = 0; i <= max; i++) {
            diary.add(new ArrayList<Course>());
        }

        for (Map.Entry<Course, Integer> room : planner.entrySet()) {
            diary.get(room.getValue()).add(room.getKey());
        }
        StdOut.setFile(file2);
        StdOut.println(diary.size());
        for (int i = diary.size() - 1; i >= 0; i--) {
            for (Course c : diary.get(i)) {
                StdOut.print(c.name + " ");
            }
            StdOut.println();
        }
    }

    public int runThrough(HashMap<Course, Integer> planner, int sem, Course cour) {
        cour.completed = true;
        int max = sem;
        planner.put(cour, sem);
        if (cour.prereqs.size() > 0) {
            for (Course prereq : cour.prereqs) {
                if (!prereq.completed || (planner.containsKey(prereq) && planner.get(prereq) <= sem))
                    max = Math.max(max, runThrough(planner, sem + 1, prereq));
            }

        }
        return max;
    }

}