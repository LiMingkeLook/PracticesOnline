package net.lzzy.practicesonline.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;


/**
 * Created by lzzy_gxy on 2019/3/27.
 * Description:
 */

public abstract class BaseFragment extends Fragment {
    public BaseFragment(){}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getLayoutRes(),null);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        populate();
    }
    /**执行onViewCreated中初始化视图组件、填充数据**/
    protected abstract void populate();

    public abstract int getLayoutRes();


    <T extends View> T find(@IdRes int id){
        return Objects.requireNonNull(getView()).findViewById(id);
    }
    public abstract void search(String kw);
}
