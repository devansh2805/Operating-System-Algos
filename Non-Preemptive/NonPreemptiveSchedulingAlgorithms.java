import java.util.Scanner;
// Non-Preemptive SchedulingAlgorithms
abstract class NonPreemptiveSchedulingAlgorithms {
	static int numberOfProcesses = 0;
	public static void main(String[] args) {
		int choice = 0, i = 0;
		Scanner scannerObject = new Scanner(System.in);
		boolean flag = true;
		System.out.print("Enter Number of Processes to Schedule: ");
		numberOfProcesses = scannerObject.nextInt();
		Process[] processes = new Process[numberOfProcesses];
		int[] processPriority = new int[numberOfProcesses];
		int[] processArrivalTime = new int[numberOfProcesses];
		int[] processCpuBurstTime = new int[numberOfProcesses];
		int[] priorityArray = new int[numberOfProcesses];
		System.out.print("Enter Arrival time of each Process: ");
		for(i=0;i<numberOfProcesses; i++) {
		 	processArrivalTime[i] = scannerObject.nextInt();
		}
		System.out.print("Enter CPU Burst time of each Process: ");
		for(i=0;i<numberOfProcesses; i++) {
		 	processCpuBurstTime[i] = scannerObject.nextInt();
		}
		System.out.print("Enter Priority of each Process: ");
		for(i=0;i<numberOfProcesses; i++) {
		 	priorityArray[i] = scannerObject.nextInt();
		}
		while(flag) {
			System.out.println();
			System.out.println("1. First Come First Serve Scheduling");
			System.out.println("2. Shortest Job First Scheduling");
			System.out.println("3. Priority Scheduling");
			System.out.println("4. End Program");
			System.out.print("Enter Scheduling Algorithm to use: ");
			choice = scannerObject.nextInt();
			if(choice >= 4 || choice <= 0) {
				flag = false;
				break;
			}
			switch(choice) {
				case 1:
					for(i=0;i<numberOfProcesses;i++) {
						processes[i] = new Process(i+1, processArrivalTime[i], processCpuBurstTime[i]);
					}
					System.out.println("----------------------------------------First Come First Serve Scheduling----------------------------------------");
					FirstComeFirstServe fcfsObject = new FirstComeFirstServe(processes);
					fcfsObject.getProcessSequence();
					fcfsObject.getProcessMetrics();
					System.out.println();
					System.out.println("Average Completion Time......: " + NonPreemptiveSchedulingAlgorithms.getAverageCompletionTime(processes));
					System.out.println("Average Turn Around Time.....: " + NonPreemptiveSchedulingAlgorithms.getAverageTurnAroundTime(processes));
					System.out.println("Average Waiting Time.........: " + NonPreemptiveSchedulingAlgorithms.getAverageWaitingTime(processes));
					System.out.println("\n-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x----");
					break;
				case 2:
					for(i=0;i<numberOfProcesses;i++) {
						processes[i] = new Process(i+1, processArrivalTime[i], processCpuBurstTime[i]);
					}
					System.out.println("------------------------------------------Shortest Job First Scheduling------------------------------------------");
					ShortestJobFirst sjfObject = new ShortestJobFirst(processes);
					sjfObject.getProcessSequence();
					sjfObject.getProcessMetrics();
					System.out.println();
					System.out.println("Average Completion Time......: " + NonPreemptiveSchedulingAlgorithms.getAverageCompletionTime(processes));
					System.out.println("Average Turn Around Time.....: " + NonPreemptiveSchedulingAlgorithms.getAverageTurnAroundTime(processes));
					System.out.println("Average Waiting Time.........: " + NonPreemptiveSchedulingAlgorithms.getAverageWaitingTime(processes));
					System.out.println("\n-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x----");
					break;
				case 3:
					for(i=0;i<numberOfProcesses;i++) {
						processes[i] = new Process(i+1, processArrivalTime[i], processCpuBurstTime[i], priorityArray[i]);
					}
					System.out.println("-------------------------------------------------Priority Scheduling-------------------------------------------------");
					PriorityScheduling psObject = new PriorityScheduling(processes);
					psObject.getProcessSequence();
					psObject.getProcessMetrics();
					System.out.println();
					System.out.println("Average Completion Time......: " + NonPreemptiveSchedulingAlgorithms.getAverageCompletionTime(processes));
					System.out.println("Average Turn Around Time.....: " + NonPreemptiveSchedulingAlgorithms.getAverageTurnAroundTime(processes));
					System.out.println("Average Waiting Time.........: " + NonPreemptiveSchedulingAlgorithms.getAverageWaitingTime(processes));
					System.out.println("\n-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x---");
					break;
			}
		}
		scannerObject.close();
	}

