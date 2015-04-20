package stable;


import java.util.ArrayList;
import java.util.List;

public class Project extends Thread {
    private int ID;
    private boolean isProposedTo = false;
    private ArrayList<Integer> ranking;
    private Register register;
    private Student student, newStudent;
    private List<Student> list;
    private int highestPriority;

    public Project(int ID, ArrayList<Integer> ranking, Register register) {
        this.ID = ID;
        this.ranking = ranking;
        this.register = register;
    }

    public void run() {
        while (!register.assignmentComplete())
            if (register.getMode() == "processStudents")
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            else { // Time for the projects to execute
                register.increaseProjectCounter(); //Even if I dont execute, I was active
                if (isProposedTo == true) {
                    list = register.getList(ID);
                    student = list.get(0);
                    highestPriority = ranking.get(student.getID());

                    for (int i = 1; i < register.getList(ID).size(); i++) {
                        list = register.getList(i);
                        newStudent = list.get(i);
                        if (ranking.get(newStudent.getID()) > highestPriority) {
                            highestPriority = ranking.get(newStudent.getID());
                            student = newStudent;
                        }
                    }
                    register.keepOnly(student, ID);
                    if (register.getProjectCounter() == register.getTotalNumber())
                         {
                             register.setMode("processStudents");
                             register.notifyAll();
                         }
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
    }

    public int getID(){
        return this.ID;
    }

    public void setIsProposedTo(boolean bool)
    {
        this.isProposedTo=bool;
    }
}
