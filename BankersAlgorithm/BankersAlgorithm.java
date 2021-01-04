import java.util.Scanner;

class BankersAlgorithm {
	static int[] available;
	static int[][] max;
	static int[][] allocation;
	static int[][] need;
	static int numberOfProcesses, numberOfResourceTypes;
	static StringBuilder safeSequence;
	static Scanner scanner;

	public static void main(String[] args) {
		int i, j;
		scanner = new Scanner(System.in);
		System.out.print("Enter Number of Processes: ");
		numberOfProcesses = scanner.nextInt();
		System.out.print("Enter Number of Resource Types: ");
		numberOfResourceTypes = scanner.nextInt();
		available = new int[numberOfResourceTypes];
		max = new int[numberOfProcesses][numberOfResourceTypes];
		allocation = new int[numberOfProcesses][numberOfResourceTypes];
		need = new int[numberOfProcesses][numberOfResourceTypes];
		System.out.print("Enter Available Vector: ");
		for(i=0;i<numberOfResourceTypes;i++) {
			available[i] = scanner.nextInt();
		}
		System.out.print("Enter Maximum Resource Matrix: ");
		for(i=0;i<numberOfProcesses;i++) {
			for(j=0;j<numberOfResourceTypes;j++) {
				max[i][j] = scanner.nextInt();
			}
		}
		System.out.print("Enter Allocation Matrix: ");
		for(i=0;i<numberOfProcesses;i++) {
			for(j=0;j<numberOfResourceTypes;j++) {
				allocation[i][j] = scanner.nextInt();
			}
		}
		calculateNeed();
		if(isSafe()) {
			System.out.print("System is safe state, safe sequence is: ");
			printSafeSequence();	
		} else {
			System.err.println("\033[0;31m" + "System is not in safe state" + "\033[0m");
			return;
		}
		System.out.println();
		// while(true) {
		// 	System.out.println("\n");
		// 	System.out.println("1. Make Request");
		// 	System.out.println("2. End Program");
		// 	System.out.print("Enter Choice: ");
		// 	int choice = scanner.nextInt();
		// 	switch(choice) {
		// 		case 1:
		// 			makeRequest();
		// 			break;
		// 		case 2:
		// 			scanner.close();
		// 			return;
		// 	}
		// }
	}

	public static void printArray(String message, int[] arrayToPrint, String colorCode) {
		System.out.print(message + ": [ ");
		for(int value: arrayToPrint) {
			System.out.print(colorCode + value + " " + "\033[0m");
		}
		System.out.println("]");
	}

	public static void printArray(String message, boolean[] arrayToPrint, String colorCode, String alternateColorCode) {
		System.out.print(message + ": [ ");
		for(boolean value: arrayToPrint) {
			if(!value) {
				System.out.print(colorCode + value + " " + "\033[0m");
			} else {
				System.out.print(alternateColorCode + value + " " + "\033[0m");
			}
		}
		System.out.println("]");
	}

	public static void calculateNeed() {
		for(int i=0;i<numberOfProcesses;i++) {
			for(int j=0;j<numberOfResourceTypes;j++) {
				need[i][j] = max[i][j] - allocation[i][j]; 
			}
		}
	}

	public static boolean isSafe() {
		System.out.println("\nSafety Algorithm Running......\n");
		safeSequence = null;
		safeSequence = new StringBuilder();
		boolean flag = true;
		boolean[] finish = new boolean[numberOfProcesses];
		int[] work = new int[numberOfResourceTypes];
		for(int i=0;i<numberOfResourceTypes;i++) {
			work[i] = available[i];
		}
		printArray("Initial Work Vector.......", work, "\033[0;37m");
		printArray("Initial Finish Vector.....", finish, "\033[0;35m", "\033[0;36m");
		System.out.println();
		int count = 0;
		while(flag) {
			count++;
			for(int i=0;i<numberOfProcesses;i++) {
				if(!finish[i]) {
					if(compare(need[i], work)) {
						if(count!=numberOfProcesses+1) {
							System.out.println("Iteration " + count);
						}
						System.out.println("Process " + i + " satisfies safety conditions");
						for(int j=0;j<numberOfResourceTypes;j++) {
							work[j] += allocation[i][j];
						}
						finish[i] = true;
						printArray("Work Vector......", work, "\033[0;37m");
						printArray("Finish Vector....", finish, "\033[0;35m", "\033[0;36m");
						System.out.println();
						flag = true;
						safeSequence.append(i);
						break;
					} else {
						flag = false;
					}
				} else {
					flag = false;
				}
			}
		}
		for(int i=0;i<numberOfProcesses;i++) {
			if(finish[i]) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	public static boolean compare(int[] array1, int[] array2) {
		for(int i=0;i<array1.length;i++) {
			if(array1[i]<=array2[i]) {
				continue;
			} else {
				return false;
			}
		}
		return true;
	}

	public static void makeRequest() {
		int[] request = new int[numberOfResourceTypes];
		System.out.print("Enter Process number of requesting process: ");
		int index = scanner.nextInt();
		System.out.print("Enter Request Vector: ");
		for(int i=0;i<numberOfResourceTypes;i++) {
			request[i] = scanner.nextInt();
		}
		try {
			if(isRequestSafe(index, request)) {
				System.out.print("System is safe state, safe sequence is: ");
				printSafeSequence();
			} else {
				revertRequest(index, request);
				System.err.println("\033[0;31m" + "System is not in safe state" + "\033[0m");
				System.err.println("Reverting Request.....");
			}
		} catch(RequestException requestException) {
			System.err.println("\033[0;31m" + requestException + "\033[0m");
		}
	}

	public static boolean isRequestSafe(int index, int[] request) throws RequestException {
		if(compare(request, need[index])) {
			if(compare(request, available)) {
				updateRecords(index, request);
				System.out.print("\nRequest Allocated......");
				return isSafe();
			} else {
				throw new RequestException("Request Cannot be fullfilled at Present. Less Available Resources");
			}
		} else {
			throw new RequestException("Request More than Need");
		}
	}

	public static void updateRecords(int index, int[] request) {
		for(int i=0;i<numberOfResourceTypes;i++) {
			available[i] -= request[i];
			allocation[index][i] += request[i];
			need[index][i] -= request[i];
		}
	}

	public static void revertRequest(int index, int[] request) {
		for(int i=0;i<numberOfResourceTypes;i++) {
			available[i] += request[i];
			allocation[index][i] -= request[i];
			need[index][i] += request[i];
		}
	}

	public static void printSafeSequence() {
		int count = 1;
		for(char element: safeSequence.toString().toCharArray()) {
			if(count != numberOfProcesses) {
				System.out.print("\033[0;33m" + "P" + element + " -> " + "\033[0m");
				count++;
			} else {
				System.out.print("\033[0;33m" + "P" + element + "\033[0m");
			}
		}
	}
}

class RequestException extends Exception {
	String errorMessage;

	RequestException(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return errorMessage;
	}
}