	abstract public void getProcessSequence();

	abstract public void getProcessMetrics();

	public static void swapProcessInfo(Process[] processArray, int firstIndex, int secondIndex) {
		Process temp;
		temp = processArray[firstIndex];
		processArray[firstIndex] = processArray[secondIndex];
		processArray[secondIndex] = temp;
	}

	public static void printGanttChart(Process[] processQueue, String schedulingAlgorithmName) {
		System.out.println();
		System.out.println("Gantt Chart For " + schedulingAlgorithmName + " is: ");
		System.out.print("Process Sequence is: ");
		for(Process process: processQueue) {
			System.out.print("P"+process.processIndex);
			for(int i=0;i<process.cpuBurstTime;i++) {
				System.out.print("-");
			}
		}
		System.out.println("End");
		System.out.print("            Time is: ");
		for(Process process: processQueue) {
			int time = process.arrivalTime + process.waitingTime;
			System.out.print(time);
			if(String.valueOf(time).length() == 1) {
				System.out.print(" ");
			}
			for(int i=0;i<process.cpuBurstTime;i++) {
				System.out.print("-");
			}
		}
		System.out.println(processQueue[processQueue.length-1].completionTime);
	}

	public static void readyQueueStatus(Process[] processQueue) {
		System.out.println("Process Switching Ready Queue Status: ");
		System.out.println("Time t = " + processQueue[0].arrivalTime + " P" + processQueue[0].processIndex);
		for(int i=1;i<numberOfProcesses;i++) {
			int time = processQueue[i-1].completionTime;
			System.out.print("Time t = " + time + " ");
			for(int j=i;j<numberOfProcesses;j++) {
				if(processQueue[j].arrivalTime <= time) {
					System.out.print("P"+processQueue[j].processIndex+" ");
				}
			}
			System.out.println();
		}
		System.out.println("Time t = " + processQueue[processQueue.length-1].completionTime + " Complete");
	}

	public static void getProcessCompletionTime(Process[] processQueue) {
		for(int i=0;i<processQueue.length;i++) {
			if(i==0) {
				processQueue[i].completionTime = processQueue[i].cpuBurstTime - processQueue[i].arrivalTime;
			} else {
				processQueue[i].completionTime = processQueue[i-1].completionTime + processQueue[i].cpuBurstTime;
			}
		}
	}

	public static void getProcessTurnAroundTime(Process[] processQueue) {
		for(int i=0;i<processQueue.length;i++) {
			processQueue[i].turnAroundTime = processQueue[i].completionTime - processQueue[i].arrivalTime;
		}
	}

	public static void getProcessWaitingTime(Process[] processQueue) {
		for(int i=0;i<processQueue.length;i++) {
			processQueue[i].waitingTime = processQueue[i].turnAroundTime - processQueue[i].cpuBurstTime;
		}
	}

	public static float getAverageCompletionTime(Process[] processQueue) {
		int completionTimeSum = 0;
		for(Process process: processQueue) {
			completionTimeSum += process.completionTime;
		}
		return (float)completionTimeSum/(float)processQueue.length;
	}

	public static float getAverageTurnAroundTime(Process[] processQueue) {
		int turnAroundTimeSum = 0;
		for(Process process: processQueue) {
			turnAroundTimeSum += process.turnAroundTime;
		}
		return (float)turnAroundTimeSum/(float)processQueue.length;
	}

	public static float getAverageWaitingTime(Process[] processQueue) {
		int waitingTimeSum = 0;
		for(Process process: processQueue) {
			waitingTimeSum += process.waitingTime;
		}
		return (float)waitingTimeSum/(float)processQueue.length;
	}

	public String padRight(String s, int n) {
    	return String.format("%-"+n+"s",s);  
	}
}

class Process {
	int processIndex;
	int arrivalTime; 
	int cpuBurstTime;
	int completionTime;
	int turnAroundTime;
	int waitingTime;
	int priority;
	boolean completionFlag;
	int schedulingIndex;

