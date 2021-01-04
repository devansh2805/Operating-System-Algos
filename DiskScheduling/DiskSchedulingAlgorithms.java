import java.util.Scanner;
import java.util.Arrays;
import java.util.LinkedList;

abstract class DiskSchedulingAlgorithms {
	static int numberOfRequests;
	static int[] request;
	static int startHeadPointer, upperLimit, totalMovement;
	static LinkedList<Integer> resourceServingList;

	public static void main(String[] args) {
		resourceServingList = new LinkedList<Integer>();
		Scanner scanner = new Scanner(System.in);
		System.out.print("Enter Number of Requests: ");
		numberOfRequests = scanner.nextInt();
		request = new int[numberOfRequests];
		System.out.print("Enter Requests: ");
		for(int i=0;i<numberOfRequests;i++) {
			request[i] = scanner.nextInt();
		}
		System.out.print("Enter Upper Limit: ");
		upperLimit = scanner.nextInt();
		System.out.print("Enter Head Pointer Position: ");
		startHeadPointer = scanner.nextInt();
		while(true) {
			printMenu();
			System.out.print("Enter Choice: ");
			int choice = scanner.nextInt();
			resourceServingList.clear();
			resourceServingList.add(startHeadPointer);
			System.out.println();
			switch(choice) {
				case 1:
					FirstComeFirstServeScheduling firstComeFirstScheduling = new FirstComeFirstServeScheduling();
					firstComeFirstScheduling.calculateMovement();
					break;
				case 2:
					ShortestSeekTimeFirstScheduling shortestSeekTimeFirstScheduling = new ShortestSeekTimeFirstScheduling();
					shortestSeekTimeFirstScheduling.calculateMovement();
					break;
				case 3:
					ScanScheduling scanScheduling = new ScanScheduling();
					scanScheduling.calculateMovement();
					break;
				case 4:
					CircularScanScheduling circularScanScheduling = new CircularScanScheduling();
					circularScanScheduling.calculateMovement();
					break;
				case 5:
					LookScheduling lookScheduling = new LookScheduling();
					lookScheduling.calculateMovement();
					break;
				case 6:
					CircularLookScheduling circularLookScheduling = new CircularLookScheduling();
					circularLookScheduling.calculateMovement();
					break;
				case 7:
					scanner.close();
					return;
				default:
					scanner.close();
					return;				
			}
			printOrder();
			System.out.println("\033[1;33m" + "Total Disk Head Movement: " + totalMovement + "\033[0m");
		}
	}

	public abstract void calculateMovement();

	public static void printMenu() {
		System.out.println();
		System.out.println("1. First Come First Serve Scheduling");
		System.out.println("2. Shortest Seek Time First Scheduling");
		System.out.println("3. Scan Scheduling");
		System.out.println("4. Circular Scan Scheduling");
		System.out.println("5. Look Scheduling");
		System.out.println("6. Circular Look Scheduling");
		System.out.println("7. End");
	}

	public static int positiveDifference(int x, int y) {
		int difference = y - x;
		return difference > 0 ? difference : -difference; 
	}

	public static int closestToHead(int[] array) {
		int min = Integer.MAX_VALUE, index = -1;
		for(int i=0;i<numberOfRequests;i++) {
			int difference = positiveDifference(array[i], startHeadPointer);  
			if(difference < min && array[i] <= startHeadPointer) {
				min = difference;
				index = i;
			}
		}
		return index;
	}

	public static void printOrder() {
		String sequence = "";
		for(Integer value: resourceServingList) {
			sequence += value.intValue() + " -> "; 
		}
		System.out.print("\033[1;37m" + "Order of Head Movement is: " + "\033[0m");
		System.out.println("\033[1;35m" + sequence.substring(0, sequence.length()-3) + "\033[0m");
	}
}

class FirstComeFirstServeScheduling extends DiskSchedulingAlgorithms {
	
	int currentHead;
	
	FirstComeFirstServeScheduling() {
		currentHead = startHeadPointer;
		totalMovement = 0;
	}

	@Override
	public void calculateMovement() {
		for(int value: request) {
			totalMovement += positiveDifference(value, currentHead);
			currentHead = value;
			resourceServingList.add(value); 
		}
	} 
}

class ShortestSeekTimeFirstScheduling extends DiskSchedulingAlgorithms {
	
	int currentHead;
	boolean[] finished;
	
