package com.example.grpc;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.example.user.GenericResponse;
import com.example.user.GetUserRequest;
import com.example.user.User;
import com.example.user.UserServiceGrpc;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

public class GrpcClient {
	private static final Logger logger = Logger.getLogger(GrpcClient.class.getName());

	private final ManagedChannel channel;
	private final UserServiceGrpc.UserServiceBlockingStub blockingStub;

	private static GrpcClient singleton;

	public static GrpcClient getSingleton() {
		if (singleton == null) {
			singleton = new GrpcClient("localhost", 50051);
		}
		return singleton;
	}

	public GrpcClient(String host, int port) {
		this(ManagedChannelBuilder.forAddress(host, port)
				// Channels are secure by default (via SSL/TLS). For the example we disable TLS
				// to avoid
				// needing certificates.
				.usePlaintext().build());
	}

	private GrpcClient(ManagedChannel channel) {
		this.channel = channel;
		blockingStub = UserServiceGrpc.newBlockingStub(channel);
	}

	public void shutdown() throws InterruptedException {
		channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
	}

	public GenericResponse upsert(User request, GenericResponse default_value) {
		try {
			return blockingStub.upsert(request);
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		}

		return default_value;
	}

	public User get(GetUserRequest request, User default_value) {

		try {
			return blockingStub.get(request);
		} catch (StatusRuntimeException e) {
			logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
		}

		return default_value;
	}

}
