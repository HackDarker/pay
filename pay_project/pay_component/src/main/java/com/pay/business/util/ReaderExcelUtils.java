package com.pay.business.util;

import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import jxl.Sheet;
import jxl.Workbook;  
  
  
/** 
 * 读取Excel 
 */  
@Component
public class ReaderExcelUtils {
	
	private final static String excel2003L =".xls";    //2003- 版本的excel  
    private final static String excel2007U =".xlsx";   //2007+ 版本的excel
      
    /** 
     * 输入Excel文件,解析后返回ArrayList 
     *  
     * @param file 
     *          输入的Excel文件 
     *  
     * @return ArrayList<Map>，其中的map以第一行的内容为键值 
     * @throws Exception 
     */  
    @SuppressWarnings({ "unchecked", "rawtypes" })  
    public static ArrayList<Map> ReaderExcel(InputStream in,String fileName) throws Exception{        
          
        /* 
         * workbook : 工作簿,就是整个Excel文档 
         * sheet : 工作表 
         * cell : 一个单元格 
         * row : 一行 
         */  
          
        if(checkExcel2007(fileName)){  
            return importToExcel2007(in);  
        }  
          
        //初始化返回值和字段名数组  
        ArrayList<Map> arr = new ArrayList<Map>();  
        String[] title;  
        Workbook workbook = null;  
          
        try{  
            //读取Excel文件  
            workbook = Workbook.getWorkbook(in);  
            //总Sheet数  
            int sheetNumber = workbook.getNumberOfSheets();  
            System.out.println("Sheet总数: "+sheetNumber);
            Map namesMap = new HashMap();
            //sheet名字对象
            StringBuilder builder=new StringBuilder();
            
            for (int i = 0; i < sheetNumber; i++) {  
            	
                Sheet sheet = workbook.getSheet(i);  
                //sheet名
                String sheetName = sheet.getName();
                if(i!=sheetNumber-1){
                	builder.append(sheetName+",");
                }else if(i==sheetNumber-1){
                	builder.append(sheetName);
                }
                //当前页 总记录行数和列数
                int rowCount = sheet.getRows();         //获取行数  
                int columeCount = sheet.getColumns();   //获取列数
                int realCount=0;//非空的行数
                System.out.println("总记录数 : "+rowCount);  
                System.out.println("总列数 : "+columeCount);  
                  
                //第一行为字段名,所以行数大于一才执行  
                if(rowCount > 1 && columeCount >0){  
                    //取第一列 为 字段名  
                    title = new String[columeCount];  
                    for (int j = 0; j < columeCount; j++) {
                    	//getcell()第二个参数为从第几行开始*
                        title[j] = sheet.getCell(j,1).getContents().trim();  
                    }
                    
                    //获取真正的行数
                    for (int h = 1; h < rowCount; h++) {
                    	jxl.Cell[] cell=sheet.getRow(h);
                    	int count=0;
                    	for (int j = 0; j < cell.length; j++) {
							if(cell[i].getContents().trim()==""){
								count++;
							}
						}                    	
                    	if(count == cell.length){
                            break; 
                        }else{
                        	realCount++;
                        }
                    }
                      
                    //取当前页所有值放入list中  
                    for (int h = 1; h < realCount+1; h++) { //非空行数
                    	LinkedHashMap dataMap = new LinkedHashMap();
                        for (int k = 0; k < columeCount; k++) {  //列数  
                            dataMap.put(title[k], sheet.getCell(k,h).getContents());    //getContents() 获取单元格的值  
                        }
                        dataMap.put("sheetName"+sheetName,sheetName);
                        dataMap.put(sheetName+"SheetNumber",realCount);//真正的行数
                        arr.add(dataMap);
                    }  
                }
            }
            if(arr.size()>0){
            	namesMap.put("nameStrs", builder.toString());
                arr.add(namesMap);
            }
        }catch(Exception e){  
            e.printStackTrace();  
        }finally{  
            if(workbook != null){  
                workbook.close();  
                workbook = null;  
            }  
        }         
        return arr;  
    }  
  
      
    /** 
     * 输入2007版以上excel文件,解析后返回ArrayList
     * @param file 
     * @return 
     */  
    @SuppressWarnings("rawtypes")  
    public static ArrayList<Map> importToExcel2007(InputStream in){  
        ArrayList<Map> arr = new ArrayList<Map>();  
        String[] title;  
          
        //初始化  
        XSSFWorkbook workbook = null;  
        XSSFRow row = null;  
        XSSFSheet sheet = null;  
        XSSFCell cell = null;  
          
        try{              
            //读取文件  
            workbook = new XSSFWorkbook(in);  
              
            //文档页数  
            int numOfSheets = workbook.getNumberOfSheets();  
            System.out.println("文档页数 : "+numOfSheets);
            Map namesMap = new HashMap();
            //sheet名字对象
            StringBuilder builder=new StringBuilder();
              
            for (int i = 0; i < numOfSheets; i++) {                
                //获取当前的sheet(工作表)  
                sheet = workbook.getSheetAt(i);
                //sheet名
                String sheetName = sheet.getSheetName();
                if(i!=numOfSheets-1){
                	builder.append(sheetName+",");
                }else if(i==numOfSheets-1){
                	builder.append(sheetName);
                }
                //获取当前页的行数  
                int sheetRows = sheet.getLastRowNum();                
                System.out.println("当前页总行数 : "+sheetRows);  
                //如果当前页行数大于0,则先取第一行为字段名  
                if(sheetRows > 0){  
                    row = sheet.getRow(0);  //当前页 第一行  
                    int cells = row.getLastCellNum();   //第一行 单元格数量  
                    title = new String[cells];  
                    for (int j = 0; j < cells; j++) {  
                        //列为空,则输入空字符串  
                        if(row.getCell(j) == null){  
                            title[j] = "";  
                            continue;  
                        }  
                        cell = row.getCell(j);  
                        switch (cell.getCellType()) {  
                            case Cell.CELL_TYPE_NUMERIC:{  
                                Integer num = new Integer((int) cell.getNumericCellValue());  
                                title[j] = String.valueOf(num);
                                break;  
                            }  
                            case Cell.CELL_TYPE_STRING:{  
                                title[j] = cell.getRichStringCellValue().toString(); 
                                break;  
                            }  
                            default:  
                                title[j] = "";  
                        }  
                    }                     
                    //分行解析  
                    for (int j = 1; j < sheetRows+1; j++) {  
                        //如果是空行,则继续下一条  
                        if(sheet.getRow(j) == null){  
                            continue;  
                        }  
                        //将每行数据放入map中  
                        row = sheet.getRow(j);
                        Map data = new HashMap();
                        data=getCellMap(row,cells,title,sheetName,sheetRows+1);
                        //空行就不添加
                        if(data!=null){
                        	arr.add(data);
                        }
                    }
                    
                    
                }  
            }
            if(arr.size()>0){
            	namesMap.put("nameStrs", builder.toString());
                arr.add(namesMap);
            }
        }catch(Exception e){  
            e.printStackTrace();  
        }finally{  
            try {  
                in.close();  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
        }  
        return arr;  
    }  
      
    /** 
     * 根据文件扩展名判断是否是Excel 2007 以上 
     * @param fileName 
     * @return 
     * @throws Exception 
     */  
    private static boolean checkExcel2007(String fileName) throws Exception{  
    	String fileType = fileName.substring(fileName.lastIndexOf("."));
    	if(excel2003L.equals(fileType)){  
            return false;  //2003-  
        }else if(excel2007U.equals(fileType)){  
            return true;  //2007+  
        }else{
            throw new Exception("解析的文件格式有误！");  
        }
    }
    
    /** 
     *  根据传入的Excel行数据,得到Map数据  
     * @param row 
     * @param cells 
     * @param title 
     * @return 
     */  
    @SuppressWarnings({ "unchecked", "rawtypes" })  
    private static Map getCellMap(XSSFRow row , int cells , String[] title,String sheetName,int sheetRows){  
        //初始化  
        Map data = new HashMap();
        XSSFCell cell = null;  
          
        //分列  
        for (int i = 0; i < cells; i++) {
            //列为空,则输入空字符串  
            if(row.getCell(i) == null){
                data.put(title[i], "");  
                continue;
            }  
            cell = row.getCell(i);  
            switch (cell.getCellType()) {  
                case Cell.CELL_TYPE_NUMERIC:{  
                    if(DateUtil.isCellDateFormatted(cell)){  
                        data.put(title[i], cell.getDateCellValue());  
                    }else{  
                        NumberFormat nf = NumberFormat.getInstance();  
                        nf.setGroupingUsed(false);  
                        data.put(title[i], nf.format(cell.getNumericCellValue()));  
                    }  
                    break;  
                }  
                case Cell.CELL_TYPE_STRING:{  
                    data.put(title[i], cell.getRichStringCellValue());  
                    break;  
                }  
                case Cell.CELL_TYPE_BOOLEAN:{  
                    data.put(title[i], cell.getBooleanCellValue());  
                    break;  
                }  
                default:  
                    data.put(title[i], "");  
            }
        }
        int number=0;
        for (int i = 0; i < data.size(); i++) {
			if(data.get(title[i]).toString().equals("")){
				number++;
			}
		}
        for (int i = 0; i < cells; i++) {
        	if(i==0){
        		data.put("sheetName"+sheetName,sheetName);
        		data.put(sheetName+"SheetNumber",sheetRows);//的行数
        	}
        }
        if(number!=(data.size()-2)){
        	return data;
        }else{
        	return null;
        }
    }  
}  

