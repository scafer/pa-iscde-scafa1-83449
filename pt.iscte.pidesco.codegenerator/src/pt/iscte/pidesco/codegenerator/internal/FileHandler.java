package pt.iscte.pidesco.codegenerator.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileHandler {
	public void saveOrUpdateFile(File file, String fileContent) {
		try {
			//Create the file
			if (file.createNewFile()) {
			    System.out.println("File is created.");
			} 
			else {
			    System.out.println("File already exists.");
			}
			
			//Write Content
			FileWriter writer = new FileWriter(file);
			writer.write(fileContent);
			writer.close();
		}
		catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
	public String readFileLines(String filePath)
	{
	    StringBuilder contentBuilder = new StringBuilder();
	    try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) 
	    {
	        stream.forEach(s -> contentBuilder.append(s).append("\n"));
	    }
	    catch (IOException e) 
	    {
	    	System.out.println(e.getMessage());
	        e.printStackTrace();
	    }
	    
	    return contentBuilder.toString();
	}
}