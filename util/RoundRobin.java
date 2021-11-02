package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dataClass.ProcessData;

public class RoundRobin {
    private static int QUANTUM = 3;
    private static List<ProcessData> processes;
    /*
     * note
     * 
     * 3 to 10 processes
     * 
     * move all input to main
     */

    public static void runTerminal() {
        Scanner input = new Scanner(System.in);
        int noOfProcesses = 0;
        String processIDsInput = "";

        // TODO: remove :'(
        while (noOfProcesses < 3 || noOfProcesses > 10) {
            System.out.print("\nProcess IDs (enter 3 to 10 processes, e.g. P0 P1 P2 P3 P4 P5) : ");
            processIDsInput = input.nextLine();
            String[] processIDs = processIDsInput.split(" ");
            noOfProcesses = processIDs.length;
            if (noOfProcesses < 3 || noOfProcesses > 10) {
                System.out.println("\nPlease enter between 3 to 10 processes.\n");
            }
        }
        System.out.print("    Arrival Times (e.g. 0 1 2 3 4 5) : ");
        String arrivalTimeInput = input.nextLine();
        System.out.print("      Burst Times (e.g. 0 1 2 3 4 5) : ");
        String burstTimesInput = input.nextLine();
        System.out.print("      Priorities (e.g. 0 1 2 3 4 5) : ");
        String prioritiesInput = input.nextLine();

        processes = GeneralFunction.collectInput(processIDsInput, arrivalTimeInput, burstTimesInput, prioritiesInput);

        // set default value
        // TODO: remove?
        for (ProcessData processData : processes) {
            processData.setFinishingTime(0);
            processData.setTurnaroundTime(0);
            processData.setWaitingTime(0);
        }
        List<ProcessData> schedulerResult = scheduleProcess(processes);
        setProcessTableData(schedulerResult);
        showGanttChart(schedulerResult);
        showProcessTable(processes);

        input.close();
    }

    public static List<ProcessData> scheduleProcess(List<ProcessData> processes) {
        // store original burst times
        List<Integer> remainingBurstTimes = new ArrayList<Integer>();
        for (ProcessData process : processes) {
            remainingBurstTimes.add(process.getBurstTime());
        }
        // store result
        List<ProcessData> schedulerResult = new ArrayList<ProcessData>();
        // next process by index
        List<Integer> processQueue = new ArrayList<Integer>();
        int queueCounter = 0;
        // process in progress
        ProcessData pData = new ProcessData();
        // is there anymore request schedule
        boolean isScheduling = true;
        // is CPU requesting for next process
        boolean isRequesting = true;
        int time = 0;
        int remainingBurstTimer = 0;
        // start of scheduling
        while (isScheduling) {
            // Add processes to queue whenever new processes arrive
            processQueue.addAll(getArrivedProcessByTime(processes, time));

            // Adds running process with remaining burst time back to queue only after the
            // process is finished, and after new arrivals
            if (remainingBurstTimer > 0 && remainingBurstTimer == time) {
                if (pData.getBurstTime() > 0) {
                    processQueue.add(processQueue.get(queueCounter - 1));
                }
            }
            isRequesting = remainingBurstTimer <= time && processQueue.size() > queueCounter;

            /**
             * PROCESS SCHEDULING
             * 
             * process scheduling new entry into the queue and decreasing waiting processes
             * burst time
             * 
             * triggers when CPU is requesting for next process
             */
            if (isRequesting) {
                pData = processes.get(processQueue.get(queueCounter));
                ProcessData newProcess = new ProcessData();
                String pID = pData.getProcessID();
                int pBurst = pData.getBurstTime();
                if (pBurst >= QUANTUM) {
                    pData.setBurstTime(pBurst - QUANTUM);
                    newProcess.setBurstTime(QUANTUM);
                    remainingBurstTimer = QUANTUM + time;
                    newProcess.setFinishingTime(remainingBurstTimer);
                } else {
                    pData.setBurstTime(0);
                    newProcess.setBurstTime(pBurst);
                    remainingBurstTimer = pBurst + time;
                    newProcess.setFinishingTime(remainingBurstTimer);
                }
                newProcess.setProcessID(pID);
                newProcess.setArrivalTime(time);
                schedulerResult.add(newProcess);
                // next one in queue
                queueCounter++;
            }

            // check if there are process still waiting
            isScheduling = false;
            for (ProcessData wp : processes) {
                if (wp.getBurstTime() != 0)
                    isScheduling = true;
            }
            time++;
        }
        // reapply original burst time after process is finished
        for (int i = 0; i < processes.size(); i++) {
            processes.get(i).setBurstTime(remainingBurstTimes.get(i));
        }

        return schedulerResult;

    }

