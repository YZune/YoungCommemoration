package com.suda.yzune.youngcommemoration.adapter;

/**
 * Created by yzune on 2018/2/16.
 */

public interface ItemTouchHelperAdapter {
    //数据交换
    void onItemMove(int fromPosition,int toPosition);
    //数据删除
    void onItemDismiss(int position);
}