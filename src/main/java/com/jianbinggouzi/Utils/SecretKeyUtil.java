package com.jianbinggouzi.Utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.crypto.Cipher;

/**
 * RSA工具
 * 
 * @author jianbinggouzi
 *
 */
public class SecretKeyUtil {

	private static KeyPair keyPair;

	private static PublicKey publicKey = null;

	private static PrivateKey privateKey = null;

	static {

		initKeys();

	}

	public static PublicKey getPublicKey() {
		return publicKey;
	}

	public static PrivateKey getPrivateKey() {
		return privateKey;
	}

	/**
	 * 从文件中读取KeyPair到当前KeyPair
	 * 
	 * @throws FileNotFoundException
	 *             文件不存在 需要新生成密钥对并保存
	 * @throws IOException
	 *             神仙难救 神仙难救啊
	 * @throws ClassNotFoundException
	 *             神仙难救 神仙难救啊
	 */
	public static void readKeyPairFromFile() throws FileNotFoundException, IOException, ClassNotFoundException {
		FileInputStream fileInputStream = new FileInputStream(new File("keyPair.txt"));
		ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
		keyPair = (KeyPair) objectInputStream.readObject();
		fileInputStream.close();
		objectInputStream.close();

	}

	/**
	 * 保存当前密钥对到文件中
	 * 
	 * @throws IOException
	 *             神仙难救 神仙难救啊
	 */
	public static void saveKeyPairToFile() throws IOException {
		File file = new File("keyPair.txt");
		try {
			file.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
		objectOutputStream.writeObject(keyPair);
		objectOutputStream.flush();
		objectOutputStream.close();
		fileOutputStream.close();

	}

	/**
	 * 初始化密钥，秘钥文件存在则读取，否则新生成并保存
	 */
	public static void initKeys() {
		try {
			readKeyPairFromFile();
			System.out.println("文件读取成功");
		} catch (FileNotFoundException e) {
			System.out.println("文件不存在");
			try {
				KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
				keyPair = generator.genKeyPair();
				saveKeyPairToFile();
				System.out.println("创建文件");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		publicKey = keyPair.getPublic();
		privateKey = keyPair.getPrivate();

	}

	/**
	 * 将Object转换为base64编码代表的未加密的byte[]
	 * 
	 * @param object
	 * @return
	 * @throws IOException
	 */
	public static String getBytesFromObject(Object object) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.flush();
		byte[] bytes = byteArrayOutputStream.toByteArray();

		Base64.Encoder encoder = Base64.getEncoder();
		String result = encoder.encodeToString(bytes);

		objectOutputStream.close();
		byteArrayOutputStream.close();
		return result;
	}

	/**
	 * 从未加密的Base64编码所代表的Byte[]中读取实例
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static Object getObjectFromBytes(String base64) throws Exception {
		Base64.Decoder decoder = Base64.getDecoder();

		byte[] bytes = decoder.decode(base64);

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
		Object object = objectInputStream.readObject();
		objectInputStream.close();
		byteArrayInputStream.close();
		return object;
	}

	/**
	 * 从加密的bytes中解密并实例化对象
	 * 
	 * @param bytes
	 * @return
	 * @throws Exception
	 */
	public static Object decodeFromBytes(byte[] bytes) throws Exception {

		Cipher cipher = Cipher.getInstance("RSA");

		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		bytes = cipher.doFinal(bytes);

		ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(bytes));
		return objectInputStream.readObject();

	}

	/**
	 * 将实例转换为加密后的bytes
	 * 
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static byte[] encodeFromObject(Object object) throws Exception {
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
		objectOutputStream.writeObject(object);
		objectOutputStream.flush();

		byte[] bytes = byteOutputStream.toByteArray();

		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		return cipher.doFinal(bytes);
	}

}
