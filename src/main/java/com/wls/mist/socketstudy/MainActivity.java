package com.wls.mist.socketstudy;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.wls.mist.socketstudy.tool.ThreadPoolTool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @InjectView(R.id.input_ip_edittext)
    EditText inputIpEdittext;
    @InjectView(R.id.connect_main_btn)
    Button connectMainBtn;
    @InjectView(R.id.input_content_edittext)
    EditText inputContentEdittext;
    @InjectView(R.id.send_main_btn)
    Button sendMainBtn;
    @InjectView(R.id.listview_socket)
    ListView listView;
    private final int port = 12345;//端口号
    private List<String> list = new ArrayList<>();
    private ArrayAdapter adapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        listView.setAdapter(adapter);

    }

    @OnClick({R.id.connect_main_btn, R.id.send_main_btn})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.connect_main_btn:
                if (TextUtils.isEmpty(inputIpEdittext.getText())){
                    Toast.makeText(this, "ip不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //获取用户输入的ip
                String host = inputIpEdittext.getText().toString().trim();
                //启动线程
                ThreadPoolTool.getInstance().execute(new MyThread(host));

                break;
            case R.id.send_main_btn:
                //发送消息
                if (TextUtils.isEmpty(inputContentEdittext.getText())){
                    Toast.makeText(this, "消息内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //将数据添加到list集合中
                list.add(inputContentEdittext.getText().toString());

                ThreadPoolTool.getInstance().execute
                       (new MySendMess6(inputContentEdittext.getText().toString()));

//                ThreadPoolTool.getInstance().execute
//                        (new MySendMess5(inputContentEdittext.getText().toString()));
                break;
        }
    }



    Socket socket;
    private class MyThread implements Runnable {
        String host;

        MyThread(String host) {
            this.host = host;
        }

        @Override
        public void run() {
            try {
                //实例化socket对象
                 socket = new Socket(host, port);
                //判断是否已连接
                boolean boo = socket.isConnected();
                if (boo) {
                    Log.e("TAG", "连接成功");
                    //获取输出流
//                    OutputStream outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();
                    //缓冲区字符输出流
//                    BufferedWriter bufferedWriter =
//                            new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                    BufferedReader bufferedReader =
                            new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
                    //定义发送的内容
//                    String content = "hello";
                    //写入数据
//                    bufferedWriter.write(content);
                    //换行表示数据的写入结束，即服务端读取结束的标识
//                    bufferedWriter.write("\n");
                    //刷新，强制提交数据
//                    bufferedWriter.flush();
                    //接受服务端的数据
//                    String line = bufferedReader.readLine();
                    String line = null;
                    while((line = bufferedReader.readLine())!=null) {
                        Log.e("TAG", line);
                        list.add(line);
                        handler.sendEmptyMessage(0x123);
                    }

                } else {
                    Log.e("TAG", "连接失败");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //处理消息的发送
    private class MySendMess6 implements Runnable{
        String mess;
        MySendMess6(String mess){
            this.mess = mess;
        }
        @Override
        public void run() {
            try {
                //获取输出流
                OutputStream outputStream = socket.getOutputStream();
                //缓冲区字符输出流
                BufferedWriter bufferedWriter =
                        new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                bufferedWriter.write(mess);
                bufferedWriter.write("\n");
                bufferedWriter.flush();
                handler.sendEmptyMessage(0x123);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }





    //处理消息的发送与接收
    private class MySendMess5 implements Runnable{

        String mess;
        MySendMess5(String mess){
            this.mess = mess;
        }
        @Override
        public void run() {
            if (socket == null) return;
            try {
                //获取输出流
                OutputStream outputStream = socket.getOutputStream();
                //缓冲区字符输出流
                BufferedWriter bufferedWriter =
                        new BufferedWriter(new OutputStreamWriter(outputStream, "utf-8"));
                bufferedWriter.write(mess);
                bufferedWriter.write("\n");
                bufferedWriter.flush();
                handler.sendEmptyMessage(0x123);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    };


}
