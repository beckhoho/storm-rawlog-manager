/**
* @author: gsw
* @version: 1.0
* @CreateTime: 2015年12月25日 下午2:45:16
* @Description: 无
*/
package com.nl.event.test.delayqueue;
import java.util.concurrent.DelayQueue;  
import java.util.concurrent.ExecutorService;  
class Teacher implements Runnable{    
    private DelayQueue<Student> students;    
   // private ExecutorService exec;    
        
    public Teacher(DelayQueue<Student> students) {//,ExecutorService exec) {    
        super();    
        this.students = students;    
        //this.exec = exec;    
    }    
    
    
    @Override    
    public void run() {    
        try {    
            System.out.println("考试开始……");    
            while (!Thread.interrupted()) {    
                students.take().run();    
            }    
            System.out.println("考试结束……");    
        } catch (InterruptedException e) {    
            e.printStackTrace();    
        }    
    
    }    
        
}    