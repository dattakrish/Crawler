package com.tcs.crawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class Crawler {
	
	private static File logger = new File("crawler.log");
	private static StringBuilder stringBuilder = new StringBuilder();
	private static String encryptedExtension = "xtbl";//"btcbtcbtc";
	private static String fromLocation = "C:\\crawler";
	private static String sever_specific_xml_file;
	private static String driveToCrawl;

	public static void main(String[] args) throws Exception{
		sever_specific_xml_file = args[0];
		if(args.length>1)
			driveToCrawl = args[1];
		if(sever_specific_xml_file == null || sever_specific_xml_file.trim().length() == 0){
			System.out.println(" \n===== You need to specify the name of xml file for the server =====\n");
			stringBuilder.append(" \n===== You need to specify the name of xml file for the server =====\n");
			throw new Exception(" \n===== You need to specify the name of xml file for the server =====\n");
		}
		long startTime = System.currentTimeMillis();
		try{
			Crawler crawler = new Crawler();
			List<Object> drives = null;
			if(null != driveToCrawl && driveToCrawl.trim().length()>0){
				drives = new ArrayList<Object>();
				drives.add(driveToCrawl);
			}else{
				drives = crawler.findAllDrives();
			}
//			List<Object> drives = crawler.findAllDrives();
			List<String> resultList = new ArrayList<String>();
			for(Object drive : drives){
				System.out.println(" \n===== Fetching directories under "+drive + " =====\n");
				stringBuilder.append(" \n===== Fetching directories under "+drive + " =====\n");
				crawler.navigateDirectories(drive.toString(),resultList);
			}
			System.out.println(" \n===== Total Directories found to be decrypted is "+resultList.size() + " =====\n");
			stringBuilder.append(" \n===== Total Directories found to be decrypted is "+resultList.size() + " =====\n");
			
//			System.out.println(" \n===== Directories found to be decrypted are =====\n");
			stringBuilder.append(" \n===== Directories found to be decrypted are =====\n");
			for(String result : resultList){
//				System.out.println("\n"+result);
				stringBuilder.append("\n"+result);
			}
//			System.out.println(" \n=================================================\n");
			stringBuilder.append(" \n=================================================\n");
			for(String result : resultList){
				crawler.copyAndRun(result);
			}
		}finally{
			stringBuilder.append("\nTotal time taken [ "+ (System.currentTimeMillis() - startTime)+" ]");
			FileWriter fw = new FileWriter(logger.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(stringBuilder.toString());
			bw.close();
			System.out.println("Total time taken [ "+ (System.currentTimeMillis() - startTime)+" ms ]");
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
		if(!isDecryptionComplete(fList)){
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
	
	private void copyAndRun(String destinationPath){
		System.out.println("\n\tStarting copy and run for directory ["+destinationPath+"].....\n");
		stringBuilder.append("\n\tStarting copy and run for directory ["+destinationPath+"].....\n");
		try{
			FileUtils.copyDirectory(new File(fromLocation), new File(destinationPath));
			//long startTime = System.currentTimeMillis();
			Process process = Runtime.getRuntime().exec(destinationPath+"\\SAMDdec.exe "+sever_specific_xml_file, null, new File(destinationPath));
			/*
			 * 30,000 msec or 30 seconds to wait before killing the process
			 */
			/*while(true){
				if((System.currentTimeMillis() - startTime) > 30000){
					System.out.println("Process did not return within 30 seconds for directory ["+destinationPath+"]");
					stringBuilder.append("Process did not return within 30 seconds for directory ["+destinationPath+"]");
					process.destroyForcibly();
					break;
				}
			}*/
//			process.waitFor(1, TimeUnit.MINUTES);// Available in jdk1.8 only
			synchronized (process) {
				process.wait(60000);
			}
		}catch(Exception exception){
			exception.printStackTrace();
			stringBuilder.append("\n\tERROR in copyAndRun for directory ["+destinationPath+"]:\n" + exception.getMessage());
		}
	}
}
