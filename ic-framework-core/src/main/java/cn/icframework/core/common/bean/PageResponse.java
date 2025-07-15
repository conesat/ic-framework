package cn.icframework.core.common.bean;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页返回数据
 * <p>
 * 用于封装分页查询的结果。
 *
 * @param <T> 数据类型
 * @author hzl
 */
@Getter
@Setter
public class PageResponse<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private int index;
    /**
     * 每页条数
     */
    private int size;
    /**
     * 总记录数
     */
    private long total;
    /**
     * 总页数
     */
    private long pages;
    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 无参构造方法
     */
    public PageResponse() {
    }

    /**
     * 构造方法
     * @param page 分页请求
     * @param records 数据列表
     */
    public PageResponse(PageRequest page, List<T> records) {
        setIndex(page.getPageIndex());
        setSize(page.getPageSize());
        setPages(page.getPages());
        setTotal(page.getTotal());
        setRecords(records);
    }

    /**
     * 获取数据列表，若为null则返回空列表
     * @return 数据列表
     */
    public List<T> getRecords() {
        return records == null ? new ArrayList<>() : records;
    }
}
