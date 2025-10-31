package com.example;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class CpuMonitor {
    private SystemInfo si;
    private CentralProcessor processor;

    public CpuMonitor() {
        si = new SystemInfo();
        processor = si.getHardware().getProcessor();
    }

    public void collectData() {
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        long[][] prevPerCoreTicks = processor.getProcessorCpuLoadTicks();

        try {
            Thread.sleep(1000); // Wait for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        double overallCpuUsage = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
        double[] perCoreCpuUsage = new double[processor.getPhysicalProcessorCount()];
        for (int i = 0; i < perCoreCpuUsage.length; i++) {
            perCoreCpuUsage[i] = processor.getProcessorCpuLoadBetweenTicks(prevPerCoreTicks[i]) * 100;
        }

        displayCpuUsage(overallCpuUsage, perCoreCpuUsage);
    }

    public void displayCpuUsage(double overallCpuUsage, double[] perCoreCpuUsage) {
        System.out.printf("Overall CPU Usage: %.2f%%\n", overallCpuUsage);
        for (int i = 0; i < perCoreCpuUsage.length; i++) {
            System.out.printf("Core %d CPU Usage: %.2f%%\n", i, perCoreCpuUsage[i]);
        }
    }
}