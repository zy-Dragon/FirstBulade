package com.sz.lzy.test;

import java.io.IOException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class ZkTest {

	private ZooKeeper zooKeeper;

	{
		String connectString = "10.1.14.53:2181";
		int sessionTimeout = 50000;
		Watcher watcher = new Watcher() {
			@Override
			public void process(WatchedEvent arg0) {
			};
		};
		try {
			zooKeeper = new ZooKeeper(connectString, sessionTimeout, watcher);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void testUpdateNodeData() throws KeeperException, InterruptedException {
		// Ҫ�����ڵ��·��
		String path = "/animal/cat";
		// ��ȡ��ǰ�ڵ��ֵ
		byte[] resultByteArray = zooKeeper.getData(path, false, new Stat());
		// ���ֽ������װΪ�ַ���
		String result = new String(resultByteArray);
		// ��ӡ��ֵ
		System.out.println("old value=" + result);
		// ��ȡ��ֵ�ַ�������Ӧ������
		byte[] newValueArray = new String("mimi").getBytes();
		// ָ����ǰ���������ڵİ汾��,�����ȷ������ʹ��-1
		int version = 0;
		// ִ�нڵ�ֵ���޸�
		Stat stat = zooKeeper.setData(path, newValueArray, version);
		// ��ȡ���µİ汾��
		int newVersion = stat.getVersion();
		System.out.println("newVersion=" + newVersion);

		// ��ȡ���µ�ֵ
		resultByteArray = zooKeeper.getData(path, false, new Stat());
		result = new String(resultByteArray);
		System.out.println("new value=" + result);

	}

	@Test
	public void testNoticOnce() throws KeeperException, InterruptedException {
		// Ҫ�����ڵ��·��
		String path = "/animal/cat";

		Watcher watcher = new Watcher() {

			@Override
			// ��ǰwatcher��⵽�ڵ�ֵ���޸�,�����process()����
			public void process(WatchedEvent arg0) {
				System.out.println("���յ���֪ͨ,ֵ�������޸�!!!");

			}
		};

		byte[] oldValue = zooKeeper.getData(path, watcher, new Stat());
		System.out.println("old value = " + new String(oldValue));
		while (true) {
			Thread.sleep(5000);
			System.out.println("��ǰ����ԭ��Ҫִ�е�ҵ���߼�");
		}

	}

	@Test
	public void testNoticForver() throws KeeperException, InterruptedException {
		// Ҫ�����ڵ��·��
		String path = "/animal/cat";
		getDataWithNotic(zooKeeper, path);
		while (true) {
			Thread.sleep(5000);
			System.out.println("��ǰ����ԭ��Ҫִ�е�ҵ���߼�");
		}

	}

	public void getDataWithNotic(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {

		byte[] resultByteArry = zooKeeper.getData(path, new Watcher() {

			@Override
			public void process(WatchedEvent arg0) {
				System.out.println("���յ���֪ͨ,ֵ�������޸�!!!");

				// �����Ƶݹ�ķ�ʽ����getDataWithNotic����ʵ�ֳ������
				try {
					getDataWithNotic(zooKeeper, path);
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}

			}
		}, new Stat());

		String result = new String(resultByteArry);
		System.out.println("��ǰ�ڵ�ֵ=" + result);
	}

}
