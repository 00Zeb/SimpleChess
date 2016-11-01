package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileHandler {
	
	public static void set(Object obj, String fileName) {
		ObjectOutputStream objectOutputStream = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(fileName);
			objectOutputStream = new ObjectOutputStream(fileOutputStream);
			objectOutputStream.writeObject(obj);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				objectOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Object get(String fileName) {
		ObjectInputStream objectInputStream = null;
		Object obj = null;
		try {
			File file = new File(fileName);
			if (file.exists()) {
				FileInputStream fileInputStream = new FileInputStream(file);
				if (fileInputStream != null) {
					objectInputStream = new ObjectInputStream(fileInputStream);
					obj =  objectInputStream.readObject();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (objectInputStream != null) {
					objectInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return obj;
	}

}
