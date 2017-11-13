package pk.edu.kics.dsl.qa.qe;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.calcite.avatica.proto.Common.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.common.collect.Table.Cell;

public class ExcelWriterPOI {


	 private static final String FILE_NAME = "resources/excelResults.xlsx";
	 private static final String RESULTS_FILE_NAME = "resources/AllTechniquesResults.xlsx";
     
      
      static XSSFWorkbook workbook = new XSSFWorkbook();
	    static XSSFSheet sheet = workbook.createSheet("Score Sheet");
	    private static int rownum=sheet.getLastRowNum();
	    
	 
/*	 public static void write(List<String> items)
	 {

	        XSSFWorkbook workbook = new XSSFWorkbook();
	        XSSFSheet sheet = workbook.createSheet("KLD CHOOSEN ITEMS");
	        Object[][] datatypes = {
	                {"TopTerms"},
	                {items.get(0), items.get(1),items.get(2),items.get(3),items.get(4),items.get(5),items.get(6),items.get(7),items.get(8), items.get(9),items.get(10)}
	                
	                
	                
	                
	                
	                
	                
	        };

	        int rowNum = 0;
	        System.out.println("Creating excel");

	        for (Object[] datatype : datatypes) {
	            XSSFRow row = sheet.createRow(rowNum);
	            int colNum = 0;
	            for (Object field : datatype) {
	                XSSFCell cell = row.createCell(colNum++);
	                if (field instanceof String) {
	                    cell.setCellValue((String) field);
	                } else if (field instanceof Integer) {
	                    cell.setCellValue((Integer) field);
	                }
	            }
	        }

	        try {
	            FileOutputStream outputStream = new FileOutputStream(FILE_NAME);
	            workbook.write(outputStream);
	            workbook.close();
	        } catch (FileNotFoundException e) {
	            e.printStackTrace();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        System.out.println("Done");
	    }

*/






	public static void writeResults(Map<String, Double>score, ArrayList<Double> tprResult,
			ArrayList<Double> fprResult, int questionNo) {
		// TODO Auto-generated method stub
		
		
		
		
		
	     List<String>termsList=new ArrayList(score.keySet());
	     List<Double>Termsscore=new ArrayList(score.values());
	     
	     
	       //rownum = sheet.getLastRowNum(); 
	     //terms
	     //for (String key : termsList) 
	     { 
	    	 XSSFRow row1 = sheet.createRow(rownum++);
	    	 
	    	 int cellnum = 0;
	    	 for (String obj :termsList )
	    	 { 
	    		 
	    		 XSSFCell cell = row1.createCell(cellnum++);
	    		 if (obj instanceof String)
	    		 { cell.setCellValue((String) obj); 
	    		 }
	    		  
	    	 }
	    
	    	
	    	 
	     }
	     //score
	     
	     {
	    	 XSSFRow row2 = sheet.createRow(rownum++);
	    	 
	    	 int cellnum = 0;
	    	 for (Double obj :Termsscore )
	    	 { 
	    		 
	    		 XSSFCell cell = row2.createCell(cellnum++);
	    		 if (obj instanceof Double)
	    		 { cell.setCellValue((Double) obj); 
	    		 }
	    		  
	    	 }
	    
	     }
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	     
	      
	     {
	    	 XSSFRow row3 = sheet.createRow(rownum++);
	    	 
	    	 int cellnum = 0;
	    	 for (Double obj :tprResult )
	    	 { 
	    		 
	    		 XSSFCell cell = row3.createCell(cellnum++);
	    		 if (obj instanceof Double)
	    		 { cell.setCellValue((Double) obj); 
	    		 }
	    		  
	    	 }
	    
	     }
	    	
	     {
	    	 XSSFRow row4 = sheet.createRow(rownum++);
	    	 
	    	 int cellnum = 0;
	    	 for (Double obj :fprResult )
	    	 { 
	    		 
	    		 XSSFCell cell = row4.createCell(cellnum++);
	    		 if (obj instanceof Double)
	    		 { cell.setCellValue((Double) obj); 
	    		 }
	    		  
	    	 }
	    
	     }
	     
	     
	     
	       
	     
	     
	     
	     
	     
	     
	     FileOutputStream os;
		try {
			os = new FileOutputStream(FILE_NAME);
			try {
				workbook.write(os);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
	     
	}//writeResults
	
	
	
	
	
	
	
	
	public static void writeScoreResults(int totalTerms, int scoreOfParticularTechnique) {
		// TODO Auto-generated method stub
		
		
		
		
		
	     /*
	     
	       //rownum = sheet.getLastRowNum(); 
	     //terms
	     //for (String key : termsList) 
	     
	    	 XSSFRow row1 = sheet.createRow(rownum++);
	    	 
	    	 int cellnum = 0;
	    	 
	    	  
	    		 
	    		 XSSFCell cell = row1.createCell(cellnum++);
	    		 if (totalTerms instanceof Integer)
	    		 { cell.setCellValue((In) obj); 
	    		 }
	    		  
	    	 
	    
	    	
	    	 
	     }
	   
	     
	     
	       
	     
	     
	     
	     
	     
	     
	     FileOutputStream os;
		try {
			os = new FileOutputStream(RESULTS_FILE_NAME);
			try {
				workbook.write(os);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
	     
*/
	
	}

	    
	     
	     
	     
	     
	     
	     
	     
	     
	








































}//class



	 
	 
	 
	 
	
	 

