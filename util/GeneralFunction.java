package util;

import java.util.ArrayList;
import java.util.List;

import dataClass.ProcessData;

public class GeneralFunction {
    public static List<ProcessData> collectInput(String inputProcessID, String inputArrivalTime, String inputBurstTime, String inputPriority){
        List<ProcessData> processDatas = new ArrayList<ProcessData>();
        String[] processIDs = inputProcessID.split(" ");
        String[] arrivalTimes = inputArrivalTime.split(" ");
        String[] burstTimes = inputBurstTime.split(" ");
        
        for(int i = 0; i < processIDs.length; i++){
            ProcessData newData = new ProcessData();
            newData.setProcessID(processIDs[i]);
            newData.setArrivalTime(Integer.parseInt(arrivalTimes[i]));
            newData.setBurstTime(Integer.parseInt(burstTimes[i]));
            processDatas.add(newData);
        }

        if(!inputPriority.equals("")){
            String[] priorities = inputPriority.split(" ");
            for(int i = 0; i < priorities.length; i++){
                processDatas.get(i).setPriority(Integer.parseInt(priorities[i]));
            }
        }   
        return processDatas;
    }

    public static List<ProcessData> calculateProcessData(List<ProcessData> processDatas){
        for (ProcessData processData : processDatas){
            processData.setTurnaroundTime(processData.getFinishingTime()-processData.getArrivalTime());
            processData.setWaitingTime(processData.getTurnaroundTime()-processData.getBurstTime());
        }
        return processDatas;
    }
}
