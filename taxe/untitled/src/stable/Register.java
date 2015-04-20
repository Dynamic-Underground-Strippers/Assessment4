package stable;


import java.util.ArrayList;
import java.util.List;

public class Register {
    private ArrayList<List<Student>> register;         // Shared data structure
    private String mode;                               // Current mode of the algorithm
    private int projectCounter=0, studentCounter=0;    // Number of projects/projects woken in current mode
    private int totalNumber;                           // Total number of students/projects


    public Register (int n){
        this.size=n;
        register = new ArrayList<List<Student>>();
    }
    totalNumber=9;

    public synchronized void put (Student student, Project project)
    {
        List list = register.get(project.getID());
        list.add(student);
        register.add(project.getID(),list);
        project.setIsProposedTo(true);
            //Figure 1.1 Put method called by a student thread
    }

    public synchronized  void keepOnly (Student student, int projectID){
        List<Student> list = register.get(projectID);   // Get the list of proposals
        for (int i=0; i<list.size(); i++)               //For all students in the list:
        {
            list.get(i).setIsRejected(true);    //signal rejection to students
            list.get(i).increaseRejections();   //increase the preference index
            list.remove(i);                     //remove from the list
        }
        list.add(student);                      //add chosen student
        student.setIsRejected(false);           //accept chosen student
        register.add(projectID,list );          //add chosen student to the list of proposals
    }

    public boolean assignmentComplete(){
        boolean isComplete=true;
        for (int i=0; i<register.size() && isComplete==true; i++)  // Assume assignment is complete
        {
            if (register.get(i).size()!=1) isComplete=false;       // Report as not complete if at least one instance fails the test
        }
        return isComplete;
    }

    public synchronized List getList(int i){
        return register.get(i);
    }

    public void setMode (String s){
        this.mode = s;
    }

    public String getMode (){
        return this.mode;
    }

    public void printPairs(){
        for (int i=0;i<3;i++)
            System.out.println("project " + i + " student" + register.get(i).get(0) );
    }

    public void increaseProjectCounter(){
        projectCounter++;
    }
    public void increaseStudentCounter(){
        studentCounter++;
    }

    public int getProjectCounter(){
        return this.projectCounter;
    }

    public int getStudentCounter(){
        return this.studentCounter;
    }

    public int getTotalNumber(){
        return this.totalNumber;
    }
}