	ShortestSeekTimeFirstScheduling() {
		currentHead = startHeadPointer;
		totalMovement = 0;
		finished = new boolean[numberOfRequests];
	}

	@Override
	public void calculateMovement() {
		int difference = 0;
		int index = 0;
		for(int i=0;i<numberOfRequests;i++) {
			int min = Integer.MAX_VALUE;
			for(int j=0;j<numberOfRequests;j++) {
				difference = positiveDifference(request[j], currentHead);
				if(difference<=min && !finished[j]) {
					min = difference;
					index = j;
				}
			}
			finished[index] = true;
			currentHead = request[index];
			totalMovement += min;
			resourceServingList.add(currentHead); 
		}
	} 
}

class ScanScheduling extends DiskSchedulingAlgorithms {

	int currentHead;
	int[] requestCopy;

	ScanScheduling() {
		currentHead = startHeadPointer;
		totalMovement = 0;
		requestCopy = Arrays.copyOfRange(request, 0, numberOfRequests);
	}

	@Override
	public void calculateMovement() {
		Arrays.sort(requestCopy);
		totalMovement = (2 * currentHead) + (requestCopy[numberOfRequests-1] - currentHead);
		this.calculateRequestOrder();
	}

	public void calculateRequestOrder() {
		int index = closestToHead(requestCopy);
		int otherIndex = index + 1;
		while(index>=0) {
			resourceServingList.add(requestCopy[index--]); 
		}
		resourceServingList.add(0);
		while(otherIndex<numberOfRequests) {
			resourceServingList.add(requestCopy[otherIndex++]); 
		}
	}
}

class CircularScanScheduling extends DiskSchedulingAlgorithms {
	
	int[] requestCopy;
	
	CircularScanScheduling() {
		totalMovement = 0;
		requestCopy = Arrays.copyOfRange(request, 0, numberOfRequests);
	}

	@Override
	public void calculateMovement() {
		Arrays.sort(requestCopy);
		int index = closestToHead(requestCopy);
		totalMovement = startHeadPointer + upperLimit + upperLimit - requestCopy[index+1];
		this.calculateRequestOrder();
	}

	public void calculateRequestOrder() {
		int index = closestToHead(requestCopy);
		int temp = index;
		int otherIndex = numberOfRequests - 1;
		while(index>=0) {
			resourceServingList.add(requestCopy[index--]); 
		}
		resourceServingList.add(0);
		resourceServingList.add(upperLimit);
		while(otherIndex>temp) {
			resourceServingList.add(requestCopy[otherIndex--]); 
		}
	}
}

class LookScheduling extends DiskSchedulingAlgorithms {

	int currentHead;
	int[] requestCopy;
	
	LookScheduling() {
		currentHead = startHeadPointer;
		totalMovement = 0;
		requestCopy = Arrays.copyOfRange(request, 0, numberOfRequests);
	}

	@Override
	public void calculateMovement() {
		Arrays.sort(requestCopy);
		totalMovement = (2 * (currentHead - requestCopy[0])) + (requestCopy[numberOfRequests-1] - currentHead);
		this.calculateRequestOrder();
	}

	public void calculateRequestOrder() {
		int index = closestToHead(requestCopy);
		int otherIndex = index + 1;
		while(index>=0) {
			resourceServingList.add(requestCopy[index--]); 
		}
		while(otherIndex<numberOfRequests) {
			resourceServingList.add(requestCopy[otherIndex++]); 
		}
	}
}

class CircularLookScheduling extends DiskSchedulingAlgorithms {

	int[] requestCopy;
	
	CircularLookScheduling() {
		totalMovement = 0;
		requestCopy = Arrays.copyOfRange(request, 0, numberOfRequests);
	}

	@Override
	public void calculateMovement() {
		Arrays.sort(requestCopy);
		int index = closestToHead(requestCopy);
		totalMovement = startHeadPointer - requestCopy[0]; 
		totalMovement += requestCopy[numberOfRequests-1] - requestCopy[0];
		totalMovement += requestCopy[numberOfRequests-1] - requestCopy[index+1];
		this.calculateRequestOrder();
	}

	public void calculateRequestOrder() {
		int index = closestToHead(requestCopy);
		int temp = index;
		int otherIndex = numberOfRequests - 1;
		while(index>=0) {
			resourceServingList.add(requestCopy[index--]); 
		}
		while(otherIndex>temp) {
			resourceServingList.add(requestCopy[otherIndex--]); 
		}
	}
} 