	Process(int processIndex, int arrivalTime, int cpuBurstTime) {
		this.processIndex = processIndex;
		this.arrivalTime = arrivalTime;
		this.cpuBurstTime = cpuBurstTime;
		this.completionTime = 0;
		this.turnAroundTime = 0;
		this.waitingTime = 0;
		this.completionFlag = false;
		this.schedulingIndex = 0;
		this.priority = 0;
	}

	Process(int processIndex, int arrivalTime, int cpuBurstTime, int priority) {
		this.processIndex = processIndex;
		this.arrivalTime = arrivalTime;
		this.cpuBurstTime = cpuBurstTime;
		this.completionTime = 0;
		this.turnAroundTime = 0;
		this.waitingTime = 0;
		this.completionFlag = false;
		this.schedulingIndex = 0;
		this.priority = priority;
	}
}

class FirstComeFirstServe extends NonPreemptiveSchedulingAlgorithms{
	Process[] processQueue;
	
	FirstComeFirstServe(Process[] processQueue) {
		this.processQueue = processQueue;
	}

	@Override
	public void getProcessSequence() {
		System.out.println();
		for(int i=0;i<numberOfProcesses;i++) {
			for(int j=0;j<numberOfProcesses-i-1;j++) {
				if(processQueue[j+1].arrivalTime < processQueue[j].arrivalTime) {
					NonPreemptiveSchedulingAlgorithms.swapProcessInfo(processQueue, j, j+1);
				}
			}
		}
	}

	@Override
	public void getProcessMetrics() {
		NonPreemptiveSchedulingAlgorithms.getProcessCompletionTime(processQueue);
		NonPreemptiveSchedulingAlgorithms.getProcessTurnAroundTime(processQueue);
		NonPreemptiveSchedulingAlgorithms.getProcessWaitingTime(processQueue);
		NonPreemptiveSchedulingAlgorithms.readyQueueStatus(processQueue);
		NonPreemptiveSchedulingAlgorithms.printGanttChart(processQueue, "First Come First Serve Scheduling");
		String[] processHeaders = {"Process", "Arrival Time", "CPU Burst Time", "Completion Time", "TurnAround Time", "Waiting Time"};
		System.out.print(" ");
		for(int i=0;i<107;i++) {
			System.out.print("-");
		}
		System.out.println();
		for(int j=0;j<6;j++) {
			System.out.print(padRight("| "+processHeaders[j], 18));
		}
		System.out.println("|");
		System.out.print(" ");
		for(int i=0;i<107;i++) {
			System.out.print("-");
		}
		System.out.println(" ");
		for(int i=0;i<numberOfProcesses;i++) {
			System.out.print("| "+padRight("P" + Integer.toString(processQueue[i].processIndex), 16) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].arrivalTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].cpuBurstTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].completionTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].turnAroundTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].waitingTime), 16) + "| ");
			System.out.println();
		}
		System.out.print(" ");
		for(int i=0;i<107;i++) {
			System.out.print("-");
		}
	}
}

class ShortestJobFirst extends NonPreemptiveSchedulingAlgorithms{
	Process[] processQueue;

	ShortestJobFirst(Process[] processQueue) {
		this.processQueue = processQueue;
	}

	@Override
	public void getProcessSequence() {
		int time = 0, count = 0;
		while(true) {
			int index = 0, min = Integer.MAX_VALUE;
			if(count == numberOfProcesses) {
				break;
			}
			for(int i=0;i<numberOfProcesses;i++) {
				if(processQueue[i].arrivalTime <= time && !processQueue[i].completionFlag) {
					if(processQueue[i].cpuBurstTime < min) {
						min = processQueue[i].cpuBurstTime;
						index = i;
					}
				}
			}
			time += processQueue[index].cpuBurstTime;
			processQueue[index].schedulingIndex = count;
			count++;
			processQueue[index].completionFlag = true;
		}
		for(int i=0;i<numberOfProcesses;i++) {
			for(int j=0;j<numberOfProcesses-i-1;j++) {
				if(processQueue[j+1].schedulingIndex < processQueue[j].schedulingIndex) {
					NonPreemptiveSchedulingAlgorithms.swapProcessInfo(processQueue, j, j+1);
				}
			}
		}
	}

