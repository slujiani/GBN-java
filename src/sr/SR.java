package sr;

import gbn.GBNServer;
import gbn.GBNClient;
public class SR {
	public static final int WindowSize = 3;//���ó�ʼ���ڴ�С
    public static final int Final = 50;//�ж��Ƿ����㴰�ڴ�СӦ�����������
    public static final int SEGMENTS =20;//���ñ��Ķ������ݰ��ĸ���
    public static final int Port = 7777;//�趨�˿ں�
    public static Boolean flag =true;//�����ж��ط������ݵ�ʱ�򣬼�ʹ���յ��µ�ACK����Ȼ�ȴ�ԭ������ݷ������
    public static void main(String[] args){
    	GBNClient client = new GBNClient();
    	client.start();
        GBNServer server = new GBNServer();
        server.start();
    }
}