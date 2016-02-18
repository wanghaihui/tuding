package com.xiaobukuaipao.vzhi.im;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import android.util.Log;

public class LoginServerHandler extends SimpleChannelHandler {
	public static final String TAG = LoginServerHandler.class.getSimpleName();
	
	private boolean connected = false;
	
	@Override
	public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		super.channelConnected(ctx, e);
		connected = true;
		// 通道建立连接
		Log.i(TAG, "Channel Connected");
		ImLoginManager.getInstance().onLoginServerConnected();
	}

	@Override
	public void channelDisconnected(ChannelHandlerContext ctx,
			ChannelStateEvent e) throws Exception {
		super.channelDisconnected(ctx, e);
		connected = false;
		// 通道断开连接
		Log.i(TAG, "Channel channelDisconnected");
		ImLoginManager.getInstance().onLoginServerDisconnected();
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		super.messageReceived(ctx, e);
		// 接收到返回消息
		Log.i(TAG, e.getMessage().toString());
		ImMessageDispatcher.dispatch(e.getMessage().toString());
	}
	
	
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		super.exceptionCaught(ctx, e);
		
		if (!connected) {
			channelUnconnected();
		}
	}

	private void channelUnconnected() {
		Log.i(TAG, "Channel channelUnconnected");
		// 通道没连接
    	ImLoginManager.getInstance().onLoginServerUnconnected();
	}
}
