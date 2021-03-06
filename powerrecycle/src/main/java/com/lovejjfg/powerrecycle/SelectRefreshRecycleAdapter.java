/*
 * Copyright (c) 2016.  Joe<lovejjfg@163.com>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lovejjfg.powerrecycle;

import android.util.Log;
import android.view.View;

import com.lovejjfg.powerrecycle.annotation.ChoiceMode;
import com.lovejjfg.powerrecycle.model.ISelect;

import java.util.HashSet;

import static com.lovejjfg.powerrecycle.model.ISelect.SingleMode;

/**
 * Created by Joe on 2016-03-11
 * Email: lovejjfg@gmail.com
 */

/**
 * {@link SelectRefreshRecycleAdapter} impl SelectMode,you can call  {@link #setSelectedMode(int)} to switch {@link ISelect#SingleMode} or {@link ISelect#MultipleMode}
 * and you can decide whether it's enable the longTouch to jump to  SelectMode, you can call {@link #longTouchSelectModeEnable(boolean)} to change ,by the way,the default was disable
 */
public abstract class SelectRefreshRecycleAdapter<T extends ISelect> extends RefreshRecycleAdapter<T> {

    private int currentMode = SingleMode;
    private int prePos;
    private boolean longTouchEnable = false;
    public static boolean isSelectMode;

    public HashSet<T> getSelectedBeans() {
        return selectedBeans;
    }

    private HashSet<T> selectedBeans = new HashSet<>();

    public void updateSelectMode(boolean isSelect) {
        if (isSelectMode != isSelect) {
            isSelectMode = isSelect;
            resetData();
            notifyDataSetChanged();
        }
    }

    public SelectRefreshRecycleAdapter(@ChoiceMode int currentMode, boolean longTouchEnable) {
        this.currentMode = currentMode;
        this.longTouchEnable = longTouchEnable;
    }

    private void resetData() {
        for (ISelect bean : list) {
            bean.setSelected(false);
        }
    }

    public boolean isSelectMode() {
        return isSelectMode;
    }

    public void longTouchSelectModeEnable(boolean longTouchSelectModeEnable) {
        longTouchEnable = longTouchSelectModeEnable;
    }


    public void setSelectedMode(@ChoiceMode int mode) {
        currentMode = mode;
    }

    @Override
    public void performClick(final View itemView, final int position) {
        final T testBean = list.get(position);

        if (isSelectMode) {
            Log.e("TAG", "onViewHolderBind: " + position + "点击了！！");
            boolean selected = !testBean.isSelected();
            testBean.setSelected(selected);
            dispatchSelected(itemView, position, testBean, selected);
            if (currentMode == SingleMode && position != prePos && testBean.isSelected()) {
                list.get(prePos).setSelected(false);
                dispatchSelected(itemView, prePos, testBean, false);
                notifyItemChanged(prePos);
            }
            notifyItemRangeChanged(position, 1);
            prePos = position;
        } else {
            if (clickListener != null) {
                clickListener.onItemClick(itemView, position);
            }
        }
    }

    private void dispatchSelected(View itemView, int position, T testBean, boolean isSelected) {
        if (isSelected) {
            selectedBeans.add(testBean);
        } else {
            selectedBeans.remove(testBean);
            if (selectedListener != null && selectedBeans.isEmpty()) {
                selectedListener.onNothingSelected();
            }
        }
        if (selectedListener != null) {
            selectedListener.onItemSelected(itemView, position, isSelected);
        }
    }

    @Override
    public boolean performLongClick(View itemView, int position) {
        if (longTouchEnable) {
            final T testBean = list.get(position);
            updateSelectMode(true);
            testBean.setSelected(!testBean.isSelected());
            dispatchSelected(itemView, position, testBean, testBean.isSelected());
            notifyItemChanged(position);
            prePos = position;
            return true;
        } else {
            return super.performLongClick(itemView, position);
        }

    }

//    private OnItemSelectedListener selectedListener;

    public void setOnItemSelectListener(OnItemSelectedListener listener) {
        this.selectedListener = listener;
    }
}
