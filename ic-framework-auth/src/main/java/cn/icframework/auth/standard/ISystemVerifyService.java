package cn.icframework.auth.standard;


/**
 * 校验系统激活
 *
 * @author hzl
 * @since 2022/6/30
 */
public abstract class ISystemVerifyService {
    public enum StatusEnum {
        /**
         * 未初始化
         */
        UNINITIALIZED,
        /**
         * 成功
         */
        SUCCESS,
        /**
         * 未激活
         */
        NOT_ACTIVE,
        /**
         * 已过期
         */
        OUT_DATE
    }

    private StatusEnum status = null;

    /**
     * 手动刷新系统状态
     *
     * @param status
     */
    public void setStatus(StatusEnum status) {
        this.status = status;
    }

    public StatusEnum getStatus() {
        return status;
    }

    /**
     * 系统是否可用
     */
    public abstract StatusEnum validate();
}
