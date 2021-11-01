package util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import dataClass.ProcessData;

public class PreSJF {
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
        String ganttChartTime = "\n";
        String ganttChartExplain = "\n";
        List<ProcessData> processDatas = GeneralFunction.collectInput(inputProcessID, inputArrivalTime, inputBurstTime, inputExtra);
        Boolean done = false;
        Boolean explainControl = false;
        Integer time = 0;
        String lastShortestJob = "";
        ArrayList<ProcessData> lastProcessAvailables = new ArrayList<ProcessData>();

        while(!done){
            ArrayList<ProcessData> processAvailables = new ArrayList<ProcessData>();
            explainControl = false;

            ProcessData shortestJob = new ProcessData();
            shortestJob.setBurstTime(0);
            for(ProcessData processData : processDatas){
                if(processData.getArrivalTime() <= time){
                    processAvailables.add(processData);
                }
            }

            if(processAvailables.isEmpty()){
                time = time + 1;
                lastProcessAvailables.clear();
            }
            else{
                ArrayList<ProcessData> checkduplicate = new ArrayList<ProcessData>();
                checkduplicate.addAll(processAvailables);
                checkduplicate.removeAll(lastProcessAvailables);

                if(!checkduplicate.isEmpty()){
                    ganttChartExplain = ganttChartExplain + "Process at time " + time.toString() + " : ";
                    for (int i = 0; i < processAvailables.size(); i++){
                        ganttChartExplain = ganttChartExplain + processAvailables.get(i).getProcessID() + "(" + processAvailables.get(i).getBurstTime() + ") ";
                    }
                    ganttChartExplain = ganttChartExplain + "\n";
                    explainControl = true;
                }

                for(ProcessData processAvailable : processAvailables){
                    if(shortestJob.getBurstTime() == 0){
                        shortestJob = processAvailable;
                    }
                    else{
                        if(shortestJob.getBurstTime()>processAvailable.getBurstTime()){
                            shortestJob = processAvailable;
                        }
                    }
                }
            
                if(!shortestJob.getProcessID().equals(lastShortestJob)){
                    ganttCharthead = ganttCharthead + "------|";

                    ganttChartContent = ganttChartContent + shortestJob.getProcessID();
                    for(int i = 0; i < 6 - shortestJob.getProcessID().length(); i++){
                        ganttChartContent = ganttChartContent + " ";
                    }
                    ganttChartContent = ganttChartContent + "|";

                    ganttChartBottom = ganttChartBottom + "------|";

                    ganttChartTime = ganttChartTime + time;
                    for(int i = 0; i < 7 - time.toString().length(); i++){
                        ganttChartTime = ganttChartTime + " ";
                    }

                    if(!explainControl){
                        ganttChartExplain = ganttChartExplain + "Process at time " + time.toString() + " : ";
                        for (int i = 0; i < processAvailables.size(); i++){
                            ganttChartExplain = ganttChartExplain + processAvailables.get(i).getProcessID() + "(" + processAvailables.get(i).getBurstTime() + ") ";
                        }
                        ganttChartExplain = ganttChartExplain + "\n";
                        explainControl = true;
                    }
                }
                lastShortestJob =  shortestJob.getProcessID();
                time = time + 1;
                lastProcessAvailables.clear();
                lastProcessAvailables.addAll(processAvailables);

                for(ProcessData processData : processDatas){
                    if(processData.getProcessID().equals(shortestJob.getProcessID())){
                        processData.setBurstTime(processData.getBurstTime()-1);
                    }
                }
    
                ArrayList<ProcessData> removeData = new ArrayList<ProcessData>();
                for(ProcessData processData : processDatas){
                    if(processData.getBurstTime() == 0){
                        removeData.add(processData);
                    }
                }
                processDatas.removeAll(removeData);

            } 

            if(processDatas.isEmpty()){
                done = true;
            }
        }
        ganttChartTime = ganttChartTime + time;

        String ganttChart = ganttCharthead + ganttChartContent + ganttChartBottom + ganttChartTime + ganttChartExplain;

        return ganttChart;
    }

    public static List<ProcessData> getFinishingTime(String inputProcessID, String inputArrivalTime, String inputBurstTime, String inputExtra){
        List<ProcessData> newProcessDatas = GeneralFunction.collectInput(inputProcessID, inputArrivalTime, inputBurstTime, inputExtra);
        
        List<ProcessData> processDatas = GeneralFunction.collectInput(inputProcessID, inputArrivalTime, inputBurstTime, inputExtra);
        Boolean done = false;
        Integer time = 0;

        while(!done){
            ArrayList<ProcessData> processAvailables = new ArrayList<ProcessData>();

            ProcessData shortestJob = new ProcessData();
            shortestJob.setBurstTime(0);
            for(ProcessData processData : processDatas){
                if(processData.getArrivalTime() <= time){
                    processAvailables.add(processData);
                }
            }

            for(ProcessData processAvailable : processAvailables){
                if(shortestJob.getBurstTime() == 0){
                    shortestJob = processAvailable;
                }
                else{
                    if(shortestJob.getBurstTime()>processAvailable.getArrivalTime()){
                        shortestJob = processAvailable;
                    }
                }
            }
            
            time = time + 1;

            for(ProcessData processData : processDatas){
                if(processData.getProcessID().equals(shortestJob.getProcessID())){
                    processData.setBurstTime(processData.getBurstTime()-1);
                }
            }

            ArrayList<ProcessData> removeData = new ArrayList<ProcessData>();
            for(ProcessData processData : processDatas){
                if(processData.getBurstTime() == 0){
                    removeData.add(processData);
                    for (ProcessData newProcessData : newProcessDatas){
                        if(newProcessData.getProcessID().equals(processData.getProcessID())){
                            newProcessData.setFinishingTime(time);
                        }
                    }
                }
            }
            processDatas.removeAll(removeData);

            if(processDatas.isEmpty()){
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
        System.out.println("Preemptive Shortest Job First");
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
