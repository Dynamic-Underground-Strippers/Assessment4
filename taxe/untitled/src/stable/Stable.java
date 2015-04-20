package stable;

import java.util.ArrayList;
import java.util.Arrays;

public class Stable {
    public static void main(String[] args) throws InterruptedException {
        Register register = new Register(3);
        Integer x=0,y=1,z=2;

        ArrayList<Integer> pref1 = new ArrayList<Integer>(Arrays.asList(x, y, z));
        Student student1 = new Student(0,pref1,register);

        ArrayList<Integer> pref2 = new ArrayList<Integer>(Arrays.asList(y, x, z));
        Student student2 = new Student(0,pref2,register);

        ArrayList<Integer> pref3 = new ArrayList<Integer>(Arrays.asList(z, x, y));
        Student student3 = new Student(0,pref3,register);

        ArrayList<Integer> pref4 = new ArrayList<Integer>(Arrays.asList(z, x, y));
        Project project1 = new Project(0, pref4, register);

        ArrayList<Integer> pref5 = new ArrayList<Integer>(Arrays.asList(z, y, x));
        Project project2 = new Project(0, pref5, register);

        ArrayList<Integer> pref6 = new ArrayList<Integer>(Arrays.asList(z, x, y));
        Project project3 = new Project(0, pref6, register);

        ArrayList <Project> projectList = new ArrayList<Project>();
        projectList.add(project1);
        projectList.add(project2);
        projectList.add(project3);

        ArrayList <Student> studentList = new ArrayList<Student>();
        studentList.add(student1);
        studentList.add(student2);
        studentList.add(student3);
        int finalNumber=12,i;

        register.setMode("processStudents");
        for (i=0; i<finalNumber; i++) {
            studentList.get(i).start();
            projectList.get(i).start();
        }

        for (i=0; i<finalNumber; i++)
            studentList.get(i).join();

        for (i=0; i<finalNumber; i++)
            projectList.get(i).join();

        for (i=0; i<finalNumber; i++)
        register.printPairs();


    }


    }


