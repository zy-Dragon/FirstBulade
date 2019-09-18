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
		// 要操作节点的路径
		String path = "/animal/cat";
		// 获取当前节点的值
		byte[] resultByteArray = zooKeeper.getData(path, false, new Stat());
		// 将字节数组封装为字符串
		String result = new String(resultByteArray);
		// 打印旧值
		System.out.println("old value=" + result);
		// 获取新值字符串所对应的数组
		byte[] newValueArray = new String("mimi").getBytes();
		// 指定当前操作所基于的版本号,如果不确定可以使用-1
		int version = 0;
		// 执行节点值得修改
		Stat stat = zooKeeper.setData(path, newValueArray, version);
		// 获取最新的版本号
		int newVersion = stat.getVersion();
		System.out.println("newVersion=" + newVersion);

		// 获取最新的值
		resultByteArray = zooKeeper.getData(path, false, new Stat());
		result = new String(resultByteArray);
		System.out.println("new value=" + result);

	}

	@Test
	public void testNoticOnce() throws KeeperException, InterruptedException {
		// 要操作节点的路径
		String path = "/animal/cat";

		Watcher watcher = new Watcher() {

			@Override
			// 当前watcher检测到节点值的修改,会调用process()方法
			public void process(WatchedEvent arg0) {
				System.out.println("接收到了通知,值发生了修改!!!");

			}
		};

		byte[] oldValue = zooKeeper.getData(path, watcher, new Stat());
		System.out.println("old value = " + new String(oldValue));
		while (true) {
			Thread.sleep(5000);
			System.out.println("当前方法原本要执行的业务逻辑");
		}

	}

	@Test
	public void testNoticForver() throws KeeperException, InterruptedException {
		// 要操作节点的路径
		String path = "/animal/cat";
		getDataWithNotic(zooKeeper, path);
		while (true) {
			Thread.sleep(5000);
			System.out.println("当前方法原本要执行的业务逻辑");
		}

	}

	public void getDataWithNotic(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {

		byte[] resultByteArry = zooKeeper.getData(path, new Watcher() {

			@Override
			public void process(WatchedEvent arg0) {
				System.out.println("接收到了通知,值发生了修改!!!");

				// 以类似递归的方式调用getDataWithNotic方法实现持续监控
				try {
					getDataWithNotic(zooKeeper, path);
				} catch (KeeperException | InterruptedException e) {
					e.printStackTrace();
				}

			}
		}, new Stat());

		String result = new String(resultByteArry);
		System.out.println("当前节点值=" + result);
	}

}
