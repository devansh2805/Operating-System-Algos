import java.util.Scanner;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Vector;

abstract class PreemptiveSchedulingAlgorithms {
	static class Process {
		int processIndex;
		int arrivalTime;
		int cpuBurstTime;
		int completionTime;
		int turnAroundTime;
		int waitingTime;
		int priority;
		int remainingTime;

		Process(int processIndex, int arrivalTime, int cpuBurstTime, int priority) {
			this.processIndex = processIndex;
			this.arrivalTime = arrivalTime;
			this.cpuBurstTime = cpuBurstTime;
			this.completionTime = 0;
			this.turnAroundTime = 0;
			this.waitingTime = 0;
			this.priority = priority;
			this.remainingTime = this.cpuBurstTime;
		}

		public void resetProcessValues() {
			this.completionTime = 0;
			this.turnAroundTime = 0;
			this.waitingTime = 0;
			this.remainingTime = this.cpuBurstTime;
		}
	}

	public static Queue<Process> readyQueue = new LinkedList<Process>();
	public static Process[] processInfo;
	public static int numberOfProcesses = 0;
	public static int quantumTime = 0;

	public static void main(String[] args) {
		int i = 0, choice = 0;
		Scanner scannerObject = new Scanner(System.in);
		System.out.print("Enter number of processes: ");
		numberOfProcesses = scannerObject.nextInt();
		processInfo = new Process[numberOfProcesses]; 
		int[] processArrivalTime = new int[numberOfProcesses];
		int[] processCpuBurstTime = new int[numberOfProcesses];
		int[] processPriority = new int[numberOfProcesses];
		System.out.print("Enter Arrival Time of each process: ");
		for (i=0;i<numberOfProcesses;i++) {
			processArrivalTime[i] = scannerObject.nextInt();
		}
		System.out.print("Enter CPU Burst Time time of each process: ");
		for (i=0;i<numberOfProcesses;i++) {
			processCpuBurstTime[i] = scannerObject.nextInt();
		}
		System.out.print("Enter Priority of each processInfo: ");
		for (i=0;i<numberOfProcesses;i++) {
			processPriority[i] = scannerObject.nextInt();
		}
		for(i=0;i<numberOfProcesses;i++) {
			processInfo[i] = new Process(i+1, processArrivalTime[i], processCpuBurstTime[i], processPriority[i]);
		}
		System.out.print("Enter Quantum Time of CPU: ");
		quantumTime = scannerObject.nextInt();
		while(true) {
			System.out.println();
			System.out.println("1. Shortest Time Remaining First Scheduling");
			System.out.println("2. Priority Scheduling");
			System.out.println("3. Round Robin Scheduling");
			System.out.println("4. End Program");
			System.out.print("Enter Scheduling Algorithm to use: ");
			choice = scannerObject.nextInt();
			if(choice >= 4 || choice <= 0) {
				break;
			}
			switch(choice) {
				case 1:
					System.out.println("-------------------------------------Shortest Time Remaining First Scheduling-------------------------------------");
					ShortestTimeRemainingFirst strfObject = new ShortestTimeRemainingFirst();
					strfObject.getProcessSequence();
					strfObject.getProcessMetrics();
					System.out.println("\n-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----");
					break;
				case 2:
					System.out.println("--------------------------------------------------Priority Scheduling-------------------------------------------------");
					PriorityScheduling psObject = new PriorityScheduling();
					psObject.getProcessSequence();
					psObject.getProcessMetrics();
					System.out.println("\n-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x----");
					break;
				case 3:
					System.out.println("----------------------------------------------Round Robin Scheduling----------------------------------------------");
					RoundRobinAlgorithm rorObject = new RoundRobinAlgorithm();
					rorObject.getProcessSequence();
					rorObject.getProcessMetrics();
					System.out.println("\n-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----x-----");
					break;
			}
			printProcessMetrics();
			for(i=0;i<numberOfProcesses;i++) {
				processInfo[i].resetProcessValues();
			}
		}
		scannerObject.close();
	}

	abstract public void getProcessSequence();

	abstract public void getProcessMetrics();

