package com.pay.business.util.nowpay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

public class FTPUtils {
	private static Logger logger = Logger.getLogger(FTPUtils.class);
	private static int port = 2211;
	private static String username = "000000001557477";
	private static String password = "Y6aYI9oIJZQDepIMDadM";
	
	/**
	 * ftp下载到本地
	 * @param ip   ip或域名地址
	 * @param fileDir  文件目录
	 * @param fileName   文件名
	 * @param saveFTPDir  本地保存地址
	 * @return
	 */
	public static boolean fileDown(String ip,String fileDir,String fileName,String saveFTPDir){
         FTPClient ftp = null; 
         File file = new File(saveFTPDir);    
         if(!file.exists() && !file.isDirectory()){//如果文件夹不存在则创建 
        	 System.out.println("文件目录不存在，创建。");
             file.mkdirs();
         }
         try{
             //ftp的数据下载
             ftp = new FTPClient();
             ftp.connect(ip, port);   
             ftp.login(username, password);
             ftp.setFileType(FTPClient.BINARY_FILE_TYPE);//传输图片的话设置文件类型为二进制
             //ftp.setControlEncoding("UTF-8");//如果有中文文件名的话需要设置
             
             //设置linux环境
             FTPClientConfig conf = new FTPClientConfig( FTPClientConfig.SYST_UNIX);
             ftp.configure(conf);
             
             //判断是否连接成功
             int reply = ftp.getReplyCode();
             if (!FTPReply.isPositiveCompletion(reply)){
                 ftp.disconnect();
                 System.out.println("FTP服务无法连接！");
                 return false;
             }
             //设置访问被动模式
             ftp.setRemoteVerificationEnabled(false);
             ftp.enterLocalPassiveMode();
             //切换工作目录到文件所在的目录
             //boolean dir = ftp.changeWorkingDirectory(new String(fileDir.getBytes(),FTP.DEFAULT_CONTROL_ENCODING));//如果是中文路径需要处理
             boolean dir = ftp.changeWorkingDirectory(fileDir);
             if (dir) { 
                 //检索ftp目录下所有的文件
                 FTPFile[] fs = ftp.listFiles(); 
                 for(FTPFile f : fs){
                	 if(f.getName().equals(fileName)){
	            		   File localFile = new File(saveFTPDir+f.getName());    
	                       OutputStream ios = new FileOutputStream(localFile);     
	                       ftp.retrieveFile(f.getName(), ios);  
	                       ios.close(); 
	                       return true;
                	 }
                       
                 }
                 return false;
             }else {
            	 System.out.println("服务器编码方式可能有问题，请检查！");
                 return false;
             }
         }catch (Exception e){
             e.printStackTrace();
             System.out.println("ftp下载文件发生异常！");
             return false;
         }finally{
             if(ftp != null)  try {ftp.disconnect();} catch (IOException ioe) {}  
         }
     }
	
	public static void main(String[] args) {
		String date="2017-11-13";
//		String userDirPath = System.getProperty("user.dir");
		fileDown("file.ipaynow.cn", "/agentbusi/","agentbusi"+date+".zip", "D://");
	}
}
