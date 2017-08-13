package com.tcs.crawler.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FilenameUtils;

public class CrawlerReport {
	
	private static File logger = new File("crawlerReport.log");
	private static StringBuilder stringBuilder = new StringBuilder();
	private static String encryptedExtension = "locked";//"xtbl";//"btcbtcbtc";
	private static String operation;
	private static String driveToCrawl;
	private static boolean isExtension = false;
	private static String extensionOrFileName;

	public static void main(String[] args) throws Exception{
		operation = args[0];
		encryptedExtension = args[1];
		extensionOrFileName = args[2];
		if(args.length>3)
			driveToCrawl = args[1];
		if(operation == null || !operation.trim().equalsIgnoreCase("report")){
			System.out.println("\n===== Invalid command line arguments =====\n\tjava -jar crawlerreport.jar <report> <extension|filename> <extension name|file name> {\"specific drive\"(optional)} =====\n");
			stringBuilder.append("\n===== Invalid command line arguments =====\n\tjava -jar crawlerreport.jar <report> <extension|filename> <extension name|file name> {\"specific drive\"(optional)} =====\n");
			throw new Exception("\n===== Invalid command line arguments =====\n\tjava -jar crawlerreport.jar <report> <extension|filename> <extension name|file name> {\"specific drive\"(optional)} =====\n");
		}
		if(encryptedExtension == null || !encryptedExtension.trim().equalsIgnoreCase("extension") || !encryptedExtension.trim().equalsIgnoreCase("filename")){
			System.out.println("\n===== Invalid command line arguments =====\n\tjava -jar crawlerreport.jar <report> <extension|filename> <extension name|file name> {\"specific drive\"(optional)} =====\n");
			stringBuilder.append("\n===== Invalid command line arguments =====\n\tjava -jar crawlerreport.jar <report> <extension|filename> <extension name|file name> {\"specific drive\"(optional)} =====\n");
			throw new Exception("\n===== Invalid command line arguments =====\n\tjava -jar crawlerreport.jar <report> <extension|filename> <extension name|file name> {\"specific drive\"(optional)} =====\n");
		}
		if(encryptedExtension.trim().equalsIgnoreCase("extension"))
			isExtension = true;
		long startTime = System.currentTimeMillis();
		try{
			CrawlerReport crawler = new CrawlerReport();
			List<Object> drives = null;
			if(null != driveToCrawl && driveToCrawl.trim().length()>0){
				drives = new ArrayList<Object>();
				drives.add(driveToCrawl);
			}else{
				drives = crawler.findAllDrives();
			}
			List<String> resultList = new ArrayList<String>();
			for(Object drive : drives){
				System.out.println(" \n===== Fetching directories under "+drive + " =====\n");
				stringBuilder.append(" \n===== Fetching directories under "+drive + " =====\n");
				//if(isExtension)
					crawler.navigateDirectories(drive.toString(),resultList);
				//else
					//crawler
			}
			System.out.println(" \n===== Total Directories found to be decrypted is "+resultList.size() + " =====\n");
			stringBuilder.append(" \n===== Total Directories found to be decrypted is "+resultList.size() + " =====\n");
//			System.out.println(" \n===== Directories found to be decrypted are =====\n");
			stringBuilder.append(" \n===== Directories found to be decrypted are =====\n");
			for(String result : resultList){
//				System.out.println("\n"+result);
				stringBuilder.append(result+"\n");
			}

//			System.out.println("\n==============================================================================\n");
			stringBuilder.append("\n==============================================================================\n");
		}finally{
			stringBuilder.append("\nTotal time taken [ "+ (System.currentTimeMillis() - startTime)+" ]");
			FileWriter fw = new FileWriter(logger.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(stringBuilder.toString());
			bw.close();
			System.out.println("\nTotal time taken [ "+ (System.currentTimeMillis() - startTime)+" ms ]");
		}
	}
	
	private List<Object> findAllDrives(){
		File[] drives = File.listRoots();
		FileSystemView fsv = FileSystemView.getFileSystemView();
		List<Object> allDrives = new ArrayList<Object>();
		if (drives != null && drives.length > 0) {
		    for (File aDrive : drives) {
		        System.out.println(aDrive);
		        System.out.println("Drive Letter: " + aDrive);
		        stringBuilder.append("\nDrive Letter: " + aDrive);
                System.out.println("\tType: " + fsv.getSystemTypeDescription(aDrive));
                stringBuilder.append("\n\tType: " + fsv.getSystemTypeDescription(aDrive));
                System.out.println();
		        allDrives.add(aDrive);
		    }
		}
		return allDrives;
	}
	
	private  void navigateDirectories(String directoryName,List<String> resultList) {
		File directory = new File(directoryName);
		File[] fList = directory.listFiles();
		if(!isDecryptionComplete(fList) && isExtension){
			resultList.add(directory.getAbsolutePath());
			stringBuilder.append("\n"+directory);
		}else if(isFilePresent(fList)){
			resultList.add(directory.getAbsolutePath());
			stringBuilder.append("\n"+directory);
		}
		if(fList != null){
			for (File file : fList) {
				if (file.isDirectory()) {
					navigateDirectories(file.getAbsolutePath(),resultList);
				}
			}
		}
	} 
	
	private boolean isFilePresent(File[] files){
		if(files != null){
			List<File> fList = Arrays.asList(files);
			for (File file : fList) {
				String absoluteFileName = file.getAbsolutePath();
				if(file.isFile() && FilenameUtils.getName(absoluteFileName).equalsIgnoreCase(extensionOrFileName))
					return true;
			}
		}
		return false;
	}
	
	private boolean isDecryptionComplete(File[] files){
		if(files != null){
			List<File> fList = Arrays.asList(files);
			for (File file : fList) {
				String absoluteFileName = file.getAbsolutePath();
				if(file.isFile() && FilenameUtils.getExtension(absoluteFileName).equalsIgnoreCase(encryptedExtension)){
					String originalFileName = FilenameUtils.removeExtension(absoluteFileName);
					if(!new File(originalFileName).exists()){
						return false;
					}
				}
			}
		}
		return true;
	}
}