	public static void printProcessMetrics() { 
		System.out.println();
		System.out.println("Average Completion Time.........: " + getAverageCompletionTime());
		System.out.println("Average TurnAround Time.........: " + getAverageTurnAroundTime());
		System.out.println("Average Waiting Time............: " + getAverageWaitingTime());
	}

	public static float getAverageCompletionTime() {
		int averageCompletionTimeSum = 0;
		for (Process process: processInfo) {
			averageCompletionTimeSum += process.completionTime;
		}
		return (float)averageCompletionTimeSum/(float)numberOfProcesses;
	}

	public static float getAverageTurnAroundTime() {
		int averageTurnAroundTimeSum = 0;
		for (Process process: processInfo) {
			averageTurnAroundTimeSum += process.turnAroundTime;
		}
		return (float)averageTurnAroundTimeSum/(float)numberOfProcesses;
	}

	public static float getAverageWaitingTime() {
		int averageWaitingTimeSum = 0;
		for (Process process: processInfo) {
			averageWaitingTimeSum += process.waitingTime;
		}
		return (float)averageWaitingTimeSum/(float)numberOfProcesses;
	}

	public String padRight(String s, int n) {
    	return String.format("%-"+n+"s",s);  
	}

	public void printReadyQueue(int time) {
		System.out.print("Ready Queue at time = " + padRight(Integer.toString(time),2) + " : ");
		for(Process process: readyQueue) {
			System.out.print("P" + process.processIndex+" ");
		}
		System.out.println();
	}
}

class ShortestTimeRemainingFirst extends PreemptiveSchedulingAlgorithms {
	StringBuilder processGanttChart;
	Vector<Integer> timeChart;
	
	ShortestTimeRemainingFirst() {
		processGanttChart = new StringBuilder();
		timeChart = new Vector<Integer>();
	}

	@Override
	public void getProcessSequence() {
		int time = 0;
		boolean tempFlag = true;
		while(!readyQueue.isEmpty() || tempFlag) {
			int min = Integer.MAX_VALUE, index = numberOfProcesses;
			Process currentProcessHeader = null;
			for(Process process : processInfo) {
				if(process.arrivalTime == time) {
					readyQueue.add(process);
					tempFlag = false;
				}
			}
			Iterator<Process> readyQueueIterator = readyQueue.iterator();
			while(readyQueueIterator.hasNext()) {
				currentProcessHeader = readyQueueIterator.next();
				if(currentProcessHeader.remainingTime <= min) {
					if(currentProcessHeader.remainingTime == 0) {
						for(Process process : processInfo) {
							if(process.processIndex == currentProcessHeader.processIndex) {
								process.completionTime = time;
								process.turnAroundTime = process.completionTime - process.arrivalTime;
							}
						} 
						readyQueueIterator.remove();
					} else {
						min = currentProcessHeader.remainingTime;
						index = currentProcessHeader.processIndex;
					}
				}
			}
			System.out.print("Ready Queue at time = " + padRight(Integer.toString(time),2) + " : ");
			for(Process process : readyQueue) {
				if(process.processIndex == index) {
					process.remainingTime--;
					processGanttChart.append(Integer.toString(index));
					timeChart.add(time++);
				} else {
					System.out.print("P" + process.processIndex+" ");
					process.waitingTime += 1;
				}
			}
			System.out.println();
		}
		int printIndex = 0;
		System.out.print("Process Gantt Chart: ");
		for(int i=0;i<processGanttChart.length();i++) {
			int printValue = Character.getNumericValue(processGanttChart.charAt(i));
			if(printValue != printIndex) {
				System.out.print("P"+printValue+"-");
				printIndex = printValue;
			} else {
				System.out.print("-");
			}
		}
		System.out.println("End");
		System.out.print("               Time: ");
		printIndex = 0;
		for(int i=0;i<processGanttChart.length();i++) {
			int printValue = Character.getNumericValue(processGanttChart.charAt(i));
			if(printValue != printIndex) {
				System.out.print(padRight(Integer.toString((int)timeChart.elementAt(i)), 2)+" ");
				printIndex = printValue;
			} else {
				System.out.print(" ");
			}
		}
		System.out.println(time);
	}

