package com.example.dell.tripsafety.SecondMap;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class Net {
	//静态网络传输函数
	public static void NetTransition(String IP, int port, String inStr, StringBuilder outStr){
		Socket socket;
		InputStream in;
		OutputStream out;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		DataOutputStream dataOutputStream = null;

		try {
			Log.d("PGNnet", "netTransition: 开始创建socket连接");
			socket = new Socket(IP, port);
			// socket = new Socket("192.168.43.250", 12397);
			socket.setSoTimeout(30000);

			//将要发送的数据转换为字节
			byte[] outByte = inStr.getBytes();

			Log.d("PGNnet", "netTransition: 开始发送数据！");

			//发送数据
			out = socket.getOutputStream();
			dataOutputStream = new DataOutputStream(out);
			dataOutputStream.write(outByte);   //写入缓冲区
			dataOutputStream.flush();    //正式发送
			socket.shutdownOutput();   //关闭输出流

			Log.d("PGNnet", "netTransition: 数据发送完毕！");

			//接收数据
			in = socket.getInputStream();
			inputStreamReader = new InputStreamReader(in, "utf-8");
			bufferedReader = new BufferedReader(inputStreamReader);
			//读取接收的数据
			String receivedData;

			receivedData = bufferedReader.readLine();
			outStr.append(receivedData);

			Log.d("PGNnet", "netTransition: 网络接收到服务器的值为"+outStr.toString());

			socket.shutdownInput();      //关闭输入流

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (dataOutputStream != null) {
				try {
					dataOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
