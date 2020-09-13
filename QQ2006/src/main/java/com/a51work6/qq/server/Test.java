package com.a51work6.qq.server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
* @author 作者 E-mail:
* @version 创建时间：2020年9月8日 下午4:03:59
* 类说明
*/
public class Test {
	public static void main(String[] args) {
		ReentrantLockCondition rt = new ReentrantLockCondition();
		Thread t = new Thread(rt);
		
		t.start();
		
		
		try {
			Thread.sleep(2000);
			rt.lock.lock();
			rt.cond.signal();
			System.out.println("main signal，只是让await线程唤醒，之后去争夺锁。而不是让其直接获得锁");
			Thread.sleep(2000);//sleep2s后释放锁
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			rt.lock.unlock(); //在没有释放锁时，main线程会一直占用锁。 而不是signal后就释放锁了。
		}
		
		System.out.println("main end");
		
	}

}

class ReentrantLockCondition implements Runnable{
	
	public static ReentrantLock lock = new ReentrantLock();
	public static Condition cond = lock.newCondition();

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		try {
			lock.lock();
			cond.await();
			System.out.println("Thread is going on");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			lock.unlock();
		}		
	}
	
	
	
}
