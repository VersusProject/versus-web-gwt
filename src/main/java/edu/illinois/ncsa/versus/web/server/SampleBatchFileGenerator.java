package edu.illinois.ncsa.versus.web.server;

public class SampleBatchFileGenerator {
	
	private final static String WIN_BATCH_COMMAND_START    = "@echo off\r\necho Running this script will automatically duplicate your images.\r\npause\r\n\r\n";
	private static       String win_batch_duplicate_image  = "";
	private final static String WIN_BATCH_COMMAND_END      = "\r\necho All done.\r\npause";
	
	private final static String LIN_BATCH_COMMAND_START    = "#!/bin/bash\nread -n 1 -p \"Running this script will automatically duplicate your images.\"\n\n";
	private static       String lin_batch_duplicate_image  = "";
	private final static String LIN_BATCH_COMMAND_END      = "\necho \"All done.\"";
	
	private final static String README_FILE_START = "Run the Win_DuplicateImages.bat script on Windows or run Linux_DuplicateImages.sh script on Linux " +
													"to automatically duplicate your images.\r\n\r\nThe sample set contains the following duplicate images:\r\n\r\n";
	private static       String readme_duplicate_image  = "";

	/**
	 * Builds the readme and batch file duplicate image content
	 * 
	 * @param filename
	 * @param occurrences
	 */
	public static void addBatchAction(String filename, Integer occurrences){
		// create readme file containing information on duplicate images
		readme_duplicate_image += filename + ", " + occurrences + " times\r\n";
		
		// separate filename and extension, should always split on the last . in the filename
		String[] filename_tokens = filename.split("\\.(?=[^\\.]+$)");
		
		for (int i = 1; i < occurrences; i++) {
			win_batch_duplicate_image += "copy " + filename + " " + filename_tokens[0] + "_" + i + "." + filename_tokens[1] + "\r\n";
		}
		
		for (int i = 1; i < occurrences; i++) {
			lin_batch_duplicate_image += "cp " + filename + " " + filename_tokens[0] + "_" + i + "." + filename_tokens[1] + "\n";
		}
		
	}
	
	/**
	 * Adds the Windows batch file content strings together
	 * 
	 * @return String
	 */
	public static String WinBuildBatchFile(){
		String batch_file_contents = "";
		
		batch_file_contents += WIN_BATCH_COMMAND_START;
		batch_file_contents += win_batch_duplicate_image;
		batch_file_contents += WIN_BATCH_COMMAND_END;
				
		return batch_file_contents;
	}
	
	/**
	 * Adds the Linux batch file content strings together
	 * 
	 * @return String
	 */
	public static String LinuxBuildBatchFile(){
		String batch_file_contents = "";
		
		batch_file_contents += LIN_BATCH_COMMAND_START;
		batch_file_contents += lin_batch_duplicate_image;
		batch_file_contents += LIN_BATCH_COMMAND_END;
				
		return batch_file_contents;
	}
	
	/**
	 * Adds the readme file content strings together
	 * 
	 * @return String
	 */
	public static String buildReadMeFile(){
		String readme_file_contents = "";
		
		readme_file_contents += README_FILE_START;
		readme_file_contents += readme_duplicate_image;
				
		return readme_file_contents;
	}
	
}
