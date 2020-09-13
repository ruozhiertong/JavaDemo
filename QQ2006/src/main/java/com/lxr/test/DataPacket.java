package com.lxr.test;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 
习惯了TCP编程，认为UDP可以包办这些问题是错误的。一个UDP应用程序要承担可靠性方面的全部工作，包括报文的丢失、重复、时延、乱序以及连接失效等问题。

通常我们在可靠性好，传输时延小的局域网上开发测试，一些问题不容易暴露，但在大型互联网上却会出现错误。

UDP协议把递送的可靠性责任推到了上层即应用层，下面简单编写了几个类来专门处理两个问题：乱序和丢包。

四个类：DataPacket 类，PacketHeader类，PacketBody类 ，DataEntry类，位于同一个文件DataPacket .java中。

DataPacket 类相当于一个门面模式，提供给外部使用，通信数据也在这个类中处理。


解决乱序。 
对于丢包，并不是很友好，只是用最近的帧替代。 后续可能要有TODO，让丢失的帧重发。


如果要我自己设计：可以像TCP那样，有个应答机制。当收到应答，再发下一个数据。 
这样可以解决乱序，丢包(如果超过规定时间，就视为丢包，重发)。 如果过了很久才返回？以最先收到的为有效。

 * @author rzet
 *
 * 2020年9月11日
 */

public class DataPacket {
	InputStream is;
	OutputStream os;
	PacketHeader header;
	PacketBody body;
	ArrayList al;
	public static final int DataSwapSize = 64532;

	/**
	 * 在接收数据报使用
	 */
	public DataPacket() {
		header = new PacketHeader();
		body = new PacketBody();
		al = new ArrayList();
	}

	/**
	 * 在发送数据报时使用,它调用报文分割操作.
	 * 
	 * @param file String 硬盘文件
	 */
	public DataPacket(String file) {
		this();
		try {
			is = new FileInputStream(file);
			header.CalcHeaderInfo(is.available());
			this.madeBody();
			is.close();
//this.Gereratedata();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex1) {
			ex1.printStackTrace();
		}
	}

