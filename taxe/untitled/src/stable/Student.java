package stable;


import java.util.ArrayList;

public class Student extends Thread {
    private int ID;
    private boolean isRejected = true;
    public int rejections = 0;
    private ArrayList<Integer> ranking;
    private Register register;

    public Student(int ID, ArrayList<Integer> ranking, Register register) {
        this.ID = ID;
        this.ranking = ranking;
        this.register = register;
    }


    public void run() {
            while (!register.assignmentComplete())                              // While the assignment is not complete
                if (register.getMode() != "processStudents")                    // If the thread runs in the wrong mode
                    try {
                        this.wait();                                            // It becomes inactive, waiting to be
                    } catch (InterruptedException e) {                          // Notified of changed of mode
                        e.printStackTrace();
                    }

                else {                                                          // Time for students to execute
                    register.increaseStudentCounter();                          // Even if thread does not execute, it was active this mode
                    if (this.isRejected)
                        register.put(this, ranking.get(rejections));            // Record proposal into register
                    if (register.getStudentCounter() == register.getTotalNumber()) //If this is the last student
                    {
                        register.setMode("processProjects");                    // Change mode
                        register.notifyAll();                                   // Notify all threads of change of mode
                    }
                    try {
                        this.wait();                                            // Become inactive upon completion of job
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
    }

    }

    public void setIsRejected(boolean rejected){
        this.isRejected=rejected;
    }

    public int getID(){
        return this.ID;
    }

    public void increaseRejections(){
        this.rejections++;
    }
}