	@Override
	public void getProcessMetrics() {
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
			System.out.print("| "+padRight("P" + Integer.toString(processInfo[i].processIndex), 16) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].arrivalTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].cpuBurstTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].completionTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].turnAroundTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].waitingTime), 16) + "| ");
			System.out.println();
		}
		System.out.print(" ");
		for(int i=0;i<107;i++) {
			System.out.print("-");
		}
	}
}

class PriorityScheduling extends PreemptiveSchedulingAlgorithms {
	StringBuilder processGanttChart;
	Vector<Integer> timeChart;

	PriorityScheduling() {
		processGanttChart = new StringBuilder();
		timeChart = new Vector<Integer>();
	}

	@Override
	public void getProcessSequence() {
		int time = 0;
		boolean tempFlag = true;
		while(!readyQueue.isEmpty() || tempFlag) {
			int min = Integer.MAX_VALUE, index = numberOfProcesses;
			for(Process process : processInfo) {
				if(process.arrivalTime == time) {
					readyQueue.add(process);
					tempFlag = false;
				}
			}
			Iterator<Process> readyQueueIterator = readyQueue.iterator();
			while(readyQueueIterator.hasNext()) {
				Process currentProcessHeader = readyQueueIterator.next();
				if(currentProcessHeader.priority <= min) {
					if(currentProcessHeader.remainingTime == 0) {
						for(Process process : processInfo) {
							if(process.processIndex == currentProcessHeader.processIndex) {
								process.completionTime = time;
								process.turnAroundTime = process.completionTime - process.arrivalTime;
							}
						} 
						readyQueueIterator.remove();
					} else {
						min = currentProcessHeader.priority;
						index = currentProcessHeader.processIndex;
					}
				}
			}
			System.out.print("Ready Queue at time = " + padRight(Integer.toString(time),2) + " : ");
			for(Process process : readyQueue) {
				if(process.processIndex == index) {
					process.remainingTime--;
					processGanttChart.append(Integer.toString(index));
					timeChart.add(time++);
				} else {
					System.out.print("P" + process.processIndex+" ");
					process.waitingTime += 1;
				}
			}
		}
		int printIndex = 0;
		System.out.print("Process Gantt Chart: ");
		for(int i=0;i<processGanttChart.length();i++) {
			int printValue = Character.getNumericValue(processGanttChart.charAt(i));
			if(printValue != printIndex) {
				System.out.print("P"+printValue+"-");
				printIndex = printValue;
			} else {
				System.out.print("-");
			}
		}
		System.out.println("End");
		System.out.print("               Time: ");
		printIndex = 0;
		for(int i=0;i<processGanttChart.length();i++) {
			int printValue = Character.getNumericValue(processGanttChart.charAt(i));
			if(printValue != printIndex) {
				System.out.print(padRight(Integer.toString((int)timeChart.elementAt(i)), 2)+" ");
				printIndex = printValue;
			} else {
				System.out.print(" ");
			}
		}
		System.out.println(time);
	}

	@Override
	public void getProcessMetrics() {
		String[] processHeaders = {"Process", "Priority", "Arrival Time", "CPU Burst Time", "Completion Time", "TurnAround Time", "Waiting Time"};
		System.out.print(" ");
		for(int i=0;i<114;i++) {
			System.out.print("-");
		}
		System.out.println();
		System.out.print(padRight("| "+processHeaders[0], 13));
		System.out.print(padRight("| "+processHeaders[1], 13));
		System.out.print(padRight("| "+processHeaders[2], 17));
		System.out.print(padRight("| "+processHeaders[3], 17));
		System.out.print(padRight("| "+processHeaders[4], 19));
		System.out.print(padRight("| "+processHeaders[5], 19));
		System.out.print(padRight("| "+processHeaders[6], 17));
		System.out.println("|");
		System.out.print(" ");
		for(int i=0;i<114;i++) {
			System.out.print("-");
		}
		System.out.println(" ");
		for(int i=0;i<numberOfProcesses;i++) {
			System.out.print("| "+padRight("P" + Integer.toString(processInfo[i].processIndex), 11) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].priority), 11) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].arrivalTime), 15) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].cpuBurstTime), 15) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].completionTime), 17) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].turnAroundTime), 17) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].waitingTime), 15) + "| ");
			System.out.println();
		}
		System.out.print(" ");
		for(int i=0;i<114;i++) {
			System.out.print("-");
		}
	}

}

