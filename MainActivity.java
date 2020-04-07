package com.example.x_2;
import com.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.FileReader;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.lang.Thread;
import java.lang.Math;
import android.Manifest;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.telephony.mbms.StreamingServiceInfo;
import android.view.View;

import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.*;
import java.io.File;
import android.widget.TextView;
import android.widget.*;
import android.widget.Button;
import org.w3c.dom.Text;
import com.example.x_2.R;
import java.util.Vector;

import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity {

    private static final int samplerate = 44100;
    private static final int channels = AudioFormat.CHANNEL_IN_MONO;
    private static final int encoding = AudioFormat.ENCODING_PCM_FLOAT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;
    private float thre=0;
    private float thre2=0;
    private int MODE=0;
    private int TEMPO=0;
    private float TEMPO_CONSTANT=0;
    private float freq=0;
    private double freq1=0;
    private double freq2=0;
    private double[][] buffer=new double[20][100000];
    Spinner s1;
    TextView test=findViewById(R.id.show);
    String song;
    private MediaPlayer[][] mp=new MediaPlayer[19][12];
    FirebaseStorage storage=FirebaseStorage.getInstance();
    StorageReference main_ref=storage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},1);
        //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        final String[] list=getResources().getStringArray(R.array.a1);
        final TextView t = (TextView) findViewById(R.id.show);
        s1=findViewById(R.id.spinner3);



        ArrayAdapter<String> myAdapter1=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.a1));
        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s1.setAdapter(myAdapter1);
        int start=R.array.a1;
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                song=list[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Button b=findViewById(R.id.load);
        final int flag=0;
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AssetManager A=getAssets();

                //t.setText("loading the song");
                InputStream[] i_arr=new InputStream[20];
                double[][] comparison_arr=new double[20][10000];
                BufferedReader[] buff_arr=new BufferedReader[20];



                for(int j=0;j<12;j++)//reading the comparison data from the files
                {
                    try {
                        i_arr[j] = A.open(song + "/f_data/" + String.valueOf(j) + ".txt");


                        buff_arr[j] = new BufferedReader(new InputStreamReader(i_arr[j]));
                        String read = null;
                        for (int k = 0; (read = buff_arr[j].readLine()) !=null;k++)
                        {
                          comparison_arr[j][k]=Double.valueOf(read);
                        }


                    }
                    catch (Exception e)
                    {
                         t.setText("there is some problem with the database");

                    }
                }
       try{
            InputStream stream_temp=A.open(song+"/data.txt");
            BufferedReader buf_temp=new BufferedReader(new InputStreamReader(stream_temp));
            MODE=Integer.valueOf(buf_temp.readLine());
            TEMPO=Integer.valueOf(buf_temp.readLine());
            TEMPO_CONSTANT=Float.parseFloat(buf_temp.readLine());
        }
        catch (Exception e2){
            t.setText("there is some problem with the database");
        }
        for(int i=TEMPO-9;i<=TEMPO+9;i++)
        {
            String temp=String.valueOf(i);
            final int index1=i-96;
            for(int j=0;j<12;j++){
                final int index2=j;
                String temp2=String.valueOf(j);

                StorageReference ref2=main_ref.child(song +"/"+temp+"/"+temp2+".mp3");
                ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String loc;
                        loc=uri.toString();
                        try{
                            mp[index1][index2]=new MediaPlayer();
                            mp[index1][index2].setDataSource(loc);
                            mp[index1][index2].prepare();
                            t.setText(loc);
                        }
                        catch (Exception e){


                        }

                    }
                });
            }

        }
                     }
        });


        //Button b =(Button)findViewById(R.id.btnStart);


    }
    public int load(View v){
        AssetManager A=getAssets();
        TextView t = (TextView) findViewById(R.id.show);
        t.setText("loading the song");
        InputStream[] i_arr=new InputStream[20];
        double[][] comparison_arr=new double[20][10000];
        BufferedReader[] buff_arr=new BufferedReader[20];
        final String[] list=getResources().getStringArray(R.array.a1);
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                song=list[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        for(int j=0;j<12;j++)//reading the comparison data from the files
        {
            try {
                i_arr[j] = A.open(song + "/f_data/" + String.valueOf(j) + ".txt");


                //buff_arr[j] = new BufferedReader(new InputStreamReader(i_arr[j]));
                //String read = null;
                //for (int k = 0; (read = buff_arr[j].readLine()) !=null;k++)
                //{
                  //  comparison_arr[j][k]=Double.valueOf(read);
                //}


            }
            catch (Exception e)
            {
                t.setText("there is some problem with the database");
                return 0;
            }
        }
       /* try{
            InputStream stream_temp=A.open(song+"/data.txt");
            BufferedReader buf_temp=new BufferedReader(new InputStreamReader(stream_temp));
            MODE=Integer.valueOf(buf_temp.readLine());
            TEMPO=Integer.valueOf(buf_temp.readLine());
            TEMPO_CONSTANT=Float.parseFloat(buf_temp.readLine());
        }
        catch (Exception e2){

        }
        for(int i=105-9;i<=TEMPO+105;i++)
        {
            String temp=String.valueOf(i);
            final int index1=i;
            for(int j=0;j<12;j++){
                final int index2=j;
                String temp2=String.valueOf(j);

                StorageReference ref2=main_ref.child(song +"/"+temp+"/"+temp2+".mp3");
                ref2.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String loc;
                        loc=uri.toString();
                        try{
                            mp[index1][index2].setDataSource(loc);
                            mp[index1][index2].prepare();
                        }
                        catch (Exception e){

                        }
                    }
                });
            }
        }
        t.setText("loading completed");*/
        return 0;
    }




    public int play(View v) {

        AssetManager A=getAssets();
        TextView t = (TextView) findViewById(R.id.show);
        InputStream[] i_arr=new InputStream[20];
        double[][] comparison_arr=new double[20][1000000];
        BufferedReader[] buff_arr=new BufferedReader[20];


        int bufferSize = AudioRecord.getMinBufferSize(samplerate, channels, encoding);
        bufferSize = samplerate*4 ;
        //000000000000000000000000000000000000000int buff_size=bufferSize*2;
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, samplerate, channels, encoding, bufferSize);
        float[] buf = new float[bufferSize / 4];//creating buffers for reading the audio data from the microphone
        float[] buf2 = new float[bufferSize / 4];
        float[] buf3= new float[bufferSize/4];
        float[] buf4= new float[bufferSize/4];
        float[] buf5= new float[bufferSize/4];
        float[] buf6= new float[bufferSize/4];
        float[] buf7= new float[bufferSize/4];
        float[] buf8= new float[bufferSize/4];
        float[] buf9= new float[bufferSize/4];
        float[] buf10= new float[bufferSize/4];
        int i;
        String[] s = new String[1];

        int k = 0;
        if(MODE==0)
        {
            recorder.startRecording();
            isRecording = true;
            recorder.read(buf, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf2, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf3, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf4, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf5, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf6, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.stop();
            float[] finalBuffer=new float[(bufferSize/4) * 6];
            int len_buff=(bufferSize/4) * 6;
            int p=bufferSize/4;
            for(k=0;k<len_buff;k++)
            {
                if(k<p)
                    finalBuffer[k]=buf[k];
                if(k>=p && k<2*p)
                    finalBuffer[k]=buf2[k-p];
                if(k>=2*p && k<3*p)
                    finalBuffer[k]=buf3[k-2*p];
                if(k>=3*p && k<4*p)
                    finalBuffer[k]=buf4[k-3*p];
                if(k>=4*p && k<5*p)
                    finalBuffer[k]=buf5[k-(2*len_buff)/3];
                if(k>=5*p && k<6*p)
                    finalBuffer[k]=buf6[k-5*p];


            }
            bufferSize*=6;
            buf=finalBuffer;
        }
        else
        {
            recorder.startRecording();
            isRecording = true;
            recorder.read(buf, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf2, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf3, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf4, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf5, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf6, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf7, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf8, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf9, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.read(buf10, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
            recorder.stop();
            float[] finalBuffer=new float[(bufferSize/4) * 10];
            int len_buff=(bufferSize/4) * 10;
            int p=bufferSize/4;
            for(k=0;k<len_buff;k++)
            {
                if(k<p)
                    finalBuffer[k]=buf[k];
                if(k>=p && k<2*p)
                    finalBuffer[k]=buf2[k-p];
                if(k>=2*p && k<3*p)
                    finalBuffer[k]=buf3[k-2*p];
                if(k>=3*p && k<4*p)
                    finalBuffer[k]=buf4[k-3*p];
                if(k>=4*p && k<5*p)
                    finalBuffer[k]=buf5[k-4*p];
                if(k>=5*p && k<6*p)
                    finalBuffer[k]=buf6[k-5*p];
                if(k>=6*p && k<7*p)
                    finalBuffer[k]=buf3[k-6*p];
                if(k>=7*p && k<8*p)
                    finalBuffer[k]=buf4[k-7*p];
                if(k>=8*p && k<9*p)
                    finalBuffer[k]=buf5[k-8*p];
                if(k>=9*p )
                    finalBuffer[k]=buf6[k-9*p];


            }
            bufferSize*=10;
            buf=finalBuffer;
        }

        int start=0;
        int end;
        int j;
        end = bufferSize / 4;
        for (j = 0; j < bufferSize / 4; j++) {
            float c = buf[j];
            if (c < 0)
                c = -c;
            if (c > thre) {
                start = j;
                break;
            }
        }

        int start2 =start;
        end = start + samplerate * 4;//clip the 4 sec part
        //String temp2=Float.toString(start);
        //t.setText(temp2);
        int len = bufferSize / 4;
        float max = 0;
        for (j = start; j < end && j < len; j++) {
            float c = buf[j];
            if (c < 0)
                c = -c;
            if (c > max)
                max = c;
        }
        thre2=max/5;
        float thre3 = max/20;
        int flag = 1;
        float[] s1 = new float[100000];
        int[] s3=new int[100000];
        int ind=0;
        int index1 = 0;
        int index2 = 0;
        int[] s2 = new int[100000];
        int[] last = new int[100000];
        int index3 = 0;
        int count2 = 0;
        for (j = start; j < end && j < len; j += 1000) {
            float m = 0;
            float temp;
            int z;
            for (z = j; z < j + 1000; z++) {
                if (z < end && z < len) {
                    temp = buf[z];
                    if (temp < 0)
                        temp = -temp;
                    if (temp > m)
                        m = temp;
                }
            }
            if (m > thre2 && flag == 1) {
                flag = 0;
                float j2 = (float) (j - start);
                s1[index1++] = j2 / samplerate;
                s2[index2++] = count2;
                count2 = 0;
            } else {
                last[index3++] = count2;
                if (m < thre3)
                {
                    flag = 1;
                    s3[ind]=j;
                    ind++;
                }
                else
                    count2++;
            }

        }
        //String temp=Float.toString(s1[0]);
        //t.setText(temp);

        for (j = 0; j < index2 - 1; j++) {
            s2[j] = s2[j + 1];
        }
        s2[index2 - 1] = last[index3 - 1];
        int max2 = 0;
        int max3 = 0;
        for (j = 1; j < index2; j++) {
            if (s2[j] > max3)
            {
                max2 = j;
                max3=s2[j];
            }
        }
        s1[1] = s1[max2];
        if(max2>ind-1)
            s3[1]=end;
        else
            s3[1]=s3[max2];
        int tempo_usr=(int)(TEMPO_CONSTANT/s1[1])*TEMPO;
        String tempo_usr2=String.valueOf(tempo_usr);






        //t.setText(temp);
        return 0;

    }

    public   void threshold(View v) {

        mp[0][0].start();
        int bufferSize = AudioRecord.getMinBufferSize(samplerate, channels, encoding);
        bufferSize = samplerate * 4 * 2;
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, samplerate, channels, encoding, bufferSize);
        float[] buf = new float[bufferSize / 4];
        float[] buf2 = new float[bufferSize / 4];
        int i;
        String[] s = new String[1];


       // t.setText(yf);

        int k = 0;
        byte count = 0;
        recorder.startRecording();
        isRecording = true;

        recorder.read(buf, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
        recorder.read(buf2, 0, bufferSize / 4, AudioRecord.READ_BLOCKING);
        recorder.stop();
        float[] final_buff = new float[bufferSize / 2];
        for (int l = 0; l < bufferSize / 2; l++)
        {
            if(l<bufferSize/4)
                final_buff[l]=buf[l];
            else
                final_buff[l]=buf2[l-bufferSize/4];
        }
        bufferSize*=2;
        buf=final_buff;
        float average=0;
        for(int j=0;j<bufferSize/4;j++){
            if (buf[j]<0){
                buf[j]=-buf[j];
                average+=buf[j];
            }
            //

        }
        float div=(float)bufferSize/4;
        average=average/(div);
        thre=average*5;
        String temp=Float.toString(thre);


    }

}

class ffts{
    public double[][]  fft(double[][] samples,int len){
        if(len==1)
            return samples;
        int N=len;
        int M=N/2;
        int i;
        double[][] xodd=new double[1024][];
        int index1=0;
        double[][] xeven=new double[1024][];
        int index2=0;
        for(i=0;i<N;i++)
        {
            if(i%2==0)
            {
                xeven[index1++]=samples[i];
            }
            else
                xodd[index2++]=samples[i];

        }
        double[][] feven;
        feven=fft(xeven,M);
        double[][] fodd;
        fodd=fft(xodd,M);
        index1=0;
        double[][] freqbins=new double[1024][];
        double[][] temp2=new double[1024][];
        for(i=0;i<M;i++)
        {
            double[] temp=new double[2];
            double N_2=(double)N;
            double i_2=(double)i;
            double angle=(2*Math.PI*i_2)/N_2;
            double real_odd=fodd[i][0]*Math.cos(angle) + fodd[i][1]*Math.sin(angle);
            //System.out.println(real_odd);
            double im_odd =fodd[i][1]*Math.cos(angle)-fodd[i][0]*Math.sin(angle);
            temp[0]=real_odd + feven[i][0];
            temp[1]=im_odd + feven[i][1];
            //reusing index1
            freqbins[i]=temp;
            double[] temp3=new double[2];
            temp3[0]= feven[i][0]-real_odd;
            temp3[1]=feven[i][1] -im_odd;
            temp2[i]=temp3;

        }
        for(i=0;i<M;i++)
        {
            freqbins[i+M]=temp2[i];
        }
        return freqbins;

    }
}