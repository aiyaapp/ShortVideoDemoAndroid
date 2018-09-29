package com.aiyaapp.aiyamediaeditor.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * [File工具类]
 */
public class FileUtil {

	private final static String TAG = "FileUtil";

	public static DecimalFormat DIGITAL_FORMAT_1 = new DecimalFormat("####.0");
	
	/**
	 * 判断某个文件是否存在
	 * @param filePath
	 * @return
	 */
	public static boolean isFileExist(String filePath) {
		File file = new File(filePath);
		return file.exists();
	}

	public static void renameFile(String filePath, String renamePath) {
		if (filePath != null && renamePath != null) {
			File dirTmp = new File(filePath);
			dirTmp.renameTo(new File(renamePath));
		}
	}
	/**
	 * 创建文件
	 * @param filePath
	 * return
	 */
	public static boolean createFile(String filePath) {
		File file = new File(filePath);
		try {
			if (file.exists()) {
				return true;
			}
			String parentPath = file.getParent();
			File parent = new File(parentPath);

			if (!parent.exists()) {
				parent.mkdirs();
			}

			file.createNewFile();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

    /**
     * 创建文件夹
     * @param dir
     * return
     */
    public static boolean createDir(String dir) {
        File file = new File(dir);
	    if (!file.exists()) {
		    return file.mkdirs();
	    } else
		    return file.isDirectory() || file.delete() && file.mkdirs();
    }

	/**
	 * 重命名文件 (1), (2) 这样来的 
	 * @author douzifly
	 * @param old
	 * @return
	 */
	public static String rename(String old){
		String newName = "";
		int index = 1;
		do{
			int lastDot = old.lastIndexOf(".");
			String surfix = "";
			String noSurfix = old;
			if(lastDot > 0){
				surfix = old.substring(lastDot);
				noSurfix = old.substring(0, lastDot);
			}
			newName = noSurfix + "(" + index + ")" + surfix;
			index++;
			if(new File(newName+".tmp").exists()){
				// if temporary file exists , return temporary file
				break;
			}
		}
		while(new File(newName).exists());
		return newName;
	}

	/**
	 * [删除文件夹]<br/>
	 * 另外开一个线程进行文件夹删除
	 * 
	 * @param path
	 */
	public static void deleteFolder(String path) {

		if (path == null || "".equals(path)) {
			return;
		}

		final File file = new File(path);
		if (file == null || !file.exists()) {
			return;
		}

		// 另开线程删除文件夹
		new Thread() {
			@Override
			public void run() {
				deleteFolder(file);
			}
		}.start();
	}

	/**
	 * [删除目录及其子文件]
	 * 
	 * @param file
	 */
	public static void deleteFolder(File file) {
		if (file == null || !file.exists()) {
			return;
		}

		deleteFolderFiles(file);
		file.delete();
	}
	
	public static void deleteFolderFiles(File file) {
		if (file == null || !file.exists()) {
			return;
		}
		
		if (file.isFile()) {
			//Log.d(TAG, "delete file:" + file.getName());
			file.delete();
		} else if (file.isDirectory()) {
			//Log.d(TAG, "delete folder:" + file.getName());
			File files[] = file.listFiles();
			if (files == null) {
				return;
			}
			for (int i = 0; i < files.length; i++) {
				//Log.d(TAG, "delete sub:" + files[i].getName());
				deleteFolder(files[i]);
			}
		}
	}

	public static boolean deleteFile(String path) {
		if (path == null || path.equals("")) {
			return false;
		}

		File file = new File(path);
		if (file == null || !file.exists()) {
			return true;
		}

		return file.delete();
	}

	public static boolean deleteFiles(List<String> pathList) {

		if (pathList == null || pathList.size() == 0) {
			return false;
		}
		boolean result = true;
		int length = pathList.size();
		for (int i = 0; i < length; i++) {
			result = deleteFile(pathList.get(i));
		}
		return result;
	}

	/**
	 * [读取文件]<br/>
	 * 将文本文件每一行读到List的一个Item
	 * 
	 * @param path
	 * @return
	 */
	public static ArrayList<String> readFileAsList(String path) {
		if (path == null || "".equals(path)) {
			return null;
		}

		File file = new File(path);
		if (file == null || !file.exists()) {
			return null;
		}

		return readFileAsList(file);
	}

	/**
	 * [读取文本文件] 将文本内容的每一行读取到List的Item中
	 * 
	 * @param file
	 * @return
	 */
	public static ArrayList<String> readFileAsList(File file) {
		return readFileAsList(file, -1);
	}

	public static ArrayList<String> readFileAsList(String path, int maxSize) {
		if (path == null || "".equals(path)) {
			return null;
		}

		File file = new File(path);
		if (file == null || !file.exists()) {
			return null;
		}
		
		return readFileAsList(file, maxSize);
	}
	
	/**
	 * [读取文本文件] 将文本内容的每一行读取到List的Item中
	 * 
	 * @param file
	 * @return
	 */
	public static ArrayList<String> readFileAsList(File file, int maxSize) {

		if (file == null || !file.exists()) {
			return null;
		}

		ArrayList<String> list;
		if (maxSize <= 0) {
			list = new ArrayList<String>();
		} else {
			list = new ArrayList<String>(maxSize);
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String str = null;
			while ((str = reader.readLine()) != null) {
				list.add(str.trim());
				if (maxSize > 0 && list.size() >= maxSize) {
					//Log.i(TAG, "文件行数超出最大长度: " + maxSize + " - 该文件为: " + file.getName());
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return list;
	}
	
	/**
	 * [读取文本文件] 将文本内容读取到String中
	 * @param file
	 * @param charsetName 字符集，默认使用“utf-8”
	 * @return
	 */
	public static String readFileAsString(File file, String charsetName) {

		if (file == null || !file.exists()) {
			return null;
		}

		String charset = charsetName == null ? "utf-8" : charsetName;
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = null;
		try {
			FileInputStream in = new FileInputStream(file);
			// 增加传入字符集(胡启明修改)
			reader = new BufferedReader(new InputStreamReader(in, charset));
			String str = null;
			while ((str = reader.readLine()) != null) {
				sb.append(str);
				sb.append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
		}
		// 将末尾加上的换行符去掉(胡启明修改)
		return sb.toString().trim();
	}

	/**
	 * [读取文本文件] 将文本内容的每一行读取到List的Item中
	 * 
	 * @param file
	 * @return
	 */
	public static String readFileAsString(File file) {
		return readFileAsString(file, null);
	}
	
	public static String readFileFormAsset(Context context, String filePath) {
		try {
			InputStream in = context.getResources().getAssets().open(filePath);
			int length = in.available();                        
	        byte [] buffer = new byte[length];                    
	        in.read(buffer);                    
	        String result = new String(buffer, "utf-8");
	        return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static List<String> readFileFormRaw(Context context, int resId) {
		try {
			List<String> list = new ArrayList<String>();
			InputStream in = context.getResources().openRawResource(resId);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String str = null;
			while ((str = reader.readLine()) != null) {
				list.add(str.trim());
			}
			return list;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] readFileAsBytes(File file) {

		if (file == null || !file.exists()) {
			return null;
		}
		FileInputStream in = null;
		try {
			int num = -1;
			byte[] buf = new byte[1024];
			in = new FileInputStream(file);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((num = in.read(buf, 0, buf.length)) != -1) {
				baos.write(buf, 0, num);
			}
			byte[] b = baos.toByteArray();
			baos.flush();
			baos.close();
			return b;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
		}
		return null;
	}

	
	/**
	 * [创建指定文件的前置文件夹]<BR>
	 * @param filePath
	 */
	public static boolean createPreDir(String filePath) {
		String preDirStr = getPreDirPath(filePath);
		if (preDirStr != null) {
			File preDir = new File(preDirStr);
			if (preDir.exists()) {
				return true;
			} else {
				boolean isSuc = preDir.mkdirs();
				return isSuc;
			}
		}
		return false;
	}
	



	/**
	 * [格式化文件大小]<br/>
	 * 例如： length 1022114 返回 998.2KB
	 * 
	 * @param length
	 * @return
	 */
	public static String parseFileSizeF(long length) {

		if (length == 0) {
			return "0M";
		}

		String[] syn = { "B", "KB", "M", "G" };
		int i = 0;
		float f = length;
		while (f >= 1024) {
			if (i >= syn.length - 1) {
				break;
			}
			f = f / 1024;
			i++;
		}

		String size = DIGITAL_FORMAT_1.format(f) + syn[i];
		return size;
	}
	
	public static String parseHZ(int length) {

		String[] syn = { "HZ", "KHZ", "MHZ", "GHZ" };
		
		int i = 0;
		while (length > 1000) {
			if (i >= syn.length - 1) {
				break;
			}
			length = length / 1000;
			i++;
		}

		return length + syn[i];
	}

	public static String parseFileSize(long length, boolean hasUnit) {
		
		String[] syn = { "B", "KB", "M", "G" };
		int i = 0;
		while (length > 1024) {
			if (i >= syn.length - 1) {
				break;
			}
			length = length / 1024;
			i++;
		}

		if (hasUnit) {
			return length + syn[i];
		} else {
			return length + "";
		}
	}

	/**
	 * [格式化文件大小]<br/>
	 * 例如： length 1022114 返回 998KB
	 * 
	 * @param length
	 * @return
	 */
	public static String parseFileSize(long length) {
		return parseFileSize(length, true);
	}

	/**
	 * [获取文件大小]<br/>
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileSize(String fileName) {
		File file = new File(fileName);

		if (!file.exists() || !file.isFile())
			return null;

		long length = file.length();
		String size = parseFileSize(length);
		//Log.i("fileSize", "fileSize:" + size);
		return size;
	}
	
	/**
	 * [获取文件大小]<br/>
	 * 
	 * @param fileName
	 * @return
	 */
	public static long getFileLength(String fileName) {
		File file = new File(fileName);

		if (!file.exists() || !file.isFile())
			return 0;

		long length = file.length();
		//Log.i("fileSize", "file.length:" + length);
		return length;
	}

	public static String parseBitrate(long length) {
		return (length/1000) + " Kbps";
	}
	
	public static String parseDowloadRate(long length) {
		return parseFileSize(length) + "/s";
	}

	/**
	 * [获取文件夹大小]<br/>
	 * 
	 * @param f
	 * @return
	 */
	public static long getFileSize(File f) {
		long size = 0;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}


	public static String getPreDirPath(String path) {
		if (path == null) {
			return null;
		}
		int index = path.lastIndexOf("/");
		if (index < 0) {
			return null;
		} else if (index == 0) {
			index += 1;
		}
		String prePath = path.substring(0, index);
		return prePath;
	}
	
	public static String getFileSuffix(String name){
		if(name == null || !name.contains(".")){
			return name;
		}
		return name.substring(name.lastIndexOf(".") + 1);
	}

	public static String getSimpleFileName(String path) {
		int fristIndex = path.lastIndexOf("/");
		int lastIndex = path.lastIndexOf(".");

		if (fristIndex == path.length() - 1 && fristIndex != 0) {
			fristIndex = path.substring(0, fristIndex).lastIndexOf("/");
			lastIndex = path.length() - 1;
		}
		
		if (fristIndex == -1) {
			fristIndex = 0;
		} else {
			fristIndex += 1;
		}
		if (lastIndex == -1) {
			lastIndex = path.length();
		}
		if (lastIndex < fristIndex) {
			fristIndex = 0;
			lastIndex = path.length();
		}
		
		return path.subSequence(fristIndex, lastIndex).toString();
	}

	public static String encodeNameFromBase64(String value, String verifyKey) {
		value = verifyKey + value;
		try {
			String base64String = Base64.encodeToString(value.getBytes(), Base64.NO_WRAP);
			base64String = base64String.replace("/", "-");
			return base64String;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String fileReader(InputStream data) {
		BufferedReader reader = null;
		StringBuffer laststr = new StringBuffer();
		try {
			reader = new BufferedReader(new InputStreamReader(data, "UTF-8"));
			String tempString = null;
			while ((tempString = reader.readLine()) != null) {
				// Log.v("debug", "line " + line + ": " + tempString);
				laststr.append(tempString);
			}
			reader.close();
			return laststr.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e1) {
				}
			}
		}
		return null;
	}
	
	public static File getFolder(String path) {
		if (path != null) {
			File folder = new File(path);
			if (!folder.exists()) {
				folder.mkdir();
			}
			return folder;
		} else {
			return null;
		}
	}
	
	public static String getFileName(String path) {
		if (path == null) {
			return null;
		}
		int index = -1;
		index = path.lastIndexOf("/");
		return path.substring(index + 1);
	}
	
	public static void writeFileOutput(Context context, String fileName, String message) {
		if (context == null || fileName == null || message == null) {
			return;
		}

		FileOutputStream fout = null;
		try {
			fout = context.openFileOutput(fileName, context.MODE_PRIVATE);
			byte[] bytes = message.getBytes();
			fout.write(bytes);
			fout.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fout != null) {
				try {
					fout.close();
				}catch (Exception e) {}
			}
		}
	}

	public static String readFileInput(Context context, String fileName) {
		if (context == null || fileName == null) {
			return null;
		}
		
		String res = null;
		FileInputStream fin = null;
		try {
			fin = context.openFileInput(fileName);
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = new String(buffer, "UTF-8");
		} catch (Exception e) {
		} finally {
			if (fin != null) {
				try {
					fin.close();
				}catch (Exception e2) {}
			}
		}
		return res;
	}
	
	public static boolean deleteDataFile(Context context, String fileName) {
		if (context == null || fileName == null) {
			return false;
		}
		return context.deleteFile(fileName);
	}
	
	public static String getFilePostfix(String fileName) {
		if (fileName == null || "".equals(fileName)) {
			return null;
		}
		int index = fileName.lastIndexOf(".");
		if (index <= 0) {
			return null;
		}
		index = index + 1;
		String filePostFix = fileName.substring(index);
		return filePostFix;
	}
	
	public static boolean copyAssetsFileToSD(Context context, String assetsPath, String sdcardPath) {
		if (assetsPath == null || sdcardPath == null) {
			return false;
		}
		
		try {
			createFile(sdcardPath);
			OutputStream myOutput = new FileOutputStream(sdcardPath);
			InputStream myInput = context.getAssets().open(assetsPath);
			byte[] buffer = new byte[1024];
			int length = myInput.read(buffer);
			while (length > 0) {
				myOutput.write(buffer, 0, length);
				length = myInput.read(buffer);
			}
	
			myOutput.flush();
			myInput.close();
			myOutput.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static File getExternalFilesDir(Context context){
		File f=context.getExternalFilesDir(null);
		if(f!=null)return f;
		return context.getFilesDir();
	}
	
	public static boolean copyFile(String fromFile, String toFile) {
		if (TextUtils.isEmpty(fromFile) || TextUtils.isEmpty(toFile)) {
			return false;
		}
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(fromFile);
			return saveInputStream(fin, toFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
	
	public static boolean saveInputStream(InputStream inputStream, String sdcardPath) {
		if (inputStream == null || TextUtils.isEmpty(sdcardPath)) {
			return false;
		}
		OutputStream myOutput = null;
		try {
			createFile(sdcardPath);
			myOutput = new FileOutputStream(sdcardPath);
			byte[] buffer = new byte[1024];
			int length = inputStream.read(buffer);
			while (length > 0) {
				myOutput.write(buffer, 0, length);
				length = inputStream.read(buffer);
			}
			myOutput.flush();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (myOutput != null) {
				try {
					myOutput.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return false;
	}
}
