package com.mongou.IDM;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * <pre>
 * fiecare rpc are un tip de request si un tip de response
 *request si response sunt mesaje (message)
 * in metoda din server se va folosi request-ul pentru a extrage datele necesare
 * la client se primeste tipul de response cu atributele necesare
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.15.0)",
    comments = "Source: idm_service.proto")
public final class IDMServiceGrpc {

  private IDMServiceGrpc() {}

  public static final String SERVICE_NAME = "IDMService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<IdmService.UserCreateRequest,
      IdmService.TokenResponse> getCreateUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateUser",
      requestType = IdmService.UserCreateRequest.class,
      responseType = IdmService.TokenResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<IdmService.UserCreateRequest,
      IdmService.TokenResponse> getCreateUserMethod() {
    io.grpc.MethodDescriptor<IdmService.UserCreateRequest, IdmService.TokenResponse> getCreateUserMethod;
    if ((getCreateUserMethod = IDMServiceGrpc.getCreateUserMethod) == null) {
      synchronized (IDMServiceGrpc.class) {
        if ((getCreateUserMethod = IDMServiceGrpc.getCreateUserMethod) == null) {
          IDMServiceGrpc.getCreateUserMethod = getCreateUserMethod = 
              io.grpc.MethodDescriptor.<IdmService.UserCreateRequest, IdmService.TokenResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "IDMService", "CreateUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.UserCreateRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.TokenResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IDMServiceMethodDescriptorSupplier("CreateUser"))
                  .build();
          }
        }
     }
     return getCreateUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<IdmService.UserLoginRequest,
      IdmService.TokenResponse> getLoginMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Login",
      requestType = IdmService.UserLoginRequest.class,
      responseType = IdmService.TokenResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<IdmService.UserLoginRequest,
      IdmService.TokenResponse> getLoginMethod() {
    io.grpc.MethodDescriptor<IdmService.UserLoginRequest, IdmService.TokenResponse> getLoginMethod;
    if ((getLoginMethod = IDMServiceGrpc.getLoginMethod) == null) {
      synchronized (IDMServiceGrpc.class) {
        if ((getLoginMethod = IDMServiceGrpc.getLoginMethod) == null) {
          IDMServiceGrpc.getLoginMethod = getLoginMethod = 
              io.grpc.MethodDescriptor.<IdmService.UserLoginRequest, IdmService.TokenResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "IDMService", "Login"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.UserLoginRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.TokenResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IDMServiceMethodDescriptorSupplier("Login"))
                  .build();
          }
        }
     }
     return getLoginMethod;
  }

