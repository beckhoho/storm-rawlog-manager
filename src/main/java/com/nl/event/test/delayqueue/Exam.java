/**
 * @author: gsw
 * @version: 1.0
 * @CreateTime: 2015年12月25日 下午2:46:59
 * @Description: 无
 */
package com.nl.event.test.delayqueue;

import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Exam {
	static final int STUDENT_SIZE = 45;

	public static void main(String[] args) {
		Random r = new Random();
		DelayQueue<Student> students = new DelayQueue<Student>();
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 0; i < STUDENT_SIZE; i++) {
			students.put(new Student("学生" + (i + 1), 3000 + r.nextInt(9000)));
		}
		//students.put(new EndExam(12000, exec));// 1200为考试结束时间
		exec.execute(new Teacher(students));//, exec));

		students.put(new Student("学生" + (100), 3000 + r.nextInt(9000)));
		
		try {
			Thread.sleep(10000);// 间隔
			students.put(new Student("学生" + (101), 10000));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}