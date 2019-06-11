package com.example.dell.tripsafety.utils;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import static com.baidu.speech.audio.MicrophoneServer.TAG;

//语音取证
public class Voice_Forensics  {
    //队列长度
    static  int q_len=8*2;//30秒数据量 8(2秒)*15
    //实际长度
    int a_len=0;
    //标志 true为需要发送危险语音 false为不需要发送
    boolean flag=true;

    byte [] audio_data=new byte[0];
    static byte [] temp_data;

    //添加数据
    public  void add_data(byte [] data){
        Log.w("audio ","add_data");
        a_len++;
        audio_data=byteMerger(audio_data,data);
        if(a_len>=8*2)
        {   a_len=0;
            temp_data=audio_data;
            audio_data=new byte[0];
            if(flag)
            {
                send_data();
            }else
            {
            }
        }




    }
    // 开一个线程 生成文件 发送文件
    private  void send_data()
    {       Log.w("audio ","发送");
            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    //生成文件
                    byte [] fileheader=fileHeader(temp_data.length);
                    byte [] file=byteMerger(fileheader,temp_data);
                    //调用接口上传
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
                    Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                    String str  = formatter.format(curDate);

                    AVFile avfile = new AVFile(str,file);
                    avfile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(AVException e) {

                        }
                    });


                }
            });
            t.start();
    }
    //设置标志位
    void set_flag(boolean b)
    {
        flag=b;
    }

//java 合并两个byte数组  
public  static byte[] byteMerger(byte[] byte_1,byte [] byte_2)
{
    byte [] byte_3=new byte[byte_1.length+byte_2.length];
    System.arraycopy(byte_1,0,byte_3,0,byte_1.length);
    System.arraycopy(byte_2,0,byte_3,byte_1.length,byte_2.length);
    return byte_3;
}

public byte[] fileHeader(int totalAudioLen)
{   int channels =1;
    long longSampleRate=16000;
    long byteRate = 16 * longSampleRate * channels / 8;
    long totalDataLen;
    totalDataLen = totalAudioLen + 36;
    byte[] header = new byte[44];
    header[0] = 'R'; // RIFF
    header[1] = 'I';
    header[2] = 'F';
    header[3] = 'F';
    header[4] = (byte) (totalDataLen & 0xff);//数据大小
    header[5] = (byte) ((totalDataLen >> 8) & 0xff);
    header[6] = (byte) ((totalDataLen >> 16) & 0xff);
    header[7] = (byte) ((totalDataLen >> 24) & 0xff);
    header[8] = 'W';//WAVE
    header[9] = 'A';
    header[10] = 'V';
    header[11] = 'E';
    //FMT Chunk
    header[12] = 'f'; // 'fmt '
    header[13] = 'm';
    header[14] = 't';
    header[15] = ' ';//过渡字节
    //数据大小
    header[16] = 16; // 4 bytes: size of 'fmt ' chunk
    header[17] = 0;
    header[18] = 0;
    header[19] = 0;
    //编码方式 10H为PCM编码格式
    header[20] = 1; // format = 1
    header[21] = 0;
    //通道数
    header[22] = (byte) channels;
    header[23] = 0;
    //采样率，每个通道的播放速度
    header[24] = (byte) (longSampleRate & 0xff);
    header[25] = (byte) ((longSampleRate >> 8) & 0xff);
    header[26] = (byte) ((longSampleRate >> 16) & 0xff);
    header[27] = (byte) ((longSampleRate >> 24) & 0xff);
    //音频数据传送速率,采样率*通道数*采样深度/8
    header[28] = (byte) (byteRate & 0xff);
    header[29] = (byte) ((byteRate >> 8) & 0xff);
    header[30] = (byte) ((byteRate >> 16) & 0xff);
    header[31] = (byte) ((byteRate >> 24) & 0xff);
    // 确定系统一次要处理多少个这样字节的数据，确定缓冲区，通道数*采样位数
    header[32] = (byte) (1 * 16 / 8);
    header[33] = 0;
    //每个样本的数据位数
    header[34] = 16;
    header[35] = 0;
    //Data chunk
    header[36] = 'd';//data
    header[37] = 'a';
    header[38] = 't';
    header[39] = 'a';
    header[40] = (byte) (totalAudioLen & 0xff);
    header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
    header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
    header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
    return  header;
}

}