  private static volatile io.grpc.MethodDescriptor<IdmService.TokenRequest,
      IdmService.StringResponse> getLogoutMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Logout",
      requestType = IdmService.TokenRequest.class,
      responseType = IdmService.StringResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<IdmService.TokenRequest,
      IdmService.StringResponse> getLogoutMethod() {
    io.grpc.MethodDescriptor<IdmService.TokenRequest, IdmService.StringResponse> getLogoutMethod;
    if ((getLogoutMethod = IDMServiceGrpc.getLogoutMethod) == null) {
      synchronized (IDMServiceGrpc.class) {
        if ((getLogoutMethod = IDMServiceGrpc.getLogoutMethod) == null) {
          IDMServiceGrpc.getLogoutMethod = getLogoutMethod = 
              io.grpc.MethodDescriptor.<IdmService.TokenRequest, IdmService.StringResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "IDMService", "Logout"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.TokenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.StringResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IDMServiceMethodDescriptorSupplier("Logout"))
                  .build();
          }
        }
     }
     return getLogoutMethod;
  }

  private static volatile io.grpc.MethodDescriptor<IdmService.TokenRequest,
      IdmService.TokenResponse> getAuthorizeMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Authorize",
      requestType = IdmService.TokenRequest.class,
      responseType = IdmService.TokenResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<IdmService.TokenRequest,
      IdmService.TokenResponse> getAuthorizeMethod() {
    io.grpc.MethodDescriptor<IdmService.TokenRequest, IdmService.TokenResponse> getAuthorizeMethod;
    if ((getAuthorizeMethod = IDMServiceGrpc.getAuthorizeMethod) == null) {
      synchronized (IDMServiceGrpc.class) {
        if ((getAuthorizeMethod = IDMServiceGrpc.getAuthorizeMethod) == null) {
          IDMServiceGrpc.getAuthorizeMethod = getAuthorizeMethod = 
              io.grpc.MethodDescriptor.<IdmService.TokenRequest, IdmService.TokenResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "IDMService", "Authorize"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.TokenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.TokenResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IDMServiceMethodDescriptorSupplier("Authorize"))
                  .build();
          }
        }
     }
     return getAuthorizeMethod;
  }

  private static volatile io.grpc.MethodDescriptor<IdmService.TokenRequest,
      IdmService.StringResponse> getDeleteUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteUser",
      requestType = IdmService.TokenRequest.class,
      responseType = IdmService.StringResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<IdmService.TokenRequest,
      IdmService.StringResponse> getDeleteUserMethod() {
    io.grpc.MethodDescriptor<IdmService.TokenRequest, IdmService.StringResponse> getDeleteUserMethod;
    if ((getDeleteUserMethod = IDMServiceGrpc.getDeleteUserMethod) == null) {
      synchronized (IDMServiceGrpc.class) {
        if ((getDeleteUserMethod = IDMServiceGrpc.getDeleteUserMethod) == null) {
          IDMServiceGrpc.getDeleteUserMethod = getDeleteUserMethod = 
              io.grpc.MethodDescriptor.<IdmService.TokenRequest, IdmService.StringResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "IDMService", "DeleteUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.TokenRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.StringResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IDMServiceMethodDescriptorSupplier("DeleteUser"))
                  .build();
          }
        }
     }
     return getDeleteUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<IdmService.UserChangePasswordRequest,
      IdmService.TokenResponse> getChangePasswordMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ChangePassword",
      requestType = IdmService.UserChangePasswordRequest.class,
      responseType = IdmService.TokenResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<IdmService.UserChangePasswordRequest,
      IdmService.TokenResponse> getChangePasswordMethod() {
    io.grpc.MethodDescriptor<IdmService.UserChangePasswordRequest, IdmService.TokenResponse> getChangePasswordMethod;
    if ((getChangePasswordMethod = IDMServiceGrpc.getChangePasswordMethod) == null) {
      synchronized (IDMServiceGrpc.class) {
        if ((getChangePasswordMethod = IDMServiceGrpc.getChangePasswordMethod) == null) {
          IDMServiceGrpc.getChangePasswordMethod = getChangePasswordMethod = 
              io.grpc.MethodDescriptor.<IdmService.UserChangePasswordRequest, IdmService.TokenResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(
                  "IDMService", "ChangePassword"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.UserChangePasswordRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  IdmService.TokenResponse.getDefaultInstance()))
                  .setSchemaDescriptor(new IDMServiceMethodDescriptorSupplier("ChangePassword"))
                  .build();
          }
        }
     }
     return getChangePasswordMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static IDMServiceStub newStub(io.grpc.Channel channel) {
    return new IDMServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static IDMServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new IDMServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static IDMServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new IDMServiceFutureStub(channel);
  }

  /**
   * <pre>
   * fiecare rpc are un tip de request si un tip de response
   *request si response sunt mesaje (message)
   * in metoda din server se va folosi request-ul pentru a extrage datele necesare
   * la client se primeste tipul de response cu atributele necesare
   * </pre>
   */
  public static abstract class IDMServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void createUser(IdmService.UserCreateRequest request,
        io.grpc.stub.StreamObserver<IdmService.TokenResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getCreateUserMethod(), responseObserver);
    }

    /**
     */
    public void login(IdmService.UserLoginRequest request,
        io.grpc.stub.StreamObserver<IdmService.TokenResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLoginMethod(), responseObserver);
    }

    /**
     */
    public void logout(IdmService.TokenRequest request,
        io.grpc.stub.StreamObserver<IdmService.StringResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getLogoutMethod(), responseObserver);
    }

    /**
     */
    public void authorize(IdmService.TokenRequest request,
        io.grpc.stub.StreamObserver<IdmService.TokenResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getAuthorizeMethod(), responseObserver);
    }

    /**
     */
    public void deleteUser(IdmService.TokenRequest request,
        io.grpc.stub.StreamObserver<IdmService.StringResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getDeleteUserMethod(), responseObserver);
    }

    /**
     */
    public void changePassword(IdmService.UserChangePasswordRequest request,
        io.grpc.stub.StreamObserver<IdmService.TokenResponse> responseObserver) {
      asyncUnimplementedUnaryCall(getChangePasswordMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCreateUserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                IdmService.UserCreateRequest,
                IdmService.TokenResponse>(
                  this, METHODID_CREATE_USER)))
          .addMethod(
            getLoginMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                IdmService.UserLoginRequest,
                IdmService.TokenResponse>(
                  this, METHODID_LOGIN)))
          .addMethod(
            getLogoutMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                IdmService.TokenRequest,
                IdmService.StringResponse>(
                  this, METHODID_LOGOUT)))
          .addMethod(
            getAuthorizeMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                IdmService.TokenRequest,
                IdmService.TokenResponse>(
                  this, METHODID_AUTHORIZE)))
          .addMethod(
            getDeleteUserMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                IdmService.TokenRequest,
                IdmService.StringResponse>(
                  this, METHODID_DELETE_USER)))
          .addMethod(
            getChangePasswordMethod(),
            asyncUnaryCall(
              new MethodHandlers<
                IdmService.UserChangePasswordRequest,
                IdmService.TokenResponse>(
                  this, METHODID_CHANGE_PASSWORD)))
          .build();
    }
  }

  /**
   * <pre>
   * fiecare rpc are un tip de request si un tip de response
   *request si response sunt mesaje (message)
   * in metoda din server se va folosi request-ul pentru a extrage datele necesare
   * la client se primeste tipul de response cu atributele necesare
   * </pre>
   */
  public static final class IDMServiceStub extends io.grpc.stub.AbstractStub<IDMServiceStub> {
    private IDMServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IDMServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IDMServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IDMServiceStub(channel, callOptions);
    }

    /**
     */
    public void createUser(IdmService.UserCreateRequest request,
        io.grpc.stub.StreamObserver<IdmService.TokenResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void login(IdmService.UserLoginRequest request,
        io.grpc.stub.StreamObserver<IdmService.TokenResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void logout(IdmService.TokenRequest request,
        io.grpc.stub.StreamObserver<IdmService.StringResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getLogoutMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void authorize(IdmService.TokenRequest request,
        io.grpc.stub.StreamObserver<IdmService.TokenResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getAuthorizeMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteUser(IdmService.TokenRequest request,
        io.grpc.stub.StreamObserver<IdmService.StringResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getDeleteUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void changePassword(IdmService.UserChangePasswordRequest request,
        io.grpc.stub.StreamObserver<IdmService.TokenResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(getChangePasswordMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   * fiecare rpc are un tip de request si un tip de response
   *request si response sunt mesaje (message)
   * in metoda din server se va folosi request-ul pentru a extrage datele necesare
   * la client se primeste tipul de response cu atributele necesare
   * </pre>
   */
  public static final class IDMServiceBlockingStub extends io.grpc.stub.AbstractStub<IDMServiceBlockingStub> {
    private IDMServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IDMServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IDMServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IDMServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public IdmService.TokenResponse createUser(IdmService.UserCreateRequest request) {
      return blockingUnaryCall(
          getChannel(), getCreateUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public IdmService.TokenResponse login(IdmService.UserLoginRequest request) {
      return blockingUnaryCall(
          getChannel(), getLoginMethod(), getCallOptions(), request);
    }

    /**
     */
    public IdmService.StringResponse logout(IdmService.TokenRequest request) {
      return blockingUnaryCall(
          getChannel(), getLogoutMethod(), getCallOptions(), request);
    }

    /**
     */
    public IdmService.TokenResponse authorize(IdmService.TokenRequest request) {
      return blockingUnaryCall(
          getChannel(), getAuthorizeMethod(), getCallOptions(), request);
    }

    /**
     */
    public IdmService.StringResponse deleteUser(IdmService.TokenRequest request) {
      return blockingUnaryCall(
          getChannel(), getDeleteUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public IdmService.TokenResponse changePassword(IdmService.UserChangePasswordRequest request) {
      return blockingUnaryCall(
          getChannel(), getChangePasswordMethod(), getCallOptions(), request);
    }
  }

  /**
   * <pre>
   * fiecare rpc are un tip de request si un tip de response
   *request si response sunt mesaje (message)
   * in metoda din server se va folosi request-ul pentru a extrage datele necesare
   * la client se primeste tipul de response cu atributele necesare
   * </pre>
   */
  public static final class IDMServiceFutureStub extends io.grpc.stub.AbstractStub<IDMServiceFutureStub> {
    private IDMServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private IDMServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected IDMServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new IDMServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<IdmService.TokenResponse> createUser(
        IdmService.UserCreateRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<IdmService.TokenResponse> login(
        IdmService.UserLoginRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getLoginMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<IdmService.StringResponse> logout(
        IdmService.TokenRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getLogoutMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<IdmService.TokenResponse> authorize(
        IdmService.TokenRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getAuthorizeMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<IdmService.StringResponse> deleteUser(
        IdmService.TokenRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getDeleteUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<IdmService.TokenResponse> changePassword(
        IdmService.UserChangePasswordRequest request) {
      return futureUnaryCall(
          getChannel().newCall(getChangePasswordMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_USER = 0;
  private static final int METHODID_LOGIN = 1;
  private static final int METHODID_LOGOUT = 2;
  private static final int METHODID_AUTHORIZE = 3;
  private static final int METHODID_DELETE_USER = 4;
  private static final int METHODID_CHANGE_PASSWORD = 5;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final IDMServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(IDMServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_USER:
          serviceImpl.createUser((IdmService.UserCreateRequest) request,
              (io.grpc.stub.StreamObserver<IdmService.TokenResponse>) responseObserver);
          break;
        case METHODID_LOGIN:
          serviceImpl.login((IdmService.UserLoginRequest) request,
              (io.grpc.stub.StreamObserver<IdmService.TokenResponse>) responseObserver);
          break;
        case METHODID_LOGOUT:
          serviceImpl.logout((IdmService.TokenRequest) request,
              (io.grpc.stub.StreamObserver<IdmService.StringResponse>) responseObserver);
          break;
        case METHODID_AUTHORIZE:
          serviceImpl.authorize((IdmService.TokenRequest) request,
              (io.grpc.stub.StreamObserver<IdmService.TokenResponse>) responseObserver);
          break;
        case METHODID_DELETE_USER:
          serviceImpl.deleteUser((IdmService.TokenRequest) request,
              (io.grpc.stub.StreamObserver<IdmService.StringResponse>) responseObserver);
          break;
        case METHODID_CHANGE_PASSWORD:
          serviceImpl.changePassword((IdmService.UserChangePasswordRequest) request,
              (io.grpc.stub.StreamObserver<IdmService.TokenResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class IDMServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    IDMServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return IdmService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("IDMService");
    }
  }

  private static final class IDMServiceFileDescriptorSupplier
      extends IDMServiceBaseDescriptorSupplier {
    IDMServiceFileDescriptorSupplier() {}
  }

  private static final class IDMServiceMethodDescriptorSupplier
      extends IDMServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    IDMServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (IDMServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new IDMServiceFileDescriptorSupplier())
              .addMethod(getCreateUserMethod())
              .addMethod(getLoginMethod())
              .addMethod(getLogoutMethod())
              .addMethod(getAuthorizeMethod())
              .addMethod(getDeleteUserMethod())
              .addMethod(getChangePasswordMethod())
              .build();
        }
      }
    }
    return result;
  }
}
