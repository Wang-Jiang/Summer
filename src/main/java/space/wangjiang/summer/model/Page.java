package space.wangjiang.summer.model;

import java.util.List;

/**
 * Created by WangJiang on 2017/9/14.
 * 数据分页
 */
public class Page<M> {

    private boolean isFirstPage;
    private boolean isLastPage;
    private int pageSize;
    private long totalRow;
    private long totalPage;
    private int currentPage;
    private List<M> data;

    public Page(int pageSize, long totalRow, long totalPage, int currentPage, List<M> data) {
        this.isFirstPage = (currentPage == 0);
        this.isLastPage = (currentPage == totalPage);
        this.pageSize = pageSize;
        this.totalRow = totalRow;
        this.totalPage = totalPage;
        this.currentPage = currentPage;
        this.data = data;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalRow() {
        return totalRow;
    }

    public long getTotalPage() {
        return totalPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public List<M> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Page{" +
                "isFirstPage=" + isFirstPage +
                ", isLastPage=" + isLastPage +
                ", pageSize=" + pageSize +
                ", totalRow=" + totalRow +
                ", totalPage=" + totalPage +
                ", currentPage=" + currentPage +
                ", data=" + data +
                '}';
    }
}
