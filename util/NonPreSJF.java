package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dataClass.ProcessData;

public class NonPreSJF {
    public static void display(Scanner scanner){
        scanner.nextLine();
        System.out.print("Process ID (e.g. P0 P1 P2 P3 P4 P5) : ");
        String processID = scanner.nextLine();
        System.out.print("    Arrival Time (e.g. 0 1 2 3 4 5) : ");
        String arrivalTime = scanner.nextLine();
        System.out.print("      Burst Time (e.g. 0 1 2 3 4 5) : ");
        String burstTime = scanner.nextLine();
        String inputExtra = "";

        tableGenerate(processID, arrivalTime, burstTime, inputExtra);

        String ganttChart = ganttChartGenerate(processID, arrivalTime, burstTime, inputExtra);

        System.out.print(ganttChart);    
    }
    public static String ganttChartGenerate(String inputProcessID, String inputArrivalTime, String inputBurstTime, String inputExtra){
        String ganttCharthead = "|";
        String ganttChartContent = "\n|";
        String ganttChartBottom = "\n|";
        String ganttChartTime = "\n0";
        String ganttChartExplain = "\n";
        List<ProcessData> processDatas = GeneralFunction.collectInput(inputProcessID, inputArrivalTime, inputBurstTime, inputExtra);
        Integer numProcess = processDatas.size();
        Integer time = 0;
        Boolean done = false;

        while(!done){
            ArrayList<ProcessData> process = new ArrayList<ProcessData>();
            ProcessData shortestJOB = new ProcessData();
            shortestJOB.setBurstTime(1000000000);
            for(ProcessData processData : processDatas){
                if(processData.getArrivalTime() <= time){
                    process.add(processData);
                    if(shortestJOB.getBurstTime() > processData.getBurstTime()){
                        shortestJOB = processData;
                    }
                }
            }

            if(shortestJOB.getBurstTime() == 1000000000){
                time = time + 1;
            }
            else{
                processDatas.remove(shortestJOB);

                ganttCharthead = ganttCharthead + "------|";

                ganttChartContent = ganttChartContent + shortestJOB.getProcessID();
                for(int i = 0; i < 6 - shortestJOB.getProcessID().length(); i++){
                    ganttChartContent = ganttChartContent + " ";
                }
                ganttChartContent = ganttChartContent + "|";

                ganttChartBottom = ganttChartBottom + "------|";

                ganttChartExplain = ganttChartExplain + "Process at time " + time.toString() + " : ";
                for (int i = 0; i < process.size(); i++){
                    ganttChartExplain = ganttChartExplain + process.get(i).getProcessID() + "(" + process.get(i).getBurstTime() + ") ";
                }
                ganttChartExplain = ganttChartExplain + "\n";

                time = time + shortestJOB.getBurstTime();
                for(int i = 0; i < 7 - time.toString().length(); i++){
                    ganttChartTime = ganttChartTime + " ";
                }
                ganttChartTime = ganttChartTime + time;

                numProcess = numProcess - 1;
            }
            
            if (numProcess == 0){
                done = true;
            }
        }

        String ganttChart = ganttCharthead + ganttChartContent + ganttChartBottom + ganttChartTime + ganttChartExplain;

        return ganttChart;
    }

    public static List<ProcessData> getFinishingTime(String inputProcessID, String inputArrivalTime, String inputBurstTime, String inputExtra){
        List<ProcessData> newProcessDatas = GeneralFunction.collectInput(inputProcessID, inputArrivalTime, inputBurstTime, inputExtra);
        
        List<ProcessData> processDatas = GeneralFunction.collectInput(inputProcessID, inputArrivalTime, inputBurstTime, inputExtra);
        Integer numProcess = processDatas.size();
        Integer time = 0;
        Boolean done = false;
        while(!done){
            ArrayList<String> process = new ArrayList<String>();
            ProcessData shortestJOB = new ProcessData();
            shortestJOB.setBurstTime(1000000000);
            for(ProcessData processData : processDatas){
                if(processData.getArrivalTime() <= time){
                    process.add(processData.getProcessID());       
                }
            }
            
            
            if(process.isEmpty()){
                time = time + 1;
            }
            else{
                for(ProcessData processData : processDatas){
                    if(processData.getArrivalTime() <= time){
                        if(shortestJOB.getBurstTime() > processData.getBurstTime()){
                            shortestJOB = processData;
                        }       
                    }
                }
                processDatas.remove(shortestJOB);
                time = time + shortestJOB.getBurstTime();
                for(ProcessData newprocessData : newProcessDatas){
                    if(newprocessData.getProcessID().equals(shortestJOB.getProcessID())){
                        newprocessData.setFinishingTime(time); 
                    }
                }
                numProcess = numProcess - 1;
            }
            
            if (numProcess == 0){
                done = true;
            }
        }
        return newProcessDatas;
    }

