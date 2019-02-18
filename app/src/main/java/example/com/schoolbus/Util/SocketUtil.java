package example.com.schoolbus.Util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


import example.com.schoolbus.User;

public class SocketUtil {
    public static void insertUser(final String name, final int age, final SocketListener listener){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //给服务器发送信息
                try {
                    //1.建立客户端Socket连接，指定服务器的位置以及端口
                    Socket socket=new Socket("192.168.1.100",8002);
                    //2.得到Socket的读写流
                    OutputStream os=socket.getOutputStream();
                    //对象输出流
                    PrintWriter pw=new PrintWriter(os);
                    //添加注册用户
                    String str="r#"+name+"#"+age;
                     // pw.write("r#Tom#25");
                    pw.write(str);
                    pw.flush();
                    //输入流
                    InputStream is=socket.getInputStream();
                    InputStreamReader reader=new InputStreamReader(is,"GBK");
                    BufferedReader br=new BufferedReader(reader);
                    //3.利用流按照一定的协议对Socket进行读/写操作

                    //关闭输出流
                    socket.shutdownOutput();
                    //接受服务器的响应并打印显示
                    String reply=null;
                    while(!((reply=br.readLine())==null)){
                        Log.d("zhu","服务器的响应为："+reply);
                        analyzeRetrunData(reply,listener);

                    }
                    //4.关闭资源
                    br.close();
                    is.close();
                    pw.close();
                    os.close();
                    os.close();
                    socket.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();


    }
    public static void deleteUserByName(final String name, final SocketListener listener){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //给服务器发送信息
                try {
                    //1.建立客户端Socket连接，指定服务器的位置以及端口
                    Socket socket=new Socket("192.168.1.100",8002);
                    //2.得到Socket的读写流
                    OutputStream os=socket.getOutputStream();
                    //对象输出流

                    PrintWriter pw=new PrintWriter(os);


                    //删除指定姓名的用户
                    //pw.write("d#tom");
               String str="d#"+name;
               pw.write(str);

                    pw.flush();
                    //输入流
                    InputStream is=socket.getInputStream();
                    InputStreamReader reader=new InputStreamReader(is,"GBK");
                    BufferedReader br=new BufferedReader(reader);
                    //3.利用流按照一定的协议对Socket进行读/写操作

                    //关闭输出流
                    socket.shutdownOutput();
                    //接受服务器的响应并打印显示
                    String reply=null;
                    while(!((reply=br.readLine())==null)){
                        Log.d("zhu","服务器的响应为："+reply);
                        analyzeRetrunData(reply,listener);

                    }
                    //4.关闭资源
                    br.close();
                    is.close();
                    pw.close();
                    os.close();
                    os.close();
                    socket.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }
    public static void updateByName(final String name,final int age,final SocketListener listener){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //给服务器发送信息
                try {
                    //1.建立客户端Socket连接，指定服务器的位置以及端口
                    Socket socket=new Socket("192.168.1.100",8002);
                    //2.得到Socket的读写流
                    OutputStream os=socket.getOutputStream();
                    //对象输出流

                    PrintWriter pw=new PrintWriter(os);





                    //更新指定姓名的用户的年龄
                 //   pw.write("u#zhangsan#100");
                    String str="u#"+name+"#"+age;
                    pw.write(str);
                    pw.flush();
                    //输入流
                    InputStream is=socket.getInputStream();
                    InputStreamReader reader=new InputStreamReader(is,"GBK");
                    BufferedReader br=new BufferedReader(reader);
                    //3.利用流按照一定的协议对Socket进行读/写操作

                    //关闭输出流
                    socket.shutdownOutput();
                    //接受服务器的响应并打印显示
                    String reply=null;
                    while(!((reply=br.readLine())==null)){
                        Log.d("zhu","服务器的响应为："+reply);
                     analyzeRetrunData(reply,listener);

                    }
                    //4.关闭资源
                    br.close();
                    is.close();
                    pw.close();
                    os.close();
                    os.close();
                    socket.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }
    public  static ArrayList<User> queryAll(){
        final ArrayList<User> users=new ArrayList<>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //给服务器发送信息
                try {
                    //1.建立客户端Socket连接，指定服务器的位置以及端口
                    Socket socket=new Socket("192.168.1.100",8002);
                    //2.得到Socket的读写流
                    OutputStream os=socket.getOutputStream();
                    //对象输出流

                    PrintWriter pw=new PrintWriter(os);

                   // 查询所有用户的信息
                    pw.write("q#all");




                    pw.flush();
                    //输入流
                    InputStream is=socket.getInputStream();
                    InputStreamReader reader=new InputStreamReader(is,"GBK");
                    BufferedReader br=new BufferedReader(reader);
                    //3.利用流按照一定的协议对Socket进行读/写操作

                    //关闭输出流
                    socket.shutdownOutput();
                    //接受服务器的响应并打印显示
                    String reply=null;
                    while(!((reply=br.readLine())==null)){
                        Log.d("zhu","服务器的响应为："+reply);
                        String[] replyArray=reply.split("#");


                        if(replyArray[0].equals("q")){
                            //q 查询
                            if(replyArray[1].equals("t")){
                                Log.d("zhu"," query success");
                                int count=Integer.parseInt(replyArray[2]);

                                int j=3;
                                while (j!=(count*2+3)){
                                    String name=replyArray[j];
                                    int age=Integer.parseInt(replyArray[j+1]);
                                    j=j+2;
                                    User user=new User(name,age);
                                    users.add(user);
                                }
                                Log.d("zhu","用户数目："+users.size());
                                for(int i=0;i<users.size();i++){
                                    Log.d("zhu","用户"+i+"的姓名为"+users.get(i).getName()+",年龄为"+users.get(i).getAge()+"\n");
                                }
                            }else if(replyArray[1].equals("f")){
                                Log.d("zhu","query false");
                            }
                        }

                    }
                    //4.关闭资源
                    br.close();
                    is.close();
                    pw.close();
                    os.close();
                    os.close();
                    socket.close();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
        return users;
    }
    private  static void analyzeRetrunData(String reply ,SocketListener listener){
        //对从服务器返回字符串的处理，适用于添加用户，删除用户，更新用户
        String[] replyArray=reply.split("#");
        if(replyArray[0].equals("r")){
            //r 注册
            if(replyArray[1].equals("t")){
                Log.d("zhu","register success");
                listener.onSuccess();
            }else if(replyArray[1].equals("f")){
                Log.d("zhu","register false");
                listener.onFailed();
            }
        } if(replyArray[0].equals("d")){
            //d 删除
            if(replyArray[1].equals("t")){
                Log.d("zhu","delete success");
                listener.onSuccess();
            }else if(replyArray[1].equals("f")){
                Log.d("zhu","delete false");
                listener.onFailed();
            }
        }if(replyArray[0].equals("u")){
            //u 更新
            if(replyArray[1].equals("t")){
                Log.d("zhu","update success");
                listener.onSuccess();
            }else if(replyArray[1].equals("f")){
                Log.d("zhu","update false");
                listener.onFailed();
            }
        }
    }
    }

