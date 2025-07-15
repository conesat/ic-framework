package cn.icframework.core.common.exception;


import cn.icframework.annotation.Author;
import cn.icframework.core.annotation.ExceptionInfo;
import cn.icframework.core.common.config.IcLogConfig;
import cn.icframework.core.common.bean.Response;
import cn.icframework.core.common.bean.enums.ResponseEnum;
import cn.icframework.core.common.helper.I18N;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 全局异常处理器。
 * <p>
 * 统一处理系统运行时的各种异常，返回标准 API 响应结构，并记录异常日志。
 * </p>
 * @author hzl
 * @since 2020/04/22
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 日志记录器
     */
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 是否正在打印异常队列
     */
    private static boolean onPrint = false;
    /**
     * 异常消息队列
     */
    private static final Queue<ExMsg> exceptionQueue = new LinkedList<>();

    /**
     * 配置参数
     */
    @Resource
    private IcLogConfig icLogConfig;

    /**
     * 处理运行时异常
     * @param e 运行时异常
     * @return 标准响应
     */
    @ExceptionHandler(value = RuntimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<?> runtimeExceptionHandler(RuntimeException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理 IO 异常
     * @param e IO 异常
     * @return 标准响应
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<?> iOExceptionHandler(IOException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理方法参数类型不匹配异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestTypeMismatch(MethodArgumentTypeMismatchException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理参数校验异常
     * @param e 参数校验异常
     * @return 标准响应
     */
    @ExceptionHandler({MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> handleBindException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<ObjectError> allErrors = bindingResult.getAllErrors();
        if (!allErrors.isEmpty()) {
            ObjectError error = allErrors.getFirst();
            String objName = error.getObjectName();
            objName = (String.valueOf(objName.charAt(0))).toUpperCase(Locale.ROOT) + objName.substring(1);
            String emsg = I18N.e(objName, error.getDefaultMessage());
            if (emsg.equals(error.getDefaultMessage())) {
                emsg = ((DefaultMessageSourceResolvable) Objects.requireNonNull(error.getArguments())[0]).getDefaultMessage() + ":" + emsg;
            }
            return getDetailInfo(e, emsg);
        }
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理请求体不可读异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理缺少请求参数异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({MissingServletRequestParameterException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(MissingServletRequestParameterException e) {
        String msg = "参数：" + e.getParameterName() + " 不能为空";//I18N.e(this.getClass(), "MissingServletRequestParameterException");
        return getDetailInfo(null, msg);
    }

    /**
     * 处理请求方法不支持异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(HttpRequestMethodNotSupportedException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理空指针异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({NullPointerException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(NullPointerException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理类未找到异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({ClassNotFoundException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(ClassNotFoundException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理下标越界异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({IndexOutOfBoundsException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(IndexOutOfBoundsException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理非法参数异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(IllegalArgumentException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理业务自定义异常
     * @param e 业务异常
     * @return 标准响应
     */
    @ExceptionHandler({MessageException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMessageException(MessageException e) {
        return getDetailInfo(e.getException(), e.getMessage(), false);
    }

    /**
     * 处理权限不足异常
     * @param e 权限异常
     * @return 标准响应
     */
    @ExceptionHandler({PermissionDeniedException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(PermissionDeniedException e) {
        return getDetailInfoByEnum(e, ResponseEnum.UNAUTHORIZED, false);
    }

    /**
     * 处理非法访问异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({IllegalAccessException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(IllegalAccessException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理未登录异常
     * @param e 未登录异常
     * @return 标准响应
     */
    @ExceptionHandler({NeedLoginException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestUnauthorizedServletRequest(NeedLoginException e) {
        return getDetailInfoByEnum(e, ResponseEnum.NEED_LOGIN, false);
    }

    /**
     * 处理 token 过期异常
     * @param e token 过期异常
     * @return 标准响应
     */
    @ExceptionHandler({TokenOutTimeException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestTokenOutTimeServletRequest(TokenOutTimeException e) {
        return getDetailInfoByEnum(e, ResponseEnum.TOKEN_OUT_TIME, false);
    }

    /**
     * 处理权限拒绝异常
     * @param e 权限异常
     * @return 标准响应
     */
    @ExceptionHandler({PermissionDenyException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestPermissionDenyServletRequest(PermissionDenyException e) {
        return getDetailInfoByEnum(e, ResponseEnum.UNAUTHORIZED, false);
    }

    /**
     * 处理异地登录异常
     * @param e 异地登录异常
     * @return 标准响应
     */
    @ExceptionHandler({OtherLoginException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> OtherLoginException(OtherLoginException e) {
        return getDetailInfoByEnum(e, ResponseEnum.OTHER_LOGIN, false);
    }

    /**
     * 处理系统激活失败异常
     * @param e 激活失败异常
     * @return 标准响应
     */
    @ExceptionHandler(value = SystemActivationFailException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<?> systemActivationFailException(SystemActivationFailException e) {
        return getDetailInfoByEnum(e, ResponseEnum.SYSTEM_OUT_DATE, false);
    }

    /**
     * 处理系统激活过期异常
     * @param e 激活过期异常
     * @return 标准响应
     */
    @ExceptionHandler(value = SystemActivationOutDateException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<?> systemActivationOutDateException(SystemActivationOutDateException e) {
        return getDetailInfoByEnum(e, ResponseEnum.SYSTEM_NOT_ACTIVE, false);
    }

    /**
     * 处理系统未初始化异常
     * @param e 未初始化异常
     * @return 标准响应
     */
    @ExceptionHandler(value = SystemUninitializedException.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<?> systemUninitializedException(SystemUninitializedException e) {
        return getDetailInfoByEnum(e, ResponseEnum.SYSTEM_UNINITIALIZED, false);
    }

    /**
     * 处理算术异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({ArithmeticException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(ArithmeticException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理类转换异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({ClassCastException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(ClassCastException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理文件未找到异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({FileNotFoundException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(FileNotFoundException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理数组存储异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({ArrayStoreException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(ArrayStoreException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理方法未找到异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({NoSuchMethodException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(NoSuchMethodException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理文件结束异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({EOFException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(EOFException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理实例化异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({InstantiationException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(InstantiationException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理中断异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({InterruptedException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(InterruptedException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理克隆异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({CloneNotSupportedException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(CloneNotSupportedException e) {
        return getDetailInfo(e, e.getMessage());
    }

    /**
     * 处理数据完整性异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(DataIntegrityViolationException e) {
        return getDetailInfo(e, "数据库操作异常");
    }

    /**
     * 处理 SQL 完整性约束异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({SQLIntegrityConstraintViolationException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(SQLIntegrityConstraintViolationException e) {
        return getDetailInfo(e, "数据库操作异常");
    }

    /**
     * 处理重复键异常
     * @param e 异常
     * @return 标准响应
     */
    @ExceptionHandler({DuplicateKeyException.class})
    @ResponseStatus(HttpStatus.OK)
    public Response<?> requestMissingServletRequest(DuplicateKeyException e) {
        return getDetailInfo(e, "数据库操作异常：键重复");
    }

    public void printDetailInfo(ExMsg exMsg) {
        String info = "";
        StringBuilder runWay = new StringBuilder();//项目链路跟踪
        String authorName = "";
        StackTraceElement stackTraceElement = null;
        StackTraceElement[] stackTrace = exMsg.getE().getStackTrace();
        if (stackTrace.length > 0) {
            stackTraceElement = stackTrace[0];
            for (StackTraceElement stackTrac : stackTrace) {
                //用于定位本地代码错误
                String PACKAGE_NAME = "com.hg";
                if (stackTrac.getClassName().contains(PACKAGE_NAME)) {
                    runWay.append("\n==>\n类名: ");
                    runWay.append(stackTrac.getClassName());
                    runWay.append("\n方法:");
                    runWay.append(stackTrac.getMethodName());
                    runWay.append("\n行数:");
                    runWay.append(stackTrac.getLineNumber());
                }
            }
            try {
                Class<?> aClass = Class.forName(stackTraceElement.getClassName());
                Author author = aClass.getAnnotation(Author.class);
                if (author != null) {
                    authorName = author.value();
                }
                //java反射机制获取所有方法名
                Method[] declaredMethods = aClass.getDeclaredMethods();
                //遍历循环方法并获取对应的注解名称
                for (Method declaredMethod : declaredMethods) {
                    if (declaredMethod.getName().equals(stackTraceElement.getMethodName())) {
                        ExceptionInfo exceptionInfo = declaredMethod.getAnnotation(ExceptionInfo.class);
                        if (exceptionInfo != null) {
                            info = exceptionInfo.value();
                        } else {
                            break;
                        }
                    }
                }
            } catch (ClassNotFoundException e1) {
                logger.error("无法定位的异常");
                return;
            }
        } else {
            logger.error("无法定位的异常");
            return;
        }
        info = exMsg.getMsg() + ":" + info;
        if (Objects.equals(authorName, "")) {
            authorName = "未知";
        }
        String time = getFormatDate("yyyy-MM-dd HH:mm:ss", exMsg.getD());
        String stringBuilder = "\n时间：" +
                time +
                "\n捕获异常：" +
                info +
                "\n出错的类：" +
                stackTraceElement.getClassName() +
                "\n出错方法：" +
                stackTraceElement.getMethodName() +
                "\n出错行数：第" +
                stackTraceElement.getLineNumber() +
                "行\n类创建者：" +
                authorName +
                "\n错误内容：" +
                exMsg.getE().getMessage() +
                "\n错误跟踪:" +
                runWay;
        logger.error(stringBuilder);
    }

    public static String getFormatDate(String format, long d) {
        Date date = new Date(d);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(date);
    }

    /**
     * 获取报错异常
     *
     * @param e
     * @return
     */
    private Response<?> getDetailInfoByEnum(Exception e, ResponseEnum responseEnum, boolean print) {
        return getDetailInfo(e, responseEnum.text(), responseEnum.code(), null, print);
    }

    private Response<?> getDetailInfoByEnum(Exception e, ResponseEnum responseEnum) {
        return getDetailInfo(e, responseEnum.text(), responseEnum.code(), null, true);
    }

    private Response<?> getDetailInfo(Exception e, String msg, Object data) {
        return getDetailInfo(e, msg, ResponseEnum.FAIL.code(), data, true);
    }

    private Response<?> getDetailInfo(Exception e, String msg, boolean print) {
        return getDetailInfo(e, msg, ResponseEnum.FAIL.code(), null, print);
    }

    private Response<?> getDetailInfo(Exception e, String msg) {
        return getDetailInfo(e, msg, ResponseEnum.FAIL.code(), null, true);
    }

    private <R> Response<R> getDetailInfo(Exception e, String msg, int code, R data, boolean print) {
        if (e != null) {
            if (StringUtils.isEmpty(msg)) {
                msg = e.getMessage();
            }
            if (print && icLogConfig.getPrintError()) {
                exceptionQueue.add(new ExMsg(e, msg));
                if (!onPrint) {
                    Thread printThread = new Thread(new PrintRunnable());
                    printThread.start();
                }
            }

            if (print && icLogConfig.getPrintStackTrace()) {
                logger.error("捕获异常：", e);
            }
        }
        return new Response.Builder<R>().code(code).msg(msg).data(data).build();
    }


    /*
   获取本机网内地址
    */
    public String getHostIP() {
        try {
            //获取所有网络接口
            Enumeration<NetworkInterface> allNetworkInterfaces = NetworkInterface.getNetworkInterfaces();
            //遍历所有网络接口
            while (allNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = allNetworkInterfaces.nextElement();
                //如果此网络接口为 回环接口 或者 虚拟接口(子接口) 或者 未启用 或者 描述中包含VM
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp() || networkInterface.getDisplayName().contains("VM")) {
                    //继续下次循环
                    continue;
                }
                //遍历此接口下的所有IP（因为包括子网掩码各种信息）
                for (Enumeration<InetAddress> inetAddressEnumeration = networkInterface.getInetAddresses(); inetAddressEnumeration.hasMoreElements(); ) {
                    InetAddress inetAddress = inetAddressEnumeration.nextElement();
                    //如果此IP不为空
                    if (inetAddress != null) {
                        //如果此IP为IPV4 则返回
                        if (inetAddress instanceof Inet4Address) {
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
            return null;
        } catch (SocketException e) {
            logger.error("捕获异常：", e);
            return null;
        }
    }

    @Getter
    @Setter
    static class ExMsg {
        private Exception e;
        private String msg;
        private long d = System.currentTimeMillis();

        public ExMsg(Exception e, String msg) {
            this.e = e;
            this.msg = msg;
        }
    }

    class PrintRunnable implements Runnable {
        @Override
        public void run() {
            if (onPrint) return;
            onPrint = true;
            while (!Thread.currentThread().isInterrupted()) {
                while (!exceptionQueue.isEmpty()) {
                    ExMsg exMsg = exceptionQueue.poll();
                    if (exMsg == null)
                        continue;

                    printDetailInfo(exMsg);

                    if (exceptionQueue.isEmpty())
                        onPrint = false;

                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    logger.error("捕获异常：", e);
                }
            }
            onPrint = false;
        }
    }
}
