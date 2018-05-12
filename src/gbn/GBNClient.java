package gbn;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import sr.SR;
public class GBNClient extends Thread {
    private static DatagramPacket receivePacket;
    private static DatagramPacket SendACK;
    private static DatagramSocket Client;
    private static byte[] receive = new byte[1024];
    private static byte[] send = new byte[20];
    private static int last;
    private static int Segments;//�����ͱ��Ķ�
    public GBNClient(){
        last = -1;
        Segments = SR.SEGMENTS;
        try {
            Client = new DatagramSocket(SR.Port);
        }catch (SocketException e){
        		System.out.println("���շ�Socket�޷��򿪣�");
        }
    }
    @Override
    public void run(){
        while (true){
            receivePacket = new DatagramPacket(receive,receive.length);
            
            try{
                Client.receive(receivePacket);
            }catch (IOException e){
            		System.out.println("����ʧ�ܣ�");
            }
            int Sequences=-1 ;
            String seqStr=new String(receive);
            String[] seqStrArr=seqStr.split(" ");
           
            Sequences=Integer.parseInt(seqStrArr[0]);
            //ͨ���������ָ����������
            if(Math.random()<0.8){
                if (Sequences == last+1){
                    //���������ȷ������ACK���ģ������ظ���������
                    
                    send = new String("ACK"+Sequences+"END").getBytes();
                    
                    InetAddress inetAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    SendACK = new DatagramPacket(send,send.length,inetAddress,clientPort);
                    try{
                        Client.send(SendACK);
                        Segments--;
                        last++;
                    }catch (IOException e){
                    }
                }
                else if(Sequences >(last+1)){
                	 System.out.println((last+1)+"���ˣ��յ����ݱ�:"+Sequences);                    
                     send = new String("ACK"+(last)+"END").getBytes();
                     //System.out.println("ACK"+(last));
                    InetAddress inetAddress = receivePacket.getAddress();
                    int clientPort = receivePacket.getPort();
                    SendACK = new DatagramPacket(send,send.length,inetAddress,clientPort);
                    try{
                        Client.send(SendACK);
                    }catch (IOException e){
                    }
                }

            }else{
            	//����
            }
        }
    }
}