package com.xiaobukuaipao.vzhi.im;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder;
import org.jboss.netty.handler.codec.frame.Delimiters;
import org.jboss.netty.handler.codec.string.StringDecoder;
import org.jboss.netty.handler.codec.string.StringEncoder;

import android.util.Log;

/**
 * Socket处理线程
 * @author xiaobu
 */
public class SocketThread extends Thread {
	
	public static final String TAG = SocketThread.class.getSimpleName();
	
	// 客户端启动器
	private ClientBootstrap clientBootstrap = null;
	// 通道工厂
	private ChannelFactory channelFactory = null;
	
	// 通道
	private Channel channel = null;
	private ChannelFuture channelFuture = null;
	
	private String ip;
	private int port;
	
	public SocketThread(String ip, int port, SimpleChannelHandler handler) {
		this.ip = ip;
		this.port = port;
		init(handler);
	}
	
	private void init(final SimpleChannelHandler handler) {
		// only one IO thread
		channelFactory = new NioClientSocketChannelFactory(Executors.newSingleThreadExecutor(), 
				Executors.newSingleThreadExecutor());
		
		clientBootstrap = new ClientBootstrap(channelFactory);
		// 超时时间--5秒钟
		clientBootstrap.setOption("connectTimeoutMillis", 5000);
		// Set up the pipeline factory--设置管道工厂
		clientBootstrap.setPipelineFactory(new ChannelPipelineFactory() {
			
			@Override
			public ChannelPipeline getPipeline() throws Exception {
				// 通道管道--设置编码器，解码器，具体的业务处理
				// 重点处理
				ChannelPipeline pipeline = Channels.pipeline();
				
				pipeline.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
				// 临时选择String编码
				pipeline.addLast("encoder", new StringEncoder());
				// 设置解码器
				pipeline.addLast("decoder", new StringDecoder());
				
				

				
				pipeline.addLast("logic", handler);
				return pipeline;
			}
			
		});
		
		clientBootstrap.setOption("tcpNoDelay", true);
		clientBootstrap.setOption("keepAlive", true);
	}

	/**
	 * 必须要重写的函数，Thread.start()就执行run函数
	 */
	@Override
	public void run() {
		doConnect();
	}
	
	/**
	 * 执行网络链接Socket操作
	 * @return
	 */
	public boolean doConnect() {
		Log.i(TAG, "doConnect");
		try {
			if ((null == channel || (null != channel && !channel.isConnected())) && null != this.ip && this.port > 0) {
				// 先生成一个ChannelFurture
				// Start the connection attempt
				channelFuture = clientBootstrap.connect(new InetSocketAddress(this.ip, this.port));
				// Wait until the connection attempt succeeds or fails
				channel = channelFuture.awaitUninterruptibly().getChannel();
				
				if (!channelFuture.isSuccess()) {
					// 打印失败原因
					channelFuture.getCause().printStackTrace();
					clientBootstrap.releaseExternalResources();
					
					Log.i(TAG, "connect is fail");
					
					return false;
				} else {
					Log.i(TAG, "connect is success");
				}
			}
			
			// Wait until the connection is closed or the connection attemp fails.
			channelFuture.getChannel().getCloseFuture().awaitUninterruptibly();
			
			// Shut down thread pools to exit.
			clientBootstrap.releaseExternalResources();
			
			return true;
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.i(TAG, "exception");
			return false;
		}
		
	}
	
	// 发送包--简单的String字符串，如果以后需要扩展，则重载或者重写
	public boolean sendPacket(String packet) {

		if (null != packet && null != channelFuture.getChannel()) {

			channelFuture.getChannel().write(packet);
			return true;
			
		} else {
			return false;
		}

	}
	
	// 关闭Socket
	public void close() {
		if (null == channelFuture) {
			return;
		}

		if (null != channelFuture.getChannel()) {
			channelFuture.getChannel().close();
		}
		
		channelFuture.cancel();
	}
	
}
