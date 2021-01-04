import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
import java.util.Scanner;
import java.util.Random;

public class ProcessSynchronization {
	public static void main(String[] args) {
		int number;
		Scanner input = new Scanner(System.in);
		System.out.print("Enter number of Philosophers: ");
		number = input.nextInt();
		Monitor monitor = new Monitor(number);
		Philosopher philosophers[] = new Philosopher[number];
		for(int i=0;i<number;i++) {
			philosophers[i] = new Philosopher(monitor, i);
		}
		for(int i=0;i<number;i++) {
			philosophers[i].start();
		}
		input.close();
	}
}

enum State {
	THINKING, 
	EATING, 
	HUNGRY
}

class Philosopher extends Thread {
	Monitor monitor;
	int philosopherId;
	
	Philosopher(Monitor monitor, int philosopherId) {
		this.monitor = monitor;
		this.philosopherId = philosopherId;
	}
	
	@Override
	public void run() {
		try {
			Thread.sleep(new Random().nextInt(100));
			do {
				monitor.pickUpChopstick(this.philosopherId);
				Thread.sleep(100);
				monitor.putDownChopstick(this.philosopherId);
			} while(true);
		} catch(InterruptedException interruptedException) {
			interruptedException.printStackTrace();
		}
	}
}

class Monitor {
	
	private final Lock lockReference;
	private final Condition chopstick[];
	private final int philosophersCount;
	State philosopherState[];
	
	Monitor(int numberOfPhilosophers) {
		philosophersCount = numberOfPhilosophers;
		lockReference = new ReentrantLock();
		chopstick = new Condition[numberOfPhilosophers];
		philosopherState = new State[numberOfPhilosophers];
		
		for(int i=0;i<numberOfPhilosophers;i++) {
			chopstick[i] = lockReference.newCondition();
			philosopherState[i] = State.THINKING;
		}
	}
	
	public void pickUpChopstick(int philosopherNumber) throws InterruptedException {
		lockReference.lock();
		philosopherState[philosopherNumber] = State.HUNGRY;
		System.out.println("\033[0;31m" + "Philosopher " + (philosopherNumber + 1) + " is Hungry" + "\033[0m");
		test(philosopherNumber);
		if(philosopherState[philosopherNumber] != State.EATING) {
			chopstick[philosopherNumber].await();
		}
		lockReference.unlock();
	}
	
	public void putDownChopstick(int philosopherNumber) {
		lockReference.lock();
		philosopherState[philosopherNumber] = State.THINKING;
		System.out.println("\033[0;37m" + "Philosopher " + (philosopherNumber + 1) + " is Thinking" + "\033[0m");
		test((philosopherNumber - 1 + philosophersCount) % philosophersCount);
		test((philosopherNumber + 1) % philosophersCount);
		lockReference.unlock();
	}
	
	public void test(int philosopherNumber) {
		State neighbourLeftState = philosopherState[(philosopherNumber - 1 + philosophersCount) % philosophersCount];
		State neighbourRightState = philosopherState[(philosopherNumber + 1) % philosophersCount];
		State selfState = philosopherState[philosopherNumber];
		if(neighbourLeftState != State.EATING && selfState == State.HUNGRY && neighbourRightState != State.EATING) {
			philosopherState[philosopherNumber] = State.EATING;
			System.out.println("Philosopher " + (philosopherNumber + 1) + " is Eating");
			chopstick[philosopherNumber].signal();
		}
		else {
			if(selfState == State.HUNGRY && neighbourLeftState == State.EATING) {
				System.out.println("\033[0;35m" + "Philosopher " + (philosopherNumber + 1) + " is Waiting for Chopstick " + ( ( (philosopherNumber - 1 + philosophersCount) % philosophersCount ) + 1) + "\033[0m");
			} else if(selfState == State.HUNGRY && neighbourRightState == State.EATING) {
				System.out.println("\033[0;35m" + "Philosopher " + (philosopherNumber + 1) + " is Waiting for Chopstick " + ( ( (philosopherNumber + 1) % philosophersCount ) + 1) + "\033[0m");		
			}
		}
	}
}