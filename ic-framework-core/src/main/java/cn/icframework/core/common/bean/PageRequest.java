package cn.icframework.core.common.bean;

import cn.icframework.common.consts.IPage;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页请求参数
 * <p>
 * 用于描述分页请求的页码、每页数量等信息。
 *
 * @author hzl
 */
@Getter
@Setter
public class PageRequest extends IPage {
    /**
     * 构造方法
     */
    public PageRequest() {
    }

    /**
     * 构造方法
     *
     * @param pageIndex 页码
     * @param pageSize  每页数量
     */
    public PageRequest(Integer pageIndex, Integer pageSize) {
        this.setPageIndex(pageIndex == null ? 1 : pageIndex);
        this.setPageSize(pageSize == null ? 10 : pageSize);
    }

    /**
     * 限制分页最大数量100
     *
     * @param pageSize 每页数量
     */
    public void setPageSize(Integer pageSize) {
        super.setPageSize(Math.min(pageSize, 100));
    }

    /**
     * 查询列表转成分页数据
     *
     * @param data 数据列表
     * @param <T>  数据类型
     * @return 分页响应
     */
    public <T> PageResponse<T> toResponse(List<T> data) {
        return new PageResponse<>(this, data);
    }

    /**
     * 构建分页请求对象
     *
     * @param current  当前页
     * @param pageSize 每页数量
     * @return PageRequest
     */
    public static PageRequest of(Integer current, Integer pageSize) {
        return new PageRequest(current, pageSize);
    }
}