class RoundRobinAlgorithm extends PreemptiveSchedulingAlgorithms{
	StringBuilder processGanttChart;
	Vector<Integer> timeChart;

	RoundRobinAlgorithm() {
		processGanttChart = new StringBuilder();
		timeChart = new Vector<Integer>();
	}

	@Override
	public void getProcessSequence() {
		int time = 0;
		for(int i=0;i<numberOfProcesses;i++) {
			for(int j=0;j<numberOfProcesses-i-1;j++) {
				if(processInfo[j+1].arrivalTime < processInfo[j].arrivalTime) {
					Process temp = processInfo[j];
					processInfo[j] = processInfo[j+1];
					processInfo[j+1] = temp;
				}
			}
		}
		for(Process process : processInfo) {
			if(process.arrivalTime == time) {
				readyQueue.add(process);
			}
		}
		printReadyQueue(time);
		while(!readyQueue.isEmpty()) {
			int min = Integer.MAX_VALUE, index = numberOfProcesses;
			Process currentProcessHeader = readyQueue.remove();
			for(Process process: processInfo) {
				if(process.processIndex == currentProcessHeader.processIndex) {
					if(process.remainingTime <= quantumTime) {
						for(int i=0;i<process.remainingTime;i++) {
							time++;
							for(Process newProcess : processInfo) {
								if(newProcess.arrivalTime == time) {
									readyQueue.add(newProcess);
								}
							}
							printReadyQueue(time);
							processGanttChart.append(process.processIndex);
							timeChart.add(time-1);
						}
						process.remainingTime = 0;
						process.completionTime = time;
						process.turnAroundTime = process.completionTime - process.arrivalTime;
					} else {
						for(int i=0;i<quantumTime;i++) {
							time++;
							for(Process newProcess : processInfo) {
								if(newProcess.arrivalTime == time) {
									readyQueue.add(newProcess);
								}
							}
							printReadyQueue(time);
							processGanttChart.append(process.processIndex);
							timeChart.add(time-1);
						}
						process.remainingTime -= quantumTime;
						readyQueue.add(currentProcessHeader);
					}
				}
			}
		}
		for(Process process: processInfo) {
			process.waitingTime = process.turnAroundTime - process.cpuBurstTime;
		}
		System.out.println("Quantum Time is: " + quantumTime);
		int printIndex = 0;
		System.out.print("Process Gantt Chart: ");
		for(int i=0;i<processGanttChart.length();i++) {
			int printValue = Character.getNumericValue(processGanttChart.charAt(i));
			if(printValue != printIndex) {
				System.out.print("P"+printValue+"-");
				printIndex = printValue;
			} else {
				System.out.print("-");
			}
		}
		System.out.println("End");
		System.out.print("               Time: ");
		printIndex = 0;
		for(int i=0;i<processGanttChart.length();i++) {
			int printValue = Character.getNumericValue(processGanttChart.charAt(i));
			if(printValue != printIndex) {
				System.out.print(padRight(Integer.toString((int)timeChart.elementAt(i)), 2)+" ");
				printIndex = printValue;
			} else {
				System.out.print(" ");
			}
		}
		System.out.println(time);
	}

	@Override
	public void getProcessMetrics() {
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
			System.out.print("| "+padRight("P" + Integer.toString(processInfo[i].processIndex), 16) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].arrivalTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].cpuBurstTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].completionTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].turnAroundTime), 16) + "| ");
			System.out.print(padRight(Integer.toString(processInfo[i].waitingTime), 16) + "| ");
			System.out.println();
		}
		System.out.print(" ");
		for(int i=0;i<107;i++) {
			System.out.print("-");
		}
	}
}