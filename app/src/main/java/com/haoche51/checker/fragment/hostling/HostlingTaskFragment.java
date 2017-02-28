package com.haoche51.checker.fragment.hostling;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.haoche51.checker.DAO.DataObserver;
import com.haoche51.checker.GlobalData;
import com.haoche51.checker.R;
import com.haoche51.checker.activity.hostling.HostlingDetailActivity;
import com.haoche51.checker.adapter.HostlingTaskAdapter;
import com.haoche51.checker.constants.TaskConstants;
import com.haoche51.checker.custom.HCPullToRefresh;
import com.haoche51.checker.entity.HostlingTaskShortEntity;
import com.haoche51.checker.fragment.CommonBaseFragment;
import com.haoche51.checker.listener.HCTasksWatched;
import com.haoche51.checker.net.HCHttpRequestParam;
import com.haoche51.checker.net.HCHttpResponse;
import com.haoche51.checker.net.HttpConstants;
import com.haoche51.checker.util.JsonParseUtil;
import com.haoche51.checker.net.OKHttpManager;
import com.haoche51.checker.util.ACache;
import com.haoche51.checker.util.DisplayUtils;
import com.haoche51.checker.util.HCActionUtil;
import com.haoche51.checker.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 待整备列表
 * Created by mac on 16/01/12.
 */
public class HostlingTaskFragment extends CommonBaseFragment implements AdapterView.OnItemClickListener, View.OnClickListener, HCPullToRefresh.OnRefreshCallback {

    private HCPullToRefresh mPullToRefresh;
    private ListView mListView;
    private List<HostlingTaskShortEntity> mTasks = new ArrayList<>();
    private HostlingTaskAdapter mAdapter = null;

