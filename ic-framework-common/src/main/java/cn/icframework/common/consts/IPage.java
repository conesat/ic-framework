package cn.icframework.common.consts;

import lombok.Getter;
import lombok.Setter;

/**
 * 分页参数
 *
 * @author hzl
 * @since 2023/5/20 0020
 */
@Getter
@Setter
public class IPage {
    /**
     * 当前页码，默认1
     */
    private Integer pageIndex = 1;
    /**
     * 每页条数，默认10
     */
    private Integer pageSize = 10;
    /**
     * 总记录数
     */
    private long total = 0;
    /**
     * 总页数
     */
    private long pages = 0;

    /**
     * 获取当前页码，若为null则返回1。
     * @return 当前页码
     */
    public Integer getPageIndex() {
        return pageIndex == null ? 1 : pageIndex;
    }

    /**
     * 获取每页条数，若为null则返回10。
     * @return 每页条数
     */
    public Integer getPageSize() {
        return pageSize == null ? 10 : pageSize;
    }
}