	/**
	 * 在发送数据报时使用,它调用报文分割操作.
	 * 
	 * @param url URL url地址
	 */
	public DataPacket(URL url) {
		this();
		try {
//is = url.openStream();
			URLConnection conn = url.openConnection();
			is = conn.getInputStream();
			int total = conn.getContentLength();
			header.CalcHeaderInfo(total);
			this.madeBody();
//System.out.println(total+":"+total);
			is.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 为发送构造分组,使用PackageHeader处理了报头格式,并为分组编序号.
	 */
	private void madeBody() {
		al.clear();
		byte[] buffer;
		DataEntry de;
		for (int i = 0; i < header.fragmentcounter; i++) {
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
//is.skip(i * body.BODY_BUFFER_SIZE);
				header.ArrageSort(i);
				de = new DataEntry(PacketBody.BODY_BUFFER_SIZE);
				de.setSn(i);
				de.setStreamsize(header.getStreamsize());
				de.setFragmentcounter(header.getFragmentcounter());
				if (header.isWTailFragment(i)) {
					buffer = new byte[header.getMinfragment()];
					is.read(buffer, 0, buffer.length);
					header.setActByteSize(header.getMinfragment());
					de.setActByteSize(header.getMinfragment());
				} else {
					buffer = new byte[body.BODY_BUFFER_SIZE];
					is.read(buffer, 0, buffer.length);
				}
//System.out.println("length-------"+i+" "+body.getBody().length+" "+header.getMinfragment());
				body.setBody(buffer);
//System.out.println("length:" + i + " " + header.toString());
				bos.write(header.getByte(), 0, header.HEADER_BUFFER_SIZE);
				bos.write(body.getBody(), 0, body.getBody().length);
				de.setBytes(bos.toByteArray());
				al.add(de);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 为发送构造分组,没有考虑报头格式,也没有为分组编序号.
	 */
	private void madeBody1() {
		al.clear();
		for (int i = 0; i < header.fragmentcounter; i++) {
			try {
				if (header.isWTailFragment(i))
					is.read(body.getBody(), i * body.BODY_BUFFER_SIZE, header.getMinfragment());
				else
					is.read(body.getBody(), i * body.BODY_BUFFER_SIZE, body.BODY_BUFFER_SIZE);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bos.write(header.getByte(), 0, header.HEADER_BUFFER_SIZE);
				bos.write(body.getBody(), header.HEADER_BUFFER_SIZE, body.getBody().length);
				al.add(bos);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * 在接收到报文后,对此报文执行组装,并处理报文丢失和乱序情况.
	 * 
	 * @param b1 byte[]
	 */
	public void Add(byte[] b1) {
		byte[] buffer = (byte[]) b1.clone();
		handlerText(buffer);
		DataEntry de = new DataEntry(buffer, header.getActByteSize());
		de.setSn(header.getSn());
		de.setStreamsize(header.getStreamsize());
		de.setFragmentcounter(header.getFragmentcounter());
		al.add(de);
	}

	private void handlerText(byte[] buffer) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(buffer, 0, header.HEADER_BUFFER_SIZE);
		byte[] b = new byte[header.HEADER_BUFFER_SIZE];
		System.arraycopy(buffer, 0, b, 0, b.length);
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		InputStreamReader isr = new InputStreamReader(bais);
		BufferedReader br = new BufferedReader(isr);
		try {
			header = new PacketHeader(br.readLine());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private String calFileSize(int size) {
		return size / 1024 + "K";
	}

	public ArrayList getDataPackets() {
		return al;
	}

	/**
	 * 是否接收完毕,通过序号是否等于最大段数来判断,这也许有问题,比如,正好是最后一个段丢失了,这样 这个包整个就丢失了.
	 * 
	 * @return
	 */
	public boolean isFull() {
		return this.header.getSn() == this.header.getFragmentcounter() - 1 ? true : false;
	}

	/**
	 * 判断是否只有一个段.
	 * 
	 * @return
	 */
	public boolean isZero() {
		return this.header.getSn() == 0 ? true : false;
	}

	/**
	 * 该函数执行报文组装,不考虑丢失的报文.
	 * 
	 * @return
	 */
	private ByteArrayOutputStream fetchDataPackets() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = null;
		DataEntry de;
		for (int i = 0; i < al.size(); i++) {
			try {
				de = this.getSnData(i);
				buffer = de.getByte();
				if (header.getStreamsize() == de.getStreamsize()) {
					bos.write(de.getByte(), header.HEADER_BUFFER_SIZE, de.getActByteSize());
					System.out.println(de.toString() + " -- fetchDataPackets");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return bos;
	}

	/**
	 * 该函数执行报文组装,对于丢失的报文,写入空报文.
	 * 
	 * @return ByteArrayOutputStream
	 */
	private ByteArrayOutputStream fetchDataPackets_sn() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer;
		DataEntry de;
		for (int i = 0; i < header.getFragmentcounter(); i++) {
			try {
				de = this.getSnData(i);
				if (de == null) {
					de = seachDeData(i);
				}
				buffer = de.getByte();
//System.out.println(de.getSn() + ":" + i);
//handlerText(buffer);
//bos.write(buffer, header.HEADER_BUFFER_SIZE,
//          buffer.length - header.HEADER_BUFFER_SIZE);
				if (header.getStreamsize() == de.getStreamsize()) {
					bos.write(de.getByte(), header.HEADER_BUFFER_SIZE, de.getActByteSize());
//System.out.println(de.toString());
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return bos;
	}

	/**
	 * 对缓冲的数据包进行排序处理,即按顺序提取同一帧的数据,如果没有找到该序号的帧,则返回空值.
	 * 
	 * @param sn int 要找的帧序号.
	 * @return DataEntry
	 */
	private DataEntry getSnData(int sn) {
		DataEntry de = null;
		for (int i = 0; i < al.size(); i++) {
			de = (DataEntry) al.get(i);
			if (header.getStreamsize() == de.getStreamsize()) {
				if (sn == de.getSn())
					break;
				else
					de = null;
			}
		}
		return de;
	}

	/**
	 * 按序号开始向前或者是向后寻找最近的帧片段,日后可以增加请求重发功能,通过开一个通信连接.
	 * 
	 * @param sn int
	 * @return DataEntry
	 */
	private DataEntry seachDeData(int sn) {
		DataEntry de = null;
		int initvalue, minvalue = 10000;
		DataEntry back, fore = null;
		for (int i = 0; i < al.size(); i++) {
			de = (DataEntry) al.get(i);
			if (header.getStreamsize() == de.getStreamsize()) {
				initvalue = Math.abs(de.getSn() - sn);
				if (de.getFragmentcounter() != de.getSn() && initvalue < minvalue) {
					minvalue = initvalue;
					fore = de;
				}
			}
		}
		return fore;
	}

	/**
	 * 除去最后一帧外,随机抽取一帧.
	 * 
	 * @return DataEntry
	 */
	private DataEntry seachDeData() {
		DataEntry de = null;
		for (int i = 0; i < al.size(); i++) {
			de = (DataEntry) al.get(i);
			System.out.println("sky ::::" + de.getFragmentcounter() + ":" + de.getSn() + ":" + i);
			if (header.getStreamsize() == de.getStreamsize()) {
				if (de.getFragmentcounter() != de.getSn()) {
					break;
				}
			}
		}
		return de;
	}

	/**
	 * 生成组装完的结果数据.因为用图像来做测试,所以令其返回图像.
	 * 
	 * @return Image
	 */
	public java.awt.Image Gereratedata() {
		ByteArrayInputStream bis;
		java.awt.image.BufferedImage bimage = null;
		try {
			byte[] b = fetchDataPackets_sn().toByteArray();
//fetchDataPackets_old1()
			bis = new ByteArrayInputStream(b);
			bimage = javax.imageio.ImageIO.read(bis);

		} catch (Exception ex1) {
			ex1.printStackTrace();
		}
		return bimage;
	}

	public static void main(String args[]) {
		DataPacket dp = new DataPacket("111-222.txt");
	}
}

/**
 * 数据实体,充当临时处理场所.
 * 
 * @author Administrator
 *
 */
class DataEntry {
	byte[] bytes;
	int fragmentcounter, sn, actbytesize;
	long streamsize;
	int minfragment;

	public DataEntry() {

	}

	public DataEntry(int size) {
		this.actbytesize = size;
	}

	public DataEntry(byte[] b, int i) {
		this.bytes = b;
		this.actbytesize = i;
	}

	public byte[] getByte() {
		return this.bytes;
	}

	public void setBytes(byte[] b) {
		this.bytes = b;
	}

	public void setStreamsize(long size) {
		this.streamsize = size;
	}

	public long getStreamsize() {
		return this.streamsize;
	}

	public int getMinfragment() {
		return minfragment;
	}

	public synchronized void setSn(int i) {
		this.sn = i;
	}

	public synchronized int getSn() {
		return sn;
	}

	public synchronized int getFragmentcounter() {
		return fragmentcounter;
	}

	public synchronized void setFragmentcounter(int c) {
		this.fragmentcounter = c;
	}

	public void setActByteSize(int size) {
		actbytesize = size;
	}

	public int getActByteSize() {
		return actbytesize;
	}

	public String toString() {
		return this.streamsize + "::" + this.fragmentcounter + "::" + this.sn + "::" + this.actbytesize
				+ " recv DataEntry";
	}
}

/**
 * 报头,处理报头格式
 * 
 * @author Administrator
 *
 */
class PacketHeader implements Serializable {
	public static final int HEADER_BUFFER_SIZE = 1024;
	int fragmentcounter, sn;
	int actbytesize = PacketBody.BODY_BUFFER_SIZE;
	byte[] header; // = new byte[HEADER_BUFFER_SIZE];
	long streamsize;
	int minfragment;

	public PacketHeader() {

	}

	public PacketHeader(long l) {
		this.setStreamsize(l);

	}

	public PacketHeader(String s) {
		String[] tm = s.split("::");
		this.setActByteSize(Integer.parseInt(tm[3]));
		this.setSn(Integer.parseInt(tm[2]));
		this.setFragmentcounter(Integer.parseInt(tm[1]));
		this.setStreamsize(Long.parseLong(tm[0]));
	}

	/**
	 * 根据文件的段的顺序生成数据头.
	 * 
	 * @param sn 文件序列
	 */
	public void ArrageSort(int sn) {
		this.setSn(sn);
		this.setByte();
	}

	public void CalcHeaderInfo(long l) {
		this.setStreamsize(l);
		CalcHeaderInfo();
	}

	/**
	 * 计算流要被分成的片段数量,并得出最小片段余量.
	 */
	public void CalcHeaderInfo() {
		fragmentcounter = Math.round((float) streamsize / PacketBody.BODY_BUFFER_SIZE);
		float critical = (float) streamsize / PacketBody.BODY_BUFFER_SIZE;
		if (critical - fragmentcounter < 0.5 && critical - fragmentcounter > 0)
			fragmentcounter++;
		minfragment = (int) (streamsize % PacketBody.BODY_BUFFER_SIZE);
	}

	public byte[] getHeader() {
		Long it = new Long(this.streamsize);
		return new byte[] { it.byteValue() };
	}

	public byte[] getByte() {
		return header; // this.toString().getBytes();
	}

	/**
	 * 生成报头字节,首先取得数据包头 流尺寸::段片数::段顺序::段实际尺寸 的字节形式,
	 * 然后加入回车换行符号,对于1024字节中剩余的部分一律写入元素为0的字节数组.
	 */
	public void setByte() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = this.toByte();
		try {
			bos.write(buffer);
			bos.write("\r\n".getBytes());
			bos.write(new byte[PacketHeader.HEADER_BUFFER_SIZE - buffer.length], 0,
					PacketHeader.HEADER_BUFFER_SIZE - buffer.length);
			header = bos.toByteArray();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void setStreamsize(long size) {
		this.streamsize = size;
	}

	public long getStreamsize() {
		return this.streamsize;
	}

	public int getMinfragment() {
		return minfragment;
	}

	public synchronized void setSn(int i) {
		this.sn = i;
	}

	public int getSn() {
		return sn;
	}

	public int getFragmentcounter() {
		return fragmentcounter;
	}

	public synchronized void setFragmentcounter(int c) {
		this.fragmentcounter = c;
	}

	public void setActByteSize(int size) {
		actbytesize = size;
		setByte();
	}

	public int getActByteSize() {
		return actbytesize;
	}

	/**
	 * 数据包头的格式为:流尺寸::段片数::段顺序::段实际尺寸
	 * 报头字节长度是可变化的,比如,可以加入流的具体信息如:流所属文件的名称,文件类型以及一些其他信息.
	 * 
	 * @return String
	 */
	public String toString() {
		return streamsize + "::" + this.fragmentcounter + "::" + this.getSn() + "::" + this.getActByteSize();
	}

	public byte[] toByte() {
		return this.toString().getBytes();
	}

	/**
	 * 是否为尾段
	 * 
	 * @param i int
	 * @return boolean
	 */
	public boolean isWTailFragment(int i) {
		return (i == fragmentcounter - 1) ? true : false;
	}

}

/**
 * 用户数据区
 * 
 * @author Administrator
 *
 */
class PacketBody implements Serializable {
	public static final int BODY_BUFFER_SIZE = 63508; // 65508
	byte[] body;

	public PacketBody() {
	}

	public void setBody(byte[] b) {
		this.body = b;
	}

	public byte[] getBody() {
		return body;
	}
}
