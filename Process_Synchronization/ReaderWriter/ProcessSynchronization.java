import java.util.LinkedList;
import java.util.Random;

class ProcessSynchronization {
	public static ReaderWriter memoryReference;
	public static void main(String[] args) {
		int i = 0, n = 5, m = 5;
		memoryReference = new ReaderWriter();
		Reader[] readerThreads = new Reader[n];
		Writer[] writerThreads = new Writer[m];
		for(i=0;i<n;i++) {
			readerThreads[i] = new Reader();
			readerThreads[i].setName(Integer.toString(i+1));
		}
		for(i=0;i<m;i++) {
			writerThreads[i] = new Writer();
			writerThreads[i].setName(Integer.toString(i+1));
		}
		
		for(i=0;i<n;i++) {
			readerThreads[i].start();
		}
		for(i=0;i<m;i++) {
			writerThreads[i].start();
		}	
	}

	static class Reader extends Thread {
		@Override
		public void run() {
			try {
				while(true) {
					memoryReference.mutex.waitSemaphore(null, null);
					memoryReference.readCount++;		
					if(memoryReference.readCount == 1) {
						memoryReference.write.waitSemaphore("\033[0;31m" + "Reader " + Thread.currentThread().getName() + " waiting...." + "\033[0m", "\033[1;34m" + "Read Count when Reader " + Thread.currentThread().getName() + " Entering Critical Section : " + memoryReference.readCount + "\033[0m");
					} else {
						System.out.println("\033[1;34m" + "Read Count when Reader " + Thread.currentThread().getName() + " Entering Critical Section : " + memoryReference.readCount + "\033[0m");
					}
					memoryReference.mutex.signalSemaphore(null);
					memoryReference.read();
					memoryReference.mutex.waitSemaphore(null, null);
					memoryReference.readCount--;
					System.out.println("\033[1;32m" + "Reading Finished by Reader " + Thread.currentThread().getName() + "\033[0m");
					System.out.println("\033[1;35m" + "Read Count when Reader " + Thread.currentThread().getName() + " Exiting Critical Section : " + memoryReference.readCount + "\033[0m");
					if(memoryReference.readCount == 0) {
						memoryReference.write.signalSemaphore("Reading Finished. Writers Can Enter");
					}
					memoryReference.mutex.signalSemaphore(null);
					Thread.sleep(new Random().nextInt(10));
				}
			} catch(InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}

	static class Writer extends Thread {
		@Override
		public void run() {
			try {
				while(true) {
					memoryReference.write.waitSemaphore("\033[1;33m" + "Writer " + Thread.currentThread().getName() + " waiting...." + "\033[0m", "\033[0;33m" + "Writer " + Thread.currentThread().getName() + " writing...." + "\033[0m");
					memoryReference.write();
					memoryReference.write.signalSemaphore("\033[1;36m" + "Writer " + Thread.currentThread().getName() + " finished Writing" + "\033[0m");
					Thread.sleep(new Random().nextInt(10));
				}
			} catch(InterruptedException interruptedException) {
				interruptedException.printStackTrace();
			}
		}
	}
}

class ReaderWriter {
	private LinkedList<String> dataObject = new LinkedList<String>();
	public Random randomObject = new Random();
	public Semaphore mutex = new Semaphore(1);
	public Semaphore write = new Semaphore(1);
	public int readCount = 0;

	public void read() {
		synchronized(this) {
			System.out.println("\033[0;37m" + "Reader " + Thread.currentThread().getName() + " Reading....." + "\033[0m");
			String storedData = "";
			for(String dataElement : this.dataObject) {
				storedData += dataElement; 
			}
		}
	}

	public void write() {
		synchronized(this) {
			int data = this.randomObject.nextInt(100);
			this.dataObject.add(Integer.toString(data) + " ");
		}
	}
}

class Semaphore {
	private int value;

	Semaphore(int value) {
		this.value = value;
	}

	public void waitSemaphore(String message, String otherMessage) throws InterruptedException {
		synchronized(this) {
			while(this.value<=0) {
				if(message != null) {
					System.out.println(message);
					//Thread.sleep(1000);
				}
				this.wait();
			}
			if(otherMessage != null) {
				System.out.println(otherMessage);
			}
			this.value--;
		}
	}

	public void signalSemaphore(String message) throws InterruptedException {
		synchronized(this) {
			if(message != null) {
				System.out.println(message);
			}
			this.value++;
			this.notify();
			//Thread.sleep(1000);
		}
	}
}