    /**
     * PROCESS ARRIVAL
     */
    public static List<Integer> getArrivedProcessByTime(List<ProcessData> processes, int time) {
        // containes the indexes of process from processes
        List<Integer> newProcesses = new ArrayList<Integer>();
        List<Integer> newProcessesToQueue = new ArrayList<Integer>();

        // filter by processes arriving in this time
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).getArrivalTime() == time)
                newProcesses.add(i);
        }
        // set order by priority
        // break loop if no new process
        int sizeOfNewProcesses = newProcesses.size();
        for (int j = 0; j < sizeOfNewProcesses; j++) {

            int newProcessIndex = 0;
            int newProcessPrio = 0;
            // get the lowest prio process of remaining new processes
            for (int i = 0; i < newProcesses.size(); i++) {
                int currentPrio = processes.get(newProcesses.get(i)).getPriority();
                // to compare priority of arrival, will atleast enter once
                if (newProcessPrio == 0 || currentPrio < newProcessPrio) {
                    newProcessPrio = currentPrio;
                    newProcessIndex = i;
                }
            }
            newProcessesToQueue.add(newProcesses.get(newProcessIndex));
            newProcesses.remove(newProcessIndex);
        }
        // TODO: remove commented reference (old version)
        // for (int i = 0; i < processes.size(); i++) {
        // if (processes.get(i).getArrivalTime() == time) {
        // int currentPrio = processes.get(i).getPriority();
        // // to compare priority of arrival, will atleast enter once
        // if (newProcessPrio == 0 || currentPrio < newProcessPrio) {
        // newProcessPrio = currentPrio;
        // newProcessIndex = i;
        // }
        // hasNewProcessArrived = true;
        // }
        // }
        // if (hasNewProcessArrived) {
        // newProcessToQueue.add(1);
        // }
        return newProcessesToQueue;
    }

    private static void setProcessTableData(List<ProcessData> result) {
        for (int i = 0; i < processes.size(); i++) {
            int arrivalTime = processes.get(i).getArrivalTime();
            int burstTime = 0;
            int finishingTime = 0;
            for (ProcessData processData : result) {
                if (processData.getProcessID() == processes.get(i).getProcessID()) {
                    finishingTime = processData.getFinishingTime();
                    burstTime += processData.getBurstTime();
                }
            }
            int turnaroundTime = finishingTime - arrivalTime;
            int waitingTime = turnaroundTime - burstTime;
            processes.get(i).setFinishingTime(finishingTime);
            processes.get(i).setTurnaroundTime(turnaroundTime);
            processes.get(i).setWaitingTime(waitingTime);
        }
    }

    private static void showGanttChart(List<ProcessData> processes) {
        int totalTime = 0;
        for (int i = 0; i < processes.size(); i++) {
            System.out.print("-------");
        }
        System.out.print("-\n");
        for (ProcessData processData : processes) {
            System.out.printf("| %-5s", processData.getProcessID());
        }
        System.out.print("|\n");
        for (int i = 0; i < processes.size(); i++) {
            System.out.print("|------");
        }
        System.out.print("|\n");
        for (ProcessData processData : processes) {
            // get process end time
            totalTime = processData.getArrivalTime() + processData.getBurstTime();
            System.out.printf("%-7d", processData.getArrivalTime());
        }
        System.out.print(totalTime + "\n");
        // TODO: add process at time

    }

    public static void showProcessTable(List<ProcessData> processes) {

        System.out.println("");
        System.out.println("Round Robin, quantum = " + QUANTUM);
        System.out.println(
                "-------------------------------------------------------------------------------------------------------");
        System.out.println(
                "| Process ID | Arrival Time | Burst Time | Priority | Finishing Time | Turnaround Time | Waiting Time |");
        for (ProcessData process : processes) {
            System.out.println(
                    "|------------|--------------|------------|----------|----------------|-----------------|--------------|");
            System.out.printf("| %-11s|%13s |%11s |%9s |%15s |%16s |%13s |\n", process.getProcessID(),
                    process.getArrivalTime(), process.getBurstTime(), process.getPriority(), process.getFinishingTime(),
                    process.getTurnaroundTime(), process.getWaitingTime());
        }
        System.out.println(
                "-------------------------------------------------------------------------------------------------------");
    }
}