    public static List<ProcessData> completeData(String inputProcessID, String inputArrivalTime, String inputBurstTime, String inputExtra){
        List<ProcessData> processDatas_finishTime = getFinishingTime(inputProcessID, inputArrivalTime, inputBurstTime, inputExtra);
        List<ProcessData> processDatas_complete = GeneralFunction.calculateProcessData(processDatas_finishTime);

        return processDatas_complete;      
    }

    public static void tableGenerate(String inputProcessID, String inputArrivalTime, String inputBurstTime, String inputExtra){
        List<ProcessData> processDatas = completeData(inputProcessID, inputArrivalTime, inputBurstTime, inputExtra);

        System.out.println("");
        System.out.println("Non Preemptive Shortest Job First");
        System.out.println("|------------|--------------|------------|----------------|-----------------|--------------|");
        System.out.println("| Process ID | Arrival Time | Burst Time | Finishing Time | Turnaround Time | Waiting Time |");
        System.out.println("|------------|--------------|------------|----------------|-----------------|--------------|");
        for (ProcessData processData : processDatas){
            System.out.print("| ");
            System.out.print(processData.getProcessID());
            for(int i = 0; i < 11-processData.getProcessID().length(); i++){
                System.out.print(" ");
            }

            System.out.print("| ");
            System.out.print(processData.getArrivalTime());
            for(int i = 0; i < 13-processData.getArrivalTime().toString().length(); i++){
                System.out.print(" ");
            }

            System.out.print("| ");
            System.out.print(processData.getBurstTime());
            for(int i = 0; i < 11-processData.getBurstTime().toString().length(); i++){
                System.out.print(" ");
            }

            System.out.print("| ");
            System.out.print(processData.getFinishingTime());
            for(int i = 0; i < 15-processData.getFinishingTime().toString().length(); i++){
                System.out.print(" ");
            }

            System.out.print("| ");
            System.out.print(processData.getTurnaroundTime());
            for(int i = 0; i < 16-processData.getTurnaroundTime().toString().length(); i++){
                System.out.print(" ");
            }

            System.out.print("| ");
            System.out.print(processData.getWaitingTime());
            for(int i = 0; i < 13-processData.getWaitingTime().toString().length(); i++){
                System.out.print(" ");
            }
            System.out.print("|\n");
        }
        System.out.println("|------------|--------------|------------|----------------|-----------------|--------------|");
        System.out.print("|                                                 Total : | ");
        Double total_turnarount = 0.00 ;
        Double total_waiting = 0.00 ;
        for (ProcessData processData : processDatas){
            total_turnarount = total_turnarount + processData.getTurnaroundTime();
            total_waiting = total_waiting + processData.getWaitingTime();
        }
        System.out.print(total_turnarount);
        for(int i = 0; i < 16-total_turnarount.toString().length(); i++){
            System.out.print(" ");
        }
        System.out.print("| ");
        System.out.print(total_waiting);
        for(int i = 0; i < 13 - total_waiting.toString().length(); i++){
            System.out.print(" ");
        }
        System.out.print("|\n");
        System.out.print("|                                               Average : | ");

        Double average_turnarount = total_turnarount / processDatas.size();
        Double average_waiting = total_waiting / processDatas.size();
        String average_turnarount_string = String.format("%.2f", average_turnarount);
        String average_waiting_string = String.format("%.2f", average_waiting);

        System.out.print(average_turnarount_string);
        for(int i = 0; i < 16-average_turnarount_string.length(); i++){
            System.out.print(" ");
        }

        System.out.print("| ");
        System.out.print(average_waiting_string);
        for(int i = 0; i < 13 - average_waiting_string.length(); i++){
            System.out.print(" ");
        }
        System.out.print("|\n");
        System.out.println("|------------------------------------------------------------------------------------------|\n");
    }
}
