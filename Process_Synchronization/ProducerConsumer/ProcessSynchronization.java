import java.util.Random;

class ProcessSynchronization {
	static ProducerConsumer memoryReference;
	public static void main(String[] args) {
		int i = 0, n = 5;
		memoryReference = new ProducerConsumer();
		Producer[] producerThreads = new Producer[n];
		Consumer[] consumerThreads = new Consumer[n]; 
		for(i=0;i<n;i++) {
			producerThreads[i] = new Producer();
			consumerThreads[i] = new Consumer();
			producerThreads[i].setName(Integer.toString(i+1));
			consumerThreads[i].setName(Integer.toString(i+1));
		}
		System.out.println("\033[1;35m" + "Bounded Buffer is a Circular Queue of size: " + memoryReference.MAX_NUMBER_OF_ELEMENTS + "\u001B[0m");
		for(i=0;i<n;i++) {
			consumerThreads[i].start();
			producerThreads[i].start();
		}
	}

	static class Producer extends Thread {
		@Override
		public void run()  {
			try {
				while(true) {
					Thread.sleep(memoryReference.randomGenerator.nextInt(1000));
					String waitingMessage = "\u001B[37m" + "Circular Queue is Full, Producer " + Thread.currentThread().getName() + " Waiting...." + "\u001B[0m";
					memoryReference.empty.waitSemaphore(waitingMessage);
					memoryReference.mutex.waitSemaphore(null);
					memoryReference.produce();
					memoryReference.mutex.signalSemaphore();
					memoryReference.full.signalSemaphore();
				}
			} catch (InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	static class Consumer extends Thread {
		@Override
		public void run() {
			try {
				while(true) {
					Thread.sleep(memoryReference.randomGenerator.nextInt(1300));
					String waitingMessage = "\u001B[36m" + "Circular Queue is Empty, Consumer " + Thread.currentThread().getName() + " Waiting...." + "\u001B[0m";
					memoryReference.full.waitSemaphore(waitingMessage);
					memoryReference.mutex.waitSemaphore(null);
					memoryReference.consume();
					memoryReference.mutex.signalSemaphore();
					memoryReference.empty.signalSemaphore();
				}
			} catch(InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}
}

class ProducerConsumer {
	public CircularQueue boundedBuffer;
	public Random randomGenerator;
	static int MAX_NUMBER_OF_ELEMENTS = 3;
	Semaphore mutex = new Semaphore(1);
	Semaphore empty = new Semaphore(MAX_NUMBER_OF_ELEMENTS);
	Semaphore full = new Semaphore(0);

	ProducerConsumer() {
		boundedBuffer = new CircularQueue(MAX_NUMBER_OF_ELEMENTS);
		randomGenerator = new Random();
	}

	public void produce() {
		synchronized(this) {
			int producerValue = randomGenerator.nextInt(100);
			boundedBuffer.insert(producerValue);
			System.out.println("Producer " + Thread.currentThread().getName() + " produced : " + producerValue + " " + "\u001B[33m" + boundedBuffer.printQueue() + "\u001B[0m");
		}
	}

	public void consume() {
		synchronized(this) {
			System.out.println("\u001B[31m" + "Consumer " + Thread.currentThread().getName() + " consumed : " + boundedBuffer.delete() + " " + "\u001B[33m" + boundedBuffer.printQueue() + "\u001B[0m");
		}
	}
}

class Semaphore {
	private int value;

	Semaphore(int value) {
		this.value = value;
	}

	public void waitSemaphore(String message) throws InterruptedException {
		synchronized(this) {
			while(this.value<=0) {
				if(message!=null) {
					System.out.println(message);
				}
				this.wait();
			}
			this.value--;
		}
	}

	public void signalSemaphore() throws InterruptedException {
		synchronized(this) {
			this.notify();
			this.value++;
		}
	}
}

class CircularQueue {
	class Node {
		int data;
		Node link;
	}

	Node front, rear;
	int size;

	CircularQueue(int size) {
		front = null;
		rear = null;
		this.size = size;
		initQueue();
	}

	public void initQueue() {
		for(int i=0;i<this.size;i++) {
			Node newNode = new Node();
			newNode.data = -1;
			if(front == null && rear == null) {
				newNode.link = null;
				front = newNode;
				rear = newNode;
			} else {
				if(i == this.size-1) {
					newNode.link = front;
				} else {
					newNode.link = null;
				}
				rear.link = newNode;
				rear = newNode;
			}
		}
		rear = front;
	}

	public void insert(int data) {
		synchronized(this) {
			rear.data = data;
			rear = rear.link;
		}
	}

	public int delete() {
		synchronized(this) {
			int currentFrontValue = front.data;
			front.data = -1;
			front = front.link;
			return currentFrontValue;
		}
	}

	public String printQueue() {
		synchronized(this) {
			Node traversePointer = this.front;
			String queue = "Queue is: ";
			String queueData = "";
			if(this.front == this.rear) {
				if(this.front.data != -1) {
					queueData += this.front.data + " ";
					traversePointer = traversePointer.link;
				}
			}
			while(traversePointer != this.rear) {
				queueData += traversePointer.data + " ";
				traversePointer = traversePointer.link;
			}
			if(queueData.equals("")) {
				return queue + "Empty";
			} else {
				return queue + queueData;
			}
		}
	} 
}