	@Override
	public void getProcessMetrics() {
		NonPreemptiveSchedulingAlgorithms.getProcessCompletionTime(processQueue);
		NonPreemptiveSchedulingAlgorithms.getProcessTurnAroundTime(processQueue);
		NonPreemptiveSchedulingAlgorithms.getProcessWaitingTime(processQueue);
		NonPreemptiveSchedulingAlgorithms.readyQueueStatus(processQueue);
		NonPreemptiveSchedulingAlgorithms.printGanttChart(processQueue, "Shortest Job First Scheduling");
		String[] processHeaders = {"Process", "Arrival Time", "CPU Burst Time", "Completion Time", "TurnAround Time", "Waiting Time"};
		System.out.print(" ");
		for(int i=0;i<107;i++) {
			System.out.print("-");
		}
		System.out.println();
		for(int j=0;j<6;j++) {
			System.out.print(padRight("| "+processHeaders[j], 18));
		}
		System.out.println("|");
		System.out.print(" ");
		for(int i=0;i<107;i++) {
			System.out.print("-");
		}
		System.out.println(" ");
		for(int i=0;i<numberOfProcesses;i++) {
			System.out.print("| "+padRight("P" + Integer.toString(processQueue[i].processIndex), 16) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].arrivalTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].cpuBurstTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].completionTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].turnAroundTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].waitingTime), 16) + "| ");
			System.out.println();
		}
		System.out.print(" ");
		for(int i=0;i<107;i++) {
			System.out.print("-");
		}
	}
}

class PriorityScheduling extends NonPreemptiveSchedulingAlgorithms {
	Process[] processQueue;

	PriorityScheduling(Process[] processQueue) {
		this.processQueue = processQueue;
	}

	@Override
	public void getProcessSequence() {
		int time = 0, count = 0;
		while(true) {
			int index = 0, min = Integer.MAX_VALUE;
			if(count == numberOfProcesses) {
				break;
			}
			for(int i=0;i<numberOfProcesses;i++) {
				if(processQueue[i].arrivalTime <= time && !processQueue[i].completionFlag) {
					if(processQueue[i].priority < min) {
						min = processQueue[i].priority;
						index = i;
					}
				}
			}
			time += processQueue[index].cpuBurstTime;
			processQueue[index].schedulingIndex = count;
			count++;
			processQueue[index].completionFlag = true;
		}
		for(int i=0;i<numberOfProcesses;i++) {
			for(int j=0;j<numberOfProcesses-i-1;j++) {
				if(processQueue[j+1].schedulingIndex < processQueue[j].schedulingIndex) {
					NonPreemptiveSchedulingAlgorithms.swapProcessInfo(processQueue, j, j+1);
				}
			}
		}
	}

	@Override
	public void getProcessMetrics() {
		NonPreemptiveSchedulingAlgorithms.getProcessCompletionTime(processQueue);
		NonPreemptiveSchedulingAlgorithms.getProcessTurnAroundTime(processQueue);
		NonPreemptiveSchedulingAlgorithms.getProcessWaitingTime(processQueue);
		NonPreemptiveSchedulingAlgorithms.readyQueueStatus(processQueue);
		NonPreemptiveSchedulingAlgorithms.printGanttChart(processQueue, "Priority Scheduling");
		String[] processHeaders = {"Process", "Priority", "Arrival Time", "CPU Burst Time", "Completion Time", "TurnAround Time", "Waiting Time"};
		System.out.print(" ");
		for(int i=0;i<113;i++) {
			System.out.print("-");
		}
		System.out.println();
		for(int j=0;j<7;j++) {
			if(j == 0) {
				System.out.print(padRight("| "+processHeaders[j], 12));
			} else {
				System.out.print(padRight("| "+processHeaders[j], 17));
			}
		}
		System.out.println("|");
		System.out.print(" ");
		for(int i=0;i<113;i++) {
			System.out.print("-");
		}
		System.out.println(" ");
		for(int i=0;i<numberOfProcesses;i++) {
			System.out.print("| "+padRight("P" + Integer.toString(processQueue[i].processIndex), 10) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].priority), 15) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].arrivalTime), 15) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].cpuBurstTime), 15) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].completionTime), 15) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].turnAroundTime), 15) + "| ");
			System.out.print(padRight(Integer.toString(processQueue[i].waitingTime), 15) + "| ");
			System.out.println();
		}
		System.out.print(" ");
		for(int i=0;i<113;i++) {
			System.out.print("-");
		}
	}
}