    private int pageIndex = 0;//当前页面索引
    private ACache mAcache;
    private int mClickIndex;
    private String search;
//  public static String BINDLE_PURCHASE_TASK = "bindle_hostling_task";
    DataObserver mDataObserver = new DataObserver(new Handler()) {
        @Override
        public void onChanged() {
        }

        @Override
        public void onChanged(Bundle data) {
            try {
                if (data != null) {
                    if (mTasks == null || mTasks.isEmpty() || mClickIndex >= mTasks.size()) return;
                    if (data.getBoolean(TaskConstants.BINDLE_DETAIL_TASK)) return;
                    //无需整备、提交整备 移除item
                    if (data.getBoolean(TaskConstants.BINDLE_NO_HOSTLING) || data.getBoolean(TaskConstants.BINDLE_COMMIT_HOSTLING)) {
                        mTasks.remove(mClickIndex);
                    }

                    if (mTasks.size() > TaskConstants.DEFAULT_SHOWTASK_COUNT)
//                        mAcache.put(TaskConstants.ACACHE_HOSTLING_DEFAULT, new HCJsonParse().parseHostlingTaskListToJson(mTasks.subList(0, TaskConstants.DEFAULT_SHOWTASK_COUNT)));//缓存删除过本条数据后的json
                        mAcache.put(TaskConstants.ACACHE_HOSTLING_DEFAULT, JsonParseUtil.toJsonArray(mTasks.subList(0, TaskConstants.DEFAULT_SHOWTASK_COUNT)));//缓存删除过本条数据后的json
                    else
//                        mAcache.put(TaskConstants.ACACHE_HOSTLING_DEFAULT, new HCJsonParse().parseHostlingTaskListToJson(mTasks));//缓存删除过本条数据后的json
                        mAcache.put(TaskConstants.ACACHE_HOSTLING_DEFAULT, JsonParseUtil.toJsonArray(mTasks));//缓存删除过本条数据后的json
                    mAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected int getContentView() {
        return R.layout.fragment_trans_task;
    }

    @Override
    protected void initView(View view) {
        mPullToRefresh = (HCPullToRefresh) view.findViewById(R.id.pull_to_refresh);
        mPullToRefresh.setCanPull(true);
        mPullToRefresh.setOnRefreshCallback(this);
        mListView = mPullToRefresh.getListView();
        mListView.setDivider(getResources().getDrawable(R.color.hc_self_gray_bg));
        mListView.setDividerHeight(DisplayUtils.dip2px(getActivity(), 10));
        mListView.setOnItemClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        //初始化ACache
        if (mAcache == null) {
            mAcache = ACache.get(getActivity());
        }
        //读取ACache中的缓存记录
        String json = mAcache.getAsString(TaskConstants.ACACHE_HOSTLING_DEFAULT);
        if (!TextUtils.isEmpty(json)) {
            this.mTasks.clear();
//            List<HostlingTaskShortEntity> tempList = new HCJsonParse().parseHostlingTaskShortEntitys(json);
            List<HostlingTaskShortEntity> tempList = JsonParseUtil.fromJsonArray(json, HostlingTaskShortEntity.class);
            if (tempList != null)
                this.mTasks.addAll(tempList);
        }
        if (mAdapter == null) {
            mAdapter = new HostlingTaskAdapter(getActivity(), mTasks, R.layout.layout_hostling_item, TaskConstants.REQUEST_HOSTLING_DEFAULT);
        }
        //设置数据
        if (mListView != null) {
            mListView.setAdapter(mAdapter);
        }
        //自动刷新
        mPullToRefresh.setFirstAutoRefresh();
        //设置监听数据变化
        HCTasksWatched.getInstance().registerDataObserver(mDataObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroyView();
        HCTasksWatched.getInstance().UnRegisterDataObserver(mDataObserver);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        mClickIndex = position;
        if (mTasks.size() == 0 || id == -1)
            return;
        Map<String, Object> map = new HashMap<>();
        HostlingTaskShortEntity tempTask=mTasks.get(position);
        map.put("id", tempTask.getStock_id());
//        map.put("status", tempTask.getRepair_status());
        HCActionUtil.launchActivity(GlobalData.context, HostlingDetailActivity.class, map);
    }

    @Override
    public void onHttpComplete(String action, int requestId, HCHttpResponse response, Throwable error) {
        if (action.equals(HttpConstants.ACTION_GET_BACK_REPAIR_LIST)) {
            onListHttpResponse(response, requestId);
        }
    }

    /**
     * 列表页刷新返回解析
     *
     * @param response
     * @param requestId
     */
    private void onListHttpResponse(HCHttpResponse response, int requestId) {
        try {
            dismissLoadingView();
            switch (response.getErrno()) {
                case 0:
//                    List<HostlingTaskShortEntity> viewTaskList = new HCJsonParse().parseHostlingTaskShortEntitys(response.getData());
                    List<HostlingTaskShortEntity> viewTaskList = JsonParseUtil.fromJsonArray(response.getData(), HostlingTaskShortEntity.class);
                    //刷新
                    if (requestId == HttpConstants.GET_LIST_REFRESH) {
                        if (response.getData() != null)
                            mAcache.put(TaskConstants.ACACHE_HOSTLING_DEFAULT, response.getData());//只缓存刷新的数据
                        mTasks.clear();
                        if (viewTaskList != null)
                            mTasks.addAll(viewTaskList);
                    } else {
                        //把后来加载的添加进来
                        if (viewTaskList != null)
                            mTasks.addAll(viewTaskList);
                    }
                    //是否还有更多
                    boolean isNoMoreData = (viewTaskList == null || viewTaskList.isEmpty()) ? true : viewTaskList.size() < TaskConstants.DEFAULT_SHOWTASK_COUNT;
                    mPullToRefresh.setFooterStatus(isNoMoreData);
                    //重置数据
                    mAdapter.notifyDataSetChanged();
                    //当没有数据时显示默认空白页
                    if (mTasks.size() == 0) {
                        mPullToRefresh.hideFooter();
                        showNoDataView(false, getString(R.string.hc_common_result_nodata), this);
                    } else {
                        dismissResultView();//有数据，显示出来数据列表页面
                    }
                    break;
                default:
                    mPullToRefresh.setFooterStatus(false);
                    ToastUtil.showInfo(response.getErrmsg());
                    //无网络视图
                    if (mTasks.size() == 0) {
                        showErrorView(false, this);
                    }
                    break;
            }
            //停止刷新
            mPullToRefresh.finishRefresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        //当空数据列表时会回调此方法
        if (mPullToRefresh != null) {
            dismissResultView();
//            showLoadingView(false);
            onPullDownRefresh();//刷新重新拉取数据
        }
    }

    @Override
    public void doingFilter(Bundle where) {
        search = where.getString("search_field", null);
        onPullDownRefresh();
    }

    @Override
    public void onPullDownRefresh() {
        pageIndex = 0;
        OKHttpManager.getInstance().post(HCHttpRequestParam.getBackRepairList(pageIndex, TaskConstants.REQUEST_HOSTLING_DEFAULT, TaskConstants.DEFAULT_SHOWTASK_COUNT,search), this, HttpConstants.GET_LIST_REFRESH);//下拉刷新
    }

    @Override
    public void onLoadMoreRefresh() {
        OKHttpManager.getInstance().post(HCHttpRequestParam.getBackRepairList(++pageIndex, TaskConstants.REQUEST_HOSTLING_DEFAULT, TaskConstants.DEFAULT_SHOWTASK_COUNT,search), this, HttpConstants.GET_LIST_LOADMORE);//上拉刷新
    }
}