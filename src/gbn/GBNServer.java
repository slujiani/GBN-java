package gbn;
import javax.swing.Timer;
import sr.SR;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;

public class GBNServer extends Thread {

    private static int Final;
    private static int begin = 0, end;
    private static int Segments;
    private static int Remain;
    static Timer timer;
    private static InetAddress inetAddress;
    private static DatagramSocket ServerSocket;
    private byte[] receive = new byte[20];
    private byte[] send = new byte[1024];

    public GBNServer(){
        try {
            inetAddress = InetAddress.getByName("localhost");
        }catch (UnknownHostException e){
        }
        end = begin + SR.WindowSize -1;
        Segments = SR.SEGMENTS;
        Remain = Segments;//�������͵ı��ĵĸ���
        Final = SR.Final;
        try {
            ServerSocket = new DatagramSocket();
        }catch (SocketException e){
        }
        System.out.println("******************* �������˼�������" + Segments +"�����ݰ���0��"+(Segments - 1)+"����******************* ");
        //���ü�ʱ������ʱΪ3��
        //���ȷ��ʹ����С���������ݰ�
        timer = new Timer(3000,new DelayActionListener(ServerSocket,begin));
        timer.start();
        for (int i = begin; i <= end; i++){            
            send = (new String(i+" seq")).getBytes();
            DatagramPacket sendPacket = new DatagramPacket(send,send.length,inetAddress, SR.Port);
            try {
                ServerSocket.send(sendPacket);
                Remain--;
                System.out.println("**--> �������˷������ݰ���"+i);
            }catch (IOException e){
            }
        }
    }

    @Override
    public void run(){
        while (true){
            DatagramPacket receivePacket = new DatagramPacket(receive,receive.length);
            try{
                ServerSocket.receive(receivePacket);
                int ackNum = -1;
                String revStr=new String(receive);
                int tmp=revStr.indexOf("END");
                revStr=revStr.substring(3, tmp);
                ackNum=Integer.parseInt(revStr);
                //System.out.println("acknum:"+ackNum);
                if(ackNum<0)
                {
                	ackNum=-1;
                }
           	 System.out.println("**<-- ���յ�ACK��ţ�"+ackNum);
                if(ackNum == Segments -1){
                    System.out.println("******************* ������������ȫ��������ϣ�******************* ");
                    timer.stop();
                    return;
                }else if(ackNum >= begin && Remain > 0){
                	
                	int increase = (ackNum-begin+1);
                	for(int i=0;i<increase;i++)
                	{
                		//δ������ϣ�
                        timer.stop();
                        //�����ƶ�
                        
                        begin++;
                        end++;
                        if(begin>=Segments-1)
                        {
                        	begin=Segments-1;
                        }
                        if(end>=Segments-1)
                        {
                         end=Segments-1 ;
                        }
                        
                        send = (new String(end+" seq")).getBytes();
                        
                        while(SR.flag==false);
                        DatagramPacket sendPacket = new DatagramPacket(send,send.length,inetAddress,SR.Port);
                        try {
                            ServerSocket.send(sendPacket);
                            Remain--;
                            System.out.println("**--> �������˷������ݰ���"+end);
                	    }
                	
                  catch (IOException e){
                      }
                   }               	
                    //���ö�ʱ��
                    timer = new Timer(3000,new DelayActionListener(ServerSocket,begin));
                    timer.start();
                }
            }
           catch (IOException e){
            }
        }
    }
    
  //���ڼ�ʱ��������
    class DelayActionListener implements ActionListener{
        private DatagramSocket socket;
        private int seqNo;
        public DelayActionListener(DatagramSocket ServerSocket, int seqNo){
            this.socket = ServerSocket;
            this.seqNo = seqNo;
        }
        @Override
        public void actionPerformed(ActionEvent e){
        	SR.flag= false;
            GBNServer.timer.stop();
            GBNServer.timer = new Timer(3000,new DelayActionListener(socket,seqNo));
            GBNServer.timer.start();
            int end = seqNo+SR.WindowSize -1;
            System.out.println("!!--> ׼���ش����ݰ��� " + seqNo +"--" + end);
            for(int i = seqNo; i <= end; i++){
                byte[] sendData = null;
                InetAddress clientAddress = null;
                try {
                    clientAddress = InetAddress.getByName("localhost");
                    
                    sendData = (new String(i+" seq")).getBytes();
                   
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, SR.Port);
                    socket.send(sendPacket);
                    System.out.println("**--> �������˷������ݰ���" + i);
                } catch (Exception e1) {
                }
            }
            SR.flag= true;

        